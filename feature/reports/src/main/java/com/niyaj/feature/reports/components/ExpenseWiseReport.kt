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

package com.niyaj.feature.reports.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor3
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.ExpensesReport
import com.niyaj.model.TotalExpenses
import com.niyaj.ui.components.CountBox
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.event.UiState

@Composable
fun ExpenseWiseReport(
    modifier: Modifier = Modifier,
    uiState: UiState<List<ExpensesReport>>,
    totalReports: TotalExpenses,
    doesExpanded: Boolean,
    onExpandChanged: () -> Unit,
    onExpenseClick: (Int) -> Unit = {},
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightColor3,
        ),
    ) {
        StandardExpandable(
            modifier = modifier
                .padding(SpaceSmall)
                .fillMaxWidth(),
            expanded = doesExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                IconWithText(
                    text = "Expenses Report",
                    icon = PoposIcons.Receipt,
                )
            },
            trailing = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpaceMini),
                ) {
                    CountBox(count = totalReports.totalExpenses.toRupee)

                    CountBox(
                        count = totalReports.totalQuantity.toString(),
                        backgroundColor = MaterialTheme.colorScheme.secondary,
                    )
                }
            },
            rowClickable = true,
            expand = null,
            content = {
                when (uiState) {
                    is UiState.Loading -> LoadingIndicator()

                    is UiState.Empty -> {
                        ItemNotAvailable(
                            text = "Expenses not available on selected date.",
                            showImage = false,
                        )
                    }

                    is UiState.Success -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            uiState.data.forEachIndexed { index, report ->
                                ExpensesReportCard(
                                    report = report,
                                    onExpenseClick = onExpenseClick,
                                )

                                // Add Spacer until the last item
                                if (index != uiState.data.size - 1) {
                                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    }
                }
            },
        )
    }
}