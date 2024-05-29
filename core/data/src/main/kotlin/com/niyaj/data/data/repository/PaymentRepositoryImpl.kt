/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.PaymentScreenTags
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.PaymentRepository
import com.niyaj.data.repository.validation.PaymentValidationRepository
import com.niyaj.database.dao.PaymentDao
import com.niyaj.database.model.EmployeeWithPaymentCrossRef
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeWithPayments
import com.niyaj.model.Payment
import com.niyaj.model.PaymentMode
import com.niyaj.model.PaymentType
import com.niyaj.model.searchEmployeeWithPayments
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentRepositoryImpl(
    private val paymentDao: PaymentDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : PaymentRepository, PaymentValidationRepository {

    override fun getAllEmployee(): Flow<List<Employee>> {
        return paymentDao.getAllEmployee().mapLatest { list ->
            list.map {
                it.asExternalModel()
            }
        }
    }

    override suspend fun getEmployeeById(employeeId: Int): Employee? {
        return withContext(ioDispatcher) {
            paymentDao.getEmployeeById(employeeId)?.asExternalModel()
        }
    }

    override suspend fun getAllEmployeePayments(searchText: String): Flow<List<EmployeeWithPayments>> {
        return withContext(ioDispatcher) {
            paymentDao.getAllEmployeePayment().mapLatest { list ->
                list.filter { it.payments.isNotEmpty() }
                    .map { it.asExternalModel() }
                    .searchEmployeeWithPayments(searchText)
            }
        }
    }

    override suspend fun getPaymentById(paymentId: Int): Resource<Payment?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(paymentDao.getPaymentById(paymentId)?.asExternalModel())
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun upsertPayment(newPayment: Payment): Resource<Boolean> {
        return try {
            val validateEmployee = validateEmployee(newPayment.employeeId)
            val validateGivenDate = validateGivenDate(newPayment.paymentDate)
            val validatePaymentType = validatePaymentType(newPayment.paymentType)
            val validateSalary = validateGivenAmount(newPayment.paymentAmount)
            val validateSalaryNote = validatePaymentNote(
                paymentNote = newPayment.paymentNote,
                isRequired = newPayment.paymentMode == PaymentMode.Both,
            )
            val validatePaymentMode = validatePaymentMode(newPayment.paymentMode)

            val hasError = listOf(
                validateEmployee,
                validateSalary,
                validateSalaryNote,
                validatePaymentMode,
                validatePaymentType,
                validateGivenDate,
            ).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher) {
                    val result = withContext(ioDispatcher) {
                        paymentDao.upsertPayment(newPayment.toEntity())
                    }

                    if (result > 0) {
                        paymentDao.upsertEmployeeWithPaymentCrossReference(
                            EmployeeWithPaymentCrossRef(newPayment.employeeId, result.toInt()),
                        )
                    }

                    Resource.Success(result > 0)
                }
            } else {
                Resource.Error("Unable to validate employee payment")
            }
        } catch (e: Exception) {
            Resource.Error("Unable to add or update employee payment")
        }
    }

    override suspend fun deletePayments(paymentIds: List<Int>): Resource<Boolean> {
        return try {
            val result = withContext(ioDispatcher) {
                paymentDao.deletePayments(paymentIds)
            }

            Resource.Success(result > 0)
        } catch (e: Exception) {
            Resource.Error("Unable to delete employee payments")
        }
    }

    override fun validateEmployee(employeeId: Int): ValidationResult {
        if (employeeId == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = PaymentScreenTags.PAYMENT_EMPLOYEE_NAME_EMPTY,
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override fun validateGivenDate(givenDate: String): ValidationResult {
        if (givenDate.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = PaymentScreenTags.PAYMENT_GIVEN_DATE_EMPTY,
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override fun validatePaymentMode(paymentMode: PaymentMode): ValidationResult {
        if (paymentMode.name.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = PaymentScreenTags.PAYMENT_MODE_EMPTY,
            )
        }

        return ValidationResult(true)
    }

    override fun validateGivenAmount(salary: String): ValidationResult {
        if (salary.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = PaymentScreenTags.GIVEN_AMOUNT_EMPTY,
            )
        }

        if (salary.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = PaymentScreenTags.GIVEN_AMOUNT_LENGTH_ERROR,
            )
        }

        if (salary.any { !it.isDigit() }) {
            return ValidationResult(
                successful = false,
                errorMessage = PaymentScreenTags.GIVEN_AMOUNT_LETTER_ERROR,
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override fun validatePaymentNote(paymentNote: String, isRequired: Boolean): ValidationResult {
        if (isRequired) {
            if (paymentNote.isEmpty()) {
                return ValidationResult(
                    successful = false,
                    errorMessage = PaymentScreenTags.PAYMENT_NOTE_EMPTY,
                )
            }
        }

        return ValidationResult(true)
    }

    override fun validatePaymentType(paymentType: PaymentType): ValidationResult {
        if (paymentType.name.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = PaymentScreenTags.PAYMENT_TYPE_EMPTY,
            )
        }

        return ValidationResult(true)
    }

    override suspend fun importPaymentsToDatabase(payments: List<EmployeeWithPayments>): Resource<Boolean> {
        try {
            payments.forEach { empWithPayment ->
                val findEmployee = withContext(ioDispatcher) {
                    paymentDao.findEmployeeByName(
                        empWithPayment.employee.employeeName,
                        empWithPayment.employee.employeeId,
                    )
                }

                if (findEmployee != null) {
                    empWithPayment.payments.forEach { payment ->
                        upsertPayment(payment)
                    }
                } else {
                    val result = withContext(ioDispatcher) {
                        paymentDao.upsertEmployee(empWithPayment.employee.toEntity())
                    }

                    if (result > 0) {
                        empWithPayment.payments.forEach { payment ->
                            upsertPayment(payment)
                        }
                    } else {
                        return Resource.Error("Something went wrong inserting employee!")
                    }
                }
            }

            return Resource.Success(true)
        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Unable to add or update absent entry.")
        }
    }
}