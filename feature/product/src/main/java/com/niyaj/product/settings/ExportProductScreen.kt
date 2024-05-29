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

package com.niyaj.product.settings

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.tags.ProductTestTags
import com.niyaj.common.tags.ProductTestTags.EXPORTED_PRODUCTS_FILE_NAME
import com.niyaj.common.tags.ProductTestTags.EXPORT_PRODUCTS_BTN_TEXT
import com.niyaj.common.tags.ProductTestTags.EXPORT_PRODUCTS_TITLE
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.domain.utils.ImportExport.createFile
import com.niyaj.domain.utils.ImportExport.writeDataAsync
import com.niyaj.product.components.ProductCard
import com.niyaj.product.destinations.AddEditProductScreenDestination
import com.niyaj.ui.components.CategoriesData
import com.niyaj.ui.components.InfoText
import com.niyaj.ui.components.ItemNotAvailableHalf
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardSearchBar
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Destination
@Composable
fun ExportProductScreen(
    navigator: DestinationsNavigator,
    viewModel: ProductSettingsViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val lazyRowState = rememberLazyListState()

    val categories = viewModel.categories.collectAsStateWithLifecycle().value
    val products = viewModel.products.collectAsStateWithLifecycle().value
    val exportedProducts = viewModel.exportedProducts.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()
    val selectedCategory = viewModel.selectedCategory.toList()

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(data.errorMessage)
                }

                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(data.successMessage)
                }
            }
        }
    }

    val context = LocalContext.current

    val hasStoragePermission = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ),
    )

    val askForPermissions = {
        if (!hasStoragePermission.allPermissionsGranted) {
            hasStoragePermission.launchMultiplePermissionRequest()
        }
    }

    val exportLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            it.data?.data?.let {
                scope.launch {
                    val result = writeDataAsync(context, it, exportedProducts)

                    if (result.isSuccess) {
                        resultBackNavigator.navigateBack("${exportedProducts.size} Products has been exported")
                    } else {
                        resultBackNavigator.navigateBack("Unable to export products")
                    }
                }
            }
        }

    fun onBackClick() {
        if (showSearchBar) {
            viewModel.closeSearchBar()
        } else if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else {
            navigator.navigateUp()
        }
    }

    BackHandler {
        onBackClick()
    }

    TrackScreenViewEvent(screenName = "Product Export Screen")

    PoposSecondaryScaffold(
        title = if (selectedItems.isEmpty()) EXPORT_PRODUCTS_TITLE else "${selectedItems.size} Selected",
        showBackButton = selectedItems.isEmpty() || showSearchBar,
        showBottomBar = products.isNotEmpty() && lazyListState.isScrollingUp(),
        navActions = {
            if (showSearchBar) {
                StandardSearchBar(
                    searchText = searchText,
                    placeholderText = "Search for products...",
                    onClearClick = viewModel::clearSearchText,
                    onSearchTextChanged = viewModel::searchTextChanged,
                )
            } else {
                if (products.isNotEmpty()) {
                    IconButton(
                        onClick = viewModel::selectAllItems,
                    ) {
                        Icon(
                            imageVector = PoposIcons.Checklist,
                            contentDescription = Constants.SELECT_ALL_ICON,
                        )
                    }

                    IconButton(
                        onClick = viewModel::openSearchBar,
                        modifier = Modifier.testTag(NAV_SEARCH_BTN),
                    ) {
                        Icon(
                            imageVector = PoposIcons.Search,
                            contentDescription = "Search Icon",
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmallMax),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            ) {
                InfoText(text = "${if (selectedItems.isEmpty()) "All" else "${selectedItems.size}"} products will be exported.")

                StandardButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(ProductTestTags.EXPORT_PRODUCTS_BTN),
                    enabled = true,
                    text = EXPORT_PRODUCTS_BTN_TEXT,
                    icon = PoposIcons.Upload,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                    ),
                    onClick = {
                        scope.launch {
                            askForPermissions()
                            val result = createFile(
                                context = context,
                                fileName = EXPORTED_PRODUCTS_FILE_NAME,
                            )
                            exportLauncher.launch(result)
                            viewModel.onEvent(ProductSettingsEvent.GetExportedProduct)
                        }
                    },
                )
            }
        },
        onBackClick = { onBackClick() },
        fabPosition = FabPosition.End,
        floatingActionButton = {
            ScrollToTop(
                visible = !lazyListState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
            )
        },
        navigationIcon = {
            IconButton(
                onClick = viewModel::deselectItems,
            ) {
                Icon(
                    imageVector = PoposIcons.Close,
                    contentDescription = Constants.CLEAR_ICON,
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            CategoriesData(
                lazyRowState = lazyRowState,
                categories = categories,
                doesSelected = selectedCategory::contains,
                onSelect = {
                    viewModel.onEvent(ProductSettingsEvent.OnSelectCategory(it))
                },
            )

            if (products.isEmpty()) {
                ItemNotAvailableHalf(
                    modifier = Modifier.weight(2f),
                    text = if (searchText.isEmpty()) ProductTestTags.PRODUCT_NOT_AVAILABLE else ProductTestTags.NO_ITEMS_IN_PRODUCT,
                    buttonText = ProductTestTags.CREATE_NEW_PRODUCT,
                    onClick = {
                        navigator.navigate(AddEditProductScreenDestination())
                    },
                )
            } else {
                TrackScrollJank(
                    scrollableState = lazyListState,
                    stateName = "Exported Products::List",
                )

                LazyColumn(
                    state = lazyListState,
                    contentPadding = PaddingValues(SpaceSmall),
                ) {
                    itemsIndexed(
                        items = products,
                        key = { index, item ->
                            item.productName.plus(index).plus(item.productId)
                        },
                    ) { _, item ->
                        ProductCard(
                            item = item,
                            doesSelected = {
                                selectedItems.contains(it)
                            },
                            onClick = viewModel::selectItem,
                            onLongClick = viewModel::selectItem,
                            border = BorderStroke(0.dp, Color.Transparent),
                            showArrow = false,
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        )
                    }
                }
            }
        }
    }
}