package com.cret.inoutmanager.presentation.viewmodel

import com.cret.inoutmanager.domain.model.Product

data class InventoryUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
