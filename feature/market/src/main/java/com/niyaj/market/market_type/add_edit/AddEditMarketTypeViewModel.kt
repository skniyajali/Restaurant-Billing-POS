/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.market.market_type.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.data.repository.MarketTypeRepository
import com.niyaj.data.repository.validation.MarketTypeValidationRepository
import com.niyaj.model.MarketType
import com.niyaj.ui.utils.UiEvent
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditMarketTypeViewModel @Inject constructor(
    private val repository: MarketTypeRepository,
    private val validationRepository: MarketTypeValidationRepository,
    private val analyticsHelper: AnalyticsHelper,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val typeId = savedStateHandle.get<Int>("typeId") ?: 0

    var state by mutableStateOf(AddEditMarketTypeState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _listTypes = mutableStateListOf<String>()
    val listTypes: MutableList<String> = _listTypes

    private val _selectedTypes = mutableStateListOf(defaultListTypes.first())
    val selectedTypes: MutableList<String> = _selectedTypes


    init {
        _listTypes.addAll(defaultListTypes)

        savedStateHandle.get<Int>("typeId")?.let {
            if(it != 0) getMarketTypeById(it)
        }

        savedStateHandle.get<String>("typeName")?.let {
            state = state.copy(typeName = it)
        }
    }

    val typeNameError = snapshotFlow { state.typeName }.mapLatest {
        validationRepository.validateTypeName(it, typeId).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    val listNameError = snapshotFlow { state.listType }.mapLatest {
        validationRepository.validateListType(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )


    val listTypesError = snapshotFlow { selectedTypes.size }.mapLatest {
        validationRepository.validateListTypes(selectedTypes.toList()).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun onEvent(event: AddEditMarketTypeEvent) {
        when (event) {
            is AddEditMarketTypeEvent.TypeNameChanged -> {
                state = state.copy(typeName = event.typeName)
            }

            is AddEditMarketTypeEvent.TypeDescChanged -> {
                state = state.copy(typeDesc = event.typeDesc.ifEmpty { null })
            }

            is AddEditMarketTypeEvent.SupplierIdChanged -> {
                state = state.copy(supplierId = event.supplierId)
            }

            is AddEditMarketTypeEvent.ListTypeChanged -> {
                state = state.copy(listType = event.listType.uppercase())
            }

            is AddEditMarketTypeEvent.OnSelectListType -> {
                viewModelScope.launch {
                    if (!listTypes.contains(event.listType)) {
                        listTypes.add(event.listType)
                    }

                    if (_selectedTypes.contains(event.listType)) {
                        _selectedTypes.remove(event.listType)
                    } else {
                        _selectedTypes.add(event.listType)
                    }

                    if (state.listType == event.listType) {
                        state = state.copy(listType = "")
                    }
                }
            }

            is AddEditMarketTypeEvent.SaveMarketType -> {
                createOrUpdateMarketType(typeId)
            }

        }
    }

    private fun createOrUpdateMarketType(typeId: Int) {
        viewModelScope.launch {
            val hasSuccessful = listOf(
                typeNameError,
                listNameError,
                listTypesError,
            ).all { it.value == null }

            if (hasSuccessful) {
                val marketType = MarketType(
                    typeId = typeId,
                    typeName = state.typeName.trim(),
                    typeDesc = state.typeDesc?.trim()?.capitalizeWords,
                    supplierId = state.supplierId,
                    listTypes = selectedTypes.toList(),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = if (typeId == 0) null else System.currentTimeMillis(),
                )
                val result = repository.createOrUpdateMarketType(marketType)
                val message = if (typeId == 0) "created" else "updated"

                when (result) {
                    is Resource.Error -> {
                        _eventFlow.emit(
                            UiEvent.OnError("Unable to $message market type"),
                        )
                    }

                    is Resource.Success -> {
                        _eventFlow.emit(
                            UiEvent.OnSuccess("Market type $message successfully"),
                        )
                        analyticsHelper.logOnCreateOrUpdateMarketType(typeId, message)
                    }
                }
            }
        }
    }

    private fun getMarketTypeById(typeId: Int) {
        viewModelScope.launch {
            repository.getMarketTypeById(typeId)?.let { result ->
                state = state.copy(
                    typeName = result.typeName.uppercase(),
                    typeDesc = result.typeDesc,
                    supplierId = result.supplierId,
                )

                if (result.listTypes.isNotEmpty()) {
                    _selectedTypes.clear()

                    result.listTypes.forEach {
                        if (!listTypes.contains(it)) {
                            listTypes.add(it)
                        }
                    }

                    _selectedTypes.addAll(result.listTypes)
                }
            }
        }
    }
}

internal val defaultListTypes = listOf(
    "NEEDED",
    "IN-STOCK",
    "OUT-OF-STOCK",
)

private fun AnalyticsHelper.logOnCreateOrUpdateMarketType(data: Int, message: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "market_type_$message",
            extras = listOf(
                AnalyticsEvent.Param("market_type_$message", data.toString()),
            ),
        ),
    )
}
