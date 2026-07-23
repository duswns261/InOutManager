package com.cret.inoutmanager.presentation.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction

/** 검색 입력 필드를 찾기 위한 테스트 전용 tag. */
internal const val ProductSearchBarFieldTag = "product-search-bar-field"

/** 검색어 지우기 버튼을 찾기 위한 테스트 전용 tag. */
internal const val ProductSearchBarClearButtonTag = "product-search-bar-clear-button"

/**
 * 입고·출고·자재 현황 화면이 공유하는 제품명 검색 입력 UI입니다.
 * 검색어는 호출부(각 화면)가 `rememberSaveable` 지역 상태로 소유하고, 이 컴포저블은 그 값을 그대로
 * 표시·수정하는 역할만 합니다.
 *
 * 공백이 아닌 검색어가 있는 동안에는 시스템 뒤로가기를 먼저 가로채 검색어를 비우고 포커스와 키보드를
 * 닫습니다. 검색어가 비어 있으면 이 [BackHandler]가 비활성화되어 상위 Navigation의 기존 뒤로가기
 * 동작(Home 이동 등)에 그대로 위임됩니다.
 */
@Composable
fun ProductSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    BackHandler(enabled = query.isNotBlank()) {
        onQueryChange("")
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .testTag(ProductSearchBarFieldTag),
        label = { Text("제품명 검색") },
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.testTag(ProductSearchBarClearButtonTag),
                ) {
                    Icon(Icons.Default.Clear, contentDescription = "검색어 지우기")
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            focusManager.clearFocus()
            keyboardController?.hide()
        }),
    )
}
