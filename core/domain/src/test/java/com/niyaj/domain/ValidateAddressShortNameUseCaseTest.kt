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

package com.niyaj.domain

import com.niyaj.common.tags.AddressTestTags
import com.niyaj.domain.address.ValidateAddressShortNameUseCase
import com.niyaj.testing.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import kotlin.test.Test

class ValidateAddressShortNameUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()
    private val validateAddressShortNameUseCase = ValidateAddressShortNameUseCase()

    @Test
    fun `empty short name returns error`() {
        val result = validateAddressShortNameUseCase("")
        assertFalse(result.successful)
        assertEquals(AddressTestTags.ADDRESS_SHORT_NAME_EMPTY_ERROR, result.errorMessage)
    }

    @Test
    fun `short name less than 2 characters returns error`() {
        val result = validateAddressShortNameUseCase("A")
        assertFalse(result.successful)
        assertEquals(AddressTestTags.ADDRESS_S_NAME_LESS_THAN_TWO_ERROR, result.errorMessage)
    }

    @Test
    fun `short name with 2 characters is valid`() {
        val result = validateAddressShortNameUseCase("AB")
        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `short name with more than 2 characters is valid`() {
        val result = validateAddressShortNameUseCase("ABC")
        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }
}