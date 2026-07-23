package com.cret.inoutmanager.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cret.inoutmanager.presentation.ui.navigation.InventoryRoute
import com.cret.inoutmanager.ui.theme.BrandAccent
import com.cret.inoutmanager.ui.theme.BrandSurface

/** 화면 전환 메뉴를 여는 제목 anchor를 찾기 위한 테스트 전용 tag. */
internal const val InventoryTopAppBarTitleTag = "inventory-top-app-bar-title"

/** 입고 전용 등록 버튼을 찾기 위한 테스트 전용 tag. */
internal const val InventoryTopAppBarAddButtonTag = "inventory-top-app-bar-add-button"

/** [route]의 화면 전환 메뉴 항목을 찾기 위한 테스트 전용 tag를 만듭니다. */
internal fun inventoryTopAppBarMenuItemTag(route: String) = "inventory-top-app-bar-menu-item-$route"

/**
 * Home을 제외한 입고·출고·자재 현황 화면이 공유하는 App Bar입니다.
 * 현재 화면명 + 아래 방향 아이콘 전체(최소 48dp)가 메뉴 진입점이며, 누르면 App Bar 바로 아래에
 * 입고 → 출고 → 자재 현황 고정 순서 메뉴가 열립니다. 현재 화면 항목은 굵게 표시되고 체크 아이콘이
 * 붙으며, 재선택하면 메뉴만 닫히고 [onRouteSelected]는 호출되지 않습니다.
 *
 * 메뉴 열림 상태는 [currentRoute]를 key로 하는 지역 상태로만 유지해, 화면 전환 후에는 이전 메뉴가
 * 남지 않고 회전 등 재구성 시에는 같은 화면에서 열려 있던 상태가 그대로 복원됩니다.
 *
 * [currentRoute]가 입고([InventoryRoute.Inbound])일 때만 우측에 등록 진입점을 노출합니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryTopAppBar(
    currentRoute: String?,
    onRouteSelected: (String) -> Unit,
    onAddProductClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var menuExpanded by rememberSaveable(currentRoute) { mutableStateOf(false) }
    val currentFeature = InventoryRoute.featureRoutes[InventoryRoute.indexOfFeature(currentRoute)]

    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Box {
                Row(
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClickLabel = "화면 전환 메뉴 열기") { menuExpanded = true }
                        .testTag(InventoryTopAppBarTitleTag),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = currentFeature.title, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    InventoryRoute.featureRoutes.forEach { route ->
                        val isSelected = route == currentFeature
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = route.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                )
                            },
                            trailingIcon = {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = BrandAccent)
                                }
                            },
                            modifier = Modifier
                                .testTag(inventoryTopAppBarMenuItemTag(route.route))
                                .semantics { if (isSelected) contentDescription = "${route.title}, 선택됨" },
                            onClick = {
                                menuExpanded = false
                                if (route.route != currentRoute) onRouteSelected(route.route)
                            },
                        )
                    }
                }
            }
        },
        actions = {
            if (currentRoute == InventoryRoute.Inbound.route) {
                IconButton(
                    onClick = onAddProductClick,
                    modifier = Modifier.testTag(InventoryTopAppBarAddButtonTag),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "신규 제품 등록", tint = BrandAccent)
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BrandSurface),
    )
}

@Preview(showBackground = true)
@Composable
private fun InventoryTopAppBarInboundPreview() {
    InventoryTopAppBar(
        currentRoute = InventoryRoute.Inbound.route,
        onRouteSelected = {},
        onAddProductClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun InventoryTopAppBarOutboundPreview() {
    InventoryTopAppBar(
        currentRoute = InventoryRoute.Outbound.route,
        onRouteSelected = {},
        onAddProductClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun InventoryTopAppBarStatusPreview() {
    InventoryTopAppBar(
        currentRoute = InventoryRoute.Status.route,
        onRouteSelected = {},
        onAddProductClick = {},
    )
}
