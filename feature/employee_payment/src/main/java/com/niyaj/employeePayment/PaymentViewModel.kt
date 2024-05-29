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

package com.niyaj.employeePayment

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.PaymentRepository
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {

    override var totalItems: List<Int> = emptyList()

    @OptIn(ExperimentalCoroutinesApi::class)
    val payments = snapshotFlow { mSearchText.value }
        .flatMapLatest { it ->
            paymentRepository.getAllEmployeePayments(it)
                .onStart { UiState.Loading }
                .map { items ->
                    totalItems = items.flatMap { payment -> payment.payments.map { it.paymentId } }

                    if (items.all { it.payments.isEmpty() }) {
                        UiState.Empty
                    } else {
                        UiState.Success(items)
                    }
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {
            when (val result = paymentRepository.deletePayments(selectedItems.toList())) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                }

                is Resource.Success -> {
                    mEventFlow.emit(UiEvent.OnSuccess("${selectedItems.size} payments has been deleted"))
                    analyticsHelper.logDeletedPayments(selectedItems.toList())
                }
            }

            mSelectedItems.clear()
        }
    }
}

internal fun AnalyticsHelper.logDeletedPayments(data: List<Int>) {
    logEvent(
        event = AnalyticsEvent(
            type = "employee_payment_deleted",
            extras = listOf(
                AnalyticsEvent.Param("employee_payment_deleted", data.toString()),
            ),
        ),
    )
}