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
import com.example.jetpackbaseapp.domain.model.DataTypeComparison

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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Chạy test")
                }
            }

            items(state.primitiveComparisons) { comparison ->
                ComparisonCard(comparison)
            }

            // Collections
            item {
                Spacer(Modifier.height(8.dp))
                Text("2. Collections", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = { viewModel.compareCollections() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Chạy test")
                }
            }

            items(state.collectionComparisons) { comparison ->
                ComparisonCard(comparison)
            }

            // Strings
            item {
                Spacer(Modifier.height(8.dp))
                Text("3. String Operations", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = { viewModel.compareStringOperations() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Chạy test")
                }
            }

            items(state.stringComparisons) { comparison ->
                ComparisonCard(comparison)
            }

            // Error message
            if (state.error.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        state.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun ComparisonCard(comparison: DataTypeComparison) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                comparison.typeName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("⏱ ${formatTime(comparison.operationTimeNs)}", style = MaterialTheme.typography.bodySmall)
                if (comparison.memoryBytes > 0) {
                    Text("💾 ${formatMemory(comparison.memoryBytes)}", style = MaterialTheme.typography.bodySmall)
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

private fun formatMemory(bytes: Long): String {
    return when {
        bytes < 1024 -> "${bytes}B"
        bytes < 1024 * 1024 -> "${bytes / 1024}KB"
        else -> "${bytes / (1024 * 1024)}MB"
    }
}

