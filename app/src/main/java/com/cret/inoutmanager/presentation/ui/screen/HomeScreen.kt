package com.cret.inoutmanager.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cret.inoutmanager.presentation.ui.navigation.InventoryRoute
import com.cret.inoutmanager.ui.theme.BrandAccent
import com.cret.inoutmanager.ui.theme.BrandBackground
import com.cret.inoutmanager.ui.theme.BrandSurface

/** Home 전체 viewport의 bounds를 조회하기 위한 테스트 전용 tag. */
internal const val HomeScreenViewportTag = "home-screen-viewport"

/** 입고·출고·자재 현황 카드 메뉴 그룹의 bounds를 조회하기 위한 테스트 전용 tag. */
internal const val HomeScreenMenuGroupTag = "home-screen-menu-group"

/** [route]로 이동하는 Home 카드를 찾기 위한 테스트 전용 tag를 만듭니다. */
internal fun homeMenuCardTag(route: String) = "home-menu-card-$route"

/**
 * `이동할 화면을 선택하세요` 안내 문구 없이, 입고 → 출고 → 자재 현황 카드 3개를
 * 하나의 메뉴 그룹으로 묶어 가용 영역(viewport) 세로 중앙에 배치합니다.
 *
 * 메뉴 그룹을 감싸는 [Column]에 현재 viewport 높이를 최소 높이로 지정해, 콘텐츠가
 * viewport보다 작을 때는 중앙에 고정되고 커지면(작은 화면·큰 글꼴) 그만큼 커져 세로
 * 스크롤됩니다. system bar inset은 상위 `InventoryApp`의 `Scaffold`가 이미 적용하므로
 * 여기서는 중복 적용하지 않고 스크롤 시작·끝 안전 여백만 둡니다.
 */
@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBackground)
            .testTag(HomeScreenViewportTag),
    ) {
        val viewportHeight = maxHeight

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .defaultMinSize(minHeight = viewportHeight)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(HomeScreenMenuGroupTag),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                HomeMenuCard(
                    title = InventoryRoute.Inbound.title,
                    subtitle = "신규 제품을 등록하고 입고 목록을 확인합니다",
                    modifier = Modifier.testTag(homeMenuCardTag(InventoryRoute.Inbound.route)),
                    onClick = { onNavigate(InventoryRoute.Inbound.route) }
                )
                HomeMenuCard(
                    title = InventoryRoute.Outbound.title,
                    subtitle = "제품을 선택해 출고 수량을 처리합니다",
                    modifier = Modifier.testTag(homeMenuCardTag(InventoryRoute.Outbound.route)),
                    onClick = { onNavigate(InventoryRoute.Outbound.route) }
                )
                HomeMenuCard(
                    title = InventoryRoute.Status.title,
                    subtitle = "전체 재고 현황을 확인하고 제품을 삭제합니다",
                    modifier = Modifier.testTag(homeMenuCardTag(InventoryRoute.Status.route)),
                    onClick = { onNavigate(InventoryRoute.Status.route) }
                )
            }
        }
    }
}

@Composable
private fun HomeMenuCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrandSurface)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = BrandAccent)
        }
    }
}

@Preview(name = "기본", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun HomeScreenPreview() {
    HomeScreen(onNavigate = {})
}

@Preview(name = "작은 높이", showBackground = true, widthDp = 360, heightDp = 320)
@Composable
fun HomeScreenSmallHeightPreview() {
    HomeScreen(onNavigate = {})
}

@Preview(name = "큰 글꼴", showBackground = true, widthDp = 360, heightDp = 640, fontScale = 2f)
@Composable
fun HomeScreenLargeFontPreview() {
    HomeScreen(onNavigate = {})
}
