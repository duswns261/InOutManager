package com.cret.inoutmanager.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cret.inoutmanager.presentation.ui.navigation.InventoryRoute

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F4EE))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "재고관리", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "이동할 화면을 선택하세요", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        HomeMenuCard(
            title = InventoryRoute.Inbound.title,
            subtitle = "신규 제품을 등록하고 입고 목록을 확인합니다",
            onClick = { onNavigate(InventoryRoute.Inbound.route) }
        )
        HomeMenuCard(
            title = InventoryRoute.Outbound.title,
            subtitle = "제품을 선택해 출고 수량을 처리합니다",
            onClick = { onNavigate(InventoryRoute.Outbound.route) }
        )
        HomeMenuCard(
            title = InventoryRoute.Status.title,
            subtitle = "전체 재고 현황을 확인하고 제품을 삭제합니다",
            onClick = { onNavigate(InventoryRoute.Status.route) }
        )
    }
}

@Composable
private fun HomeMenuCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun HomeScreenPreview() {
    HomeScreen(onNavigate = {})
}
