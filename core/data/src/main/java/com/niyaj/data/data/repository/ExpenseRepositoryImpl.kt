package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.result.ValidationResult
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.ExpenseRepository
import com.niyaj.data.repository.ExpenseValidationRepository
import com.niyaj.database.dao.ExpenseDao
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Expense
import com.niyaj.model.searchExpense
import com.niyaj.data.utils.ExpenseTestTags.EXPENSE_DATE_EMPTY_ERROR
import com.niyaj.data.utils.ExpenseTestTags.EXPENSE_NAME_EMPTY_ERROR
import com.niyaj.data.utils.ExpenseTestTags.EXPENSE_NAME_LENGTH_ERROR
import com.niyaj.data.utils.ExpenseTestTags.EXPENSE_PRICE_EMPTY_ERROR
import com.niyaj.data.utils.ExpenseTestTags.EXPENSE_PRICE_LESS_THAN_TEN_ERROR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class ExpenseRepositoryImpl(
    private val expenseDao: ExpenseDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : ExpenseRepository, ExpenseValidationRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllExpense(searchText: String, givenDate: String): Flow<List<Expense>> {
        return withContext(ioDispatcher) {
            expenseDao.getAllExpense(givenDate).mapLatest { it ->
                it.map {
                    it.asExternalModel()
                }.searchExpense(searchText)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllExpenseName(searchText: String): Flow<List<String>> {
        return withContext(ioDispatcher) {
            expenseDao.getAllExpenseName().mapLatest { list ->
                list.filter { it.contains(searchText, ignoreCase = true) }
            }
        }
    }

    override suspend fun getAllPagingExpenses(
        searchText: String,
        limit: Int,
        offset: Int,
    ): List<Expense> {
        TODO("Not yet implemented")
    }

    override suspend fun findExpenseByNameAndDate(
        expenseName: String,
        expenseDate: String,
    ): Boolean {
        return withContext(ioDispatcher) {
            expenseDao.findExpenseByNameAndDate(expenseName, expenseDate) != null
        }
    }

    override suspend fun getExpenseById(expenseId: Int): Resource<Expense?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(expenseDao.getExpenseById(expenseId)?.asExternalModel())
            }
        } catch (e: Exception) {
            Resource.Error("Unable to get expense details")
        }
    }

    override suspend fun addOrIgnoreExpense(newExpense: Expense): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateName = validateExpenseName(newExpense.expenseName)
                val validateAmount = validateExpenseName(newExpense.expenseAmount)
                val validateDate = validateExpenseName(newExpense.expenseDate)

                val hasError = listOf(validateName, validateAmount, validateDate).any {
                    !it.successful
                }

                if (!hasError) {
                    val result = withContext(ioDispatcher) {
                        expenseDao.insertOrIgnoreExpense(newExpense.toEntity())
                    }

                    Resource.Success(result > 0)
                } else {
                    Resource.Error("Unable to validate expenses")
                }
            }
        } catch (e: Exception) {
            Resource.Error("Unable to add new expense")
        }
    }

    override suspend fun updateExpense(newExpense: Expense): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateName = validateExpenseName(newExpense.expenseName)
                val validateAmount = validateExpenseName(newExpense.expenseAmount)
                val validateDate = validateExpenseName(newExpense.expenseDate)

                val hasError = listOf(validateName, validateAmount, validateDate).any {
                    !it.successful
                }

                if (!hasError) {
                    val result = withContext(ioDispatcher) {
                        expenseDao.updateExpense(newExpense.toEntity())
                    }

                    Resource.Success(result > 0)
                } else {
                    Resource.Error("Unable to validate expenses")
                }
            }
        } catch (e: Exception) {
            Resource.Error("Unable to update expense")
        }
    }

    override suspend fun upsertExpense(newExpense: Expense): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateName = validateExpenseName(newExpense.expenseName)
                val validateAmount = validateExpenseName(newExpense.expenseAmount)
                val validateDate = validateExpenseName(newExpense.expenseDate)

                val hasError = listOf(validateName, validateAmount, validateDate).any {
                    !it.successful
                }

                if (!hasError) {
                    val result = withContext(ioDispatcher) {
                        expenseDao.upsertExpense(newExpense.toEntity())
                    }

                    Resource.Success(result > 0)
                } else {
                    Resource.Error("Unable to validate expenses")
                }
            }
        } catch (e: Exception) {
            Resource.Error("Unable to add or update expense")
        }
    }

    override suspend fun deleteExpense(expenseId: Int): Resource<Boolean> {
        return try {
            val result = withContext(ioDispatcher) {
                expenseDao.deleteExpense(expenseId)
            }

            Resource.Success(result > 0)
        } catch (e: Exception) {
            Resource.Error("Unable to delete expense")
        }
    }

    override suspend fun deleteExpenses(expenseIds: List<Int>): Resource<Boolean> {
        return try {
            val result = withContext(ioDispatcher) {
                expenseDao.deleteExpense(expenseIds)
            }

            Resource.Success(result > 0)
        } catch (e: Exception) {
            Resource.Error("Unable to delete expenses")
        }
    }

    override fun validateExpenseName(expenseName: String): ValidationResult {
        if (expenseName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = EXPENSE_NAME_EMPTY_ERROR
            )
        }

        if (expenseName.length < 3) {
            return ValidationResult(
                successful = false,
                errorMessage = EXPENSE_NAME_LENGTH_ERROR
            )
        }

        return ValidationResult(true)
    }

    override fun validateExpenseDate(expenseDate: String): ValidationResult {
        if (expenseDate.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = EXPENSE_DATE_EMPTY_ERROR
            )
        }

        return ValidationResult(true)
    }

    override fun validateExpenseAmount(expenseAmount: String): ValidationResult {
        if (expenseAmount.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = EXPENSE_PRICE_EMPTY_ERROR
            )
        }

        if (expenseAmount.toInt() < 10) {
            return ValidationResult(
                successful = false,
                errorMessage = EXPENSE_PRICE_LESS_THAN_TEN_ERROR
            )
        }

        return ValidationResult(true)
    }
}