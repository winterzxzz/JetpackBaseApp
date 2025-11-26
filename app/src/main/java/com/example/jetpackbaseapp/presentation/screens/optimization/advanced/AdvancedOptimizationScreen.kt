package com.example.jetpackbaseapp.presentation.screens.optimization.advanced

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedOptimizationScreen(
    viewModel: AdvancedOptimizationViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Value Classes Demo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Value Class ",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            

            
            Button(
                onClick = { viewModel.testValueClass() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text(if (state.isLoading) "Đang chạy test..." else "Chạy Test")
            }

            state.valueClassResult?.let { result ->
                ValueClassResultCard(result)
            }

            if (state.error.isNotEmpty()) {
                Card(colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )) {
                    Text(state.error, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ValueClassResultCard(result: ValueClassResult) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                result.testName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            

            
            // Value Class
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Value Class",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFF4CAF50)
                    )
                    Spacer(Modifier.height(8.dp))
                    
                    Text("Thời gian: ${String.format(Locale.US, "%.2f", result.valueClassTime / 1_000.0)} µs")

                    val memDiff = result.valueClassMemoryAfter - result.valueClassMemory
                    Text("Memory tăng: ${String.format(Locale.US, "%,d", memDiff / 1024)} KB")
                    
                    Text("Objects: ~0 (inlined)")
                }
                
                // Regular Class
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Regular Class",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFFF44336)
                    )
                    Spacer(Modifier.height(8.dp))
                    
                    Text("Thời gian: ${String.format(Locale.US, "%.2f", result.regularClassTime / 1_000.0)} µs")

                    val memDiff = result.regularClassMemoryAfter - result.regularClassMemory
                    Text("Memory tăng: ${String.format(Locale.US, "%,d", memDiff / 1024)} KB")
                    
                    Text("Objects: ~${String.format(Locale.US, "%,d", result.iterations)}")
                }
            }
            
            HorizontalDivider()
            
            // So sánh
            Column {
                Text(
                    "Kết quả:",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                
                val memValue = result.valueClassMemoryAfter - result.valueClassMemory
                val memRegular = result.regularClassMemoryAfter - result.regularClassMemory
                val memSaved = if (memRegular > 0) {
                    ((memRegular - memValue).toDouble() / memRegular * 100)
                } else {
                    0.0
                }
                
                Text(
                    "✓ Value Class tiết kiệm ~${String.format(Locale.US, "%.0f", memSaved)}% bộ nhớ",
                    color = Color(0xFF4CAF50),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                val timeDiff = if (result.valueClassTime > 0) {
                    ((result.regularClassTime - result.valueClassTime).toDouble() / result.valueClassTime * 100)
                } else {
                    0.0
                }

                if (timeDiff > 5) {
                    Text(
                        "✓ Value Class nhanh hơn ~${String.format(Locale.US, "%.0f", timeDiff)}%",
                        color = Color(0xFF4CAF50),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            }
        }
    }
}

