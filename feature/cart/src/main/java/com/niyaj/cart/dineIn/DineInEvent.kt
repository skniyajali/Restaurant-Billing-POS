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

package com.niyaj.cart.dineIn

sealed class DineInEvent {

    data class SelectDineInOrder(val orderId: Int) : DineInEvent()

    data object SelectAllDineInOrder : DineInEvent()

    data class IncreaseQuantity(val orderId: Int, val productId: Int) : DineInEvent()

    data class DecreaseQuantity(val orderId: Int, val productId: Int) : DineInEvent()

    data class UpdateAddOnItemInCart(val itemId: Int, val orderId: Int) : DineInEvent()

    data class PlaceDineInOrder(val orderId: Int) : DineInEvent()

    data object PlaceAllDineInOrder : DineInEvent()

    data object RefreshDineInOrder : DineInEvent()
}