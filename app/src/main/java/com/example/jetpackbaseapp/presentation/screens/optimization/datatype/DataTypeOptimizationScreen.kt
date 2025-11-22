package com.example.jetpackbaseapp.presentation.screens.optimization.datatype

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataTypeOptimizationScreen(
    viewModel: DataTypeOptimizationViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tối ưu Data Type") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Primitive vs Boxed
            item {
                Text("1. Primitive vs Boxed", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = { viewModel.comparePrimitiveVsBoxed() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    Text("Chạy test")
                }
            }

            items(state.primitiveComparisons) { comparison ->
                ComparisonCard(comparison.typeName, comparison.operationTimeNs, comparison.memoryBytes)
            }

            // Collections
            item {
                Spacer(Modifier.height(8.dp))
                Text("2. Collections", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = { viewModel.compareCollections() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    Text("Chạy test")
                }
            }

            items(state.collectionComparisons) { comparison ->
                ComparisonCard(comparison.typeName, comparison.operationTimeNs, comparison.memoryBytes)
            }

            // Strings
            item {
                Spacer(Modifier.height(8.dp))
                Text("3. String Operations", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = { viewModel.compareStringOperations() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    Text("Chạy test")
                }
            }

            items(state.stringComparisons) { comparison ->
                ComparisonCard(comparison.typeName, comparison.operationTimeNs, comparison.memoryBytes)
            }

            // Kotlin optimizations
            item {
                Spacer(Modifier.height(8.dp))
                Text("4. Kotlin Features", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = { viewModel.compareKotlinOptimizations() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    Text("Chạy test")
                }
            }

            items(state.kotlinComparisons) { comparison ->
                ComparisonCard(comparison.typeName, comparison.operationTimeNs, comparison.memoryBytes)
            }

            // Info
            item {
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("💡 Tips:", style = MaterialTheme.typography.titleSmall)
                        Text("• int > Integer (primitive nhanh hơn)", style = MaterialTheme.typography.bodySmall)
                        Text("• Pre-size collections khi biết size", style = MaterialTheme.typography.bodySmall)
                        Text("• StringBuilder cho string concat", style = MaterialTheme.typography.bodySmall)
                        Text("• Data class cho performance", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonCard(name: String, timeNs: Long, memoryBytes: Long) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(name, style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("⏱ ${formatTime(timeNs)}", style = MaterialTheme.typography.bodySmall)
                if (memoryBytes > 0) {
                    Text("💾 ${memoryBytes}B", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

private fun formatTime(nanos: Long): String {
    return when {
        nanos < 1000 -> "${nanos}ns"
        nanos < 1_000_000 -> "${nanos / 1000}μs"
        nanos < 1_000_000_000 -> "${nanos / 1_000_000}ms"
        else -> "${nanos / 1_000_000_000}s"
    }
}
