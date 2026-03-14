package com.example.despensacuartel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.despensacuartel.data.model.Category
import com.example.despensacuartel.data.model.SectionColor
import com.example.despensacuartel.ui.navigation.AppNavigation
import com.example.despensacuartel.ui.theme.AppBackground
import com.example.despensacuartel.ui.theme.AppColors
import com.example.despensacuartel.ui.theme.DespensaCuartelTheme
import com.example.despensacuartel.ui.viewmodel.InventoryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DespensaCuartelTheme {
                val viewModel: InventoryViewModel = viewModel(
                    factory = InventoryViewModel.Factory(useDummyData = true)
                )
                val uiState by viewModel.uiState.collectAsState()

                PantallaPrincipal(
                    uiState = uiState,
                    getSectionColors = { viewModel.getSectionColors() },
                    getItemsByCategory = viewModel::getItemsByCategory,
                    getItemById = viewModel::getItemById,
                    onQuantityChange = viewModel::updateItemQuantity
                )
            }
        }
    }
}

@Composable
fun PantallaPrincipal(
    uiState: com.example.despensacuartel.ui.viewmodel.InventoryUiState,
    getSectionColors: () -> Map<Category, List<SectionColor>>,
    getItemsByCategory: (String) -> List<com.example.despensacuartel.data.model.InventoryItem>,
    getItemById: (String) -> com.example.despensacuartel.data.model.InventoryItem?,
    onQuantityChange: (String, Int) -> Unit
) {
    AppBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        color = AppColors.Primary
                    )
                }
                uiState.error != null -> {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = AppColors.StatusVeryLow
                    )
                }
                else -> {
                    AppNavigation(
                        sectionColors = getSectionColors(),
                        getItemsByCategory = getItemsByCategory,
                        getItemById = getItemById,
                        onQuantityChange = onQuantityChange
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Pantalla Principal",
    showBackground = true,
    backgroundColor = 0xFFE8E8E8,
    showSystemUi = true,
    device = "id:pixel_4"
)
@Composable
fun MiDespensaPreview() {
    DespensaCuartelTheme {
        val dummyColors = Category.entries.associateWith { listOf(SectionColor.Full, SectionColor.Full, SectionColor.Full, SectionColor.Full) }
        AppNavigation(
            sectionColors = dummyColors,
            getItemsByCategory = { emptyList() },
            getItemById = { null },
            onQuantityChange = { _, _ -> }
        )
    }
}
