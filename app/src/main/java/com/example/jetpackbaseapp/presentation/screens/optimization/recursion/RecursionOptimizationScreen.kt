package com.example.jetpackbaseapp.presentation.screens.optimization.recursion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecursionOptimizationScreen(
    viewModel: RecursionOptimizationViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var inputN by remember { mutableStateOf("40") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đệ quy vs Vòng lặp") },
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
            OutlinedTextField(
                value = inputN,
                onValueChange = { inputN = it },
                label = { Text("Nhập n") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val n = inputN.toIntOrNull() ?: 10
                    viewModel.calculateFibonacci(n)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text(if (state.isLoading) "Đang tính..." else "Tính Fibonacci")
            }

            if (state.error.isNotEmpty()) {
                 Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        state.error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            if (state.results.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Kết quả n=${state.selectedN}:",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(8.dp))

                        state.results.forEach { result ->
                            if (result.stackOverflow) {
                                Text(
                                    "• ${result.method}: Quá lớn (Stack Overflow)",
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Text("• ${result.method}: ${result.value} (${formatNanosToText(result.executionTimeNs)})")
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        val iterative = state.results.find { it.method.contains("Iterative") }
                        val recursive =
                            state.results.find { it.method.contains("Recursive") && !it.stackOverflow }
                        if (iterative != null && recursive != null && recursive.executionTimeNs > 0 && iterative.executionTimeNs > 0) {
                            val speedup = recursive.executionTimeNs.toDouble() / iterative.executionTimeNs.coerceAtLeast(1).toDouble()
                        }
                    }
                }
            }
        }
    }
}

private fun formatNanosToText(nanos: Long): String {
    return when {
        nanos < 1000 -> "$nanos ns"
        nanos < 1_000_000 -> String.format(Locale.US, "%.1f µs", nanos / 1000.0)
        else -> String.format(Locale.US, "%.2f ms", nanos / 1_000_000.0)
    }
}
