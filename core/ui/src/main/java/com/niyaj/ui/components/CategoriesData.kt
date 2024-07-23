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

package com.niyaj.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.model.Category
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CategoryPreviewData
import com.niyaj.ui.parameterProvider.CategoryPreviewParameter
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.TrackScrollJank
import kotlinx.collections.immutable.ImmutableList

const val CATEGORY_ITEM_TAG = "Category-"

@Composable
fun TwoColumnLazyRowList(
    modifier: Modifier = Modifier,
    uiState: UiState<List<Category>>,
    selectedCategory: Int,
    onSelect: (Int) -> Unit,
    lazyRowState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
) = trace("CategoriesData") {
    Crossfade(
        targetState = uiState,
        label = "CategoryData::State",
    ) { state ->
        when (state) {
            is UiState.Success -> {
                TrackScrollJank(scrollableState = lazyRowState, stateName = "category:list")

                LazyHorizontalStaggeredGrid(
                    modifier = modifier.height(90.dp),
                    rows = StaggeredGridCells.Fixed(2),
                    state = lazyRowState,
                    verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                    horizontalItemSpacing = SpaceSmall,
                    contentPadding = PaddingValues(start = SpaceSmall),
                ) {
                    items(
                        items = state.data,
                        key = { it.categoryId },
                    ) {
                        CategoryData(
                            modifier = Modifier,
                            item = it,
                            selected = selectedCategory == it.categoryId,
                            onClick = {
                                onSelect(it.categoryId)
                            },
                        )
                    }
                }
            }

            else -> Unit
        }
    }
}

@Composable
fun CategoryList(
    modifier: Modifier = Modifier,
    lazyRowState: LazyListState = rememberLazyListState(),
    categories: ImmutableList<Category>,
    doesSelected: (Int) -> Boolean,
    onSelect: (Int) -> Unit,
) = trace("CategoriesData") {
    TrackScrollJank(scrollableState = lazyRowState, stateName = "category:list")

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        state = lazyRowState,
    ) {
        items(
            items = categories,
            key = {
                it.categoryId
            },
        ) { category ->
            CategoryData(
                item = category,
                selected = doesSelected(category.categoryId),
                onClick = { onSelect(category.categoryId) },
            )
        }
    }
}

@Composable
private fun CategoryData(
    modifier: Modifier = Modifier,
    item: Category,
    selected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    unselectedColor: Color = MaterialTheme.colorScheme.background,
) = trace("CategoryData") {
    val color = if (selected) selectedColor else unselectedColor

    ElevatedCard(
        modifier = modifier
            .testTag(CATEGORY_ITEM_TAG.plus(item.categoryId)),
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors().copy(
            containerColor = color,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularBox(
                icon = PoposIcons.Category,
                doesSelected = selected,
                size = 25.dp,
                text = item.categoryName,
            )

            Spacer(modifier = Modifier.width(SpaceSmallMax))

            Text(
                text = item.categoryName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun CategoryListPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        CategoryList(
            modifier = modifier,
            categories = CategoryPreviewData.categories,
            doesSelected = { false },
            onSelect = {},
        )
    }
}

@DevicePreviews
@Composable
private fun CategoryDataPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        CategoryData(
            modifier = modifier,
            item = CategoryPreviewData.categoryList.first(),
            selected = false,
            onClick = {},
        )
    }
}

@DevicePreviews
@Composable
private fun TwoColumnLazyRowListPreview(
    @PreviewParameter(CategoryPreviewParameter::class)
    uiState: UiState<List<Category>>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        TwoColumnLazyRowList(
            modifier = modifier,
            uiState = uiState,
            selectedCategory = 0,
            onSelect = {},
        )
    }
}
