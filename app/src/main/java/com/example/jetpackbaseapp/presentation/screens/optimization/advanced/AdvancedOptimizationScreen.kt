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
                title = { Text("Tối ưu Nâng cao") },
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
            // Intro Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🚀 Advanced Optimizations", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Text("• Value Classes: Zero-cost wrappers")
                    Text("• Inline Functions: No lambda overhead")
                    Text("• Strong Skipping: Smart recomposition")
                    Text("• Baseline Profile: AOT compilation")
                }
            }

            // Value Classes Section
            Text("1. Value Classes (@JvmInline)", style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = { viewModel.testValueClasses() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text(if (state.isLoading) "Testing..." else "Test Value Classes")
            }

            state.valueClassResults.forEach { comparison ->
                ValueClassResultCard(comparison)
            }

            // Inline Functions Section
            Text("2. Inline Functions", style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = { viewModel.testInlineFunctions() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text(if (state.isLoading) "Testing..." else "Test Inline Functions")
            }

            state.inlineFunctionResults.forEach { comparison ->
                InlineFunctionResultCard(comparison)
            }

            // Strong Skipping Mode Info
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("3. Strong Skipping Mode ✅", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("Đã bật trong build.gradle.kts:", style = MaterialTheme.typography.bodySmall)
                    Text("composeCompiler {", style = MaterialTheme.typography.bodySmall)
                    Text("  enableStrongSkippingMode = true", 
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary)
                    Text("}", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    Text("💡 Compose tự động skip recomposition cho @Stable/@Immutable composables")
                }
            }

            // Baseline Profile Info
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("4. Baseline Profile ✅", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("File: app/src/main/baseline-prof.txt", style = MaterialTheme.typography.bodySmall)
                    Text("• App startup nhanh hơn ~30%", style = MaterialTheme.typography.bodySmall)
                    Text("• Giảm jank khi chạy lần đầu", style = MaterialTheme.typography.bodySmall)
                    Text("• AOT compile critical paths", style = MaterialTheme.typography.bodySmall)
                }
            }

            // Summary
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📊 Tổng kết:", style = MaterialTheme.typography.titleSmall)
                    Text("✅ Value Classes: Tiết kiệm ~100% memory cho wrappers")
                    Text("✅ Inline Functions: Giảm 50-70% overhead cho HOF")
                    Text("✅ Strong Skipping: Tối ưu recomposition tự động")
                    Text("✅ Baseline Profile: Startup nhanh hơn 30%")
                }
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
private fun ValueClassResultCard(comparison: ValueClassComparison) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(comparison.testName, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("With Value Class:", style = MaterialTheme.typography.bodySmall)
                    Text("${comparison.withValueClass.executionTimeNs / 1000} µs", 
                        color = Color(0xFF4CAF50))
                    Text("Objects: ${comparison.withValueClass.objectsAllocated}")
                    Text("Memory: ${comparison.withValueClass.memoryUsedBytes} B")
                }
                Column {
                    Text("Without:", style = MaterialTheme.typography.bodySmall)
                    Text("${comparison.withoutValueClass.executionTimeNs / 1000} µs", 
                        color = Color(0xFFF44336))
                    Text("Objects: ${comparison.withoutValueClass.objectsAllocated}")
                    Text("Memory: ${comparison.withoutValueClass.memoryUsedBytes} B")
                }
            }
            
            Spacer(Modifier.height(4.dp))
            Text("💾 Tiết kiệm: ${String.format("%.1f", comparison.memoryReduction)}% memory",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
private fun InlineFunctionResultCard(comparison: InlineFunctionComparison) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(comparison.testName, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Inline:", style = MaterialTheme.typography.bodySmall)
                    Text("${comparison.withInline.executionTimeNs / 1000} µs", 
                        color = Color(0xFF4CAF50))
                    Text("Objects: ${comparison.withInline.objectsAllocated}")
                }
                Column {
                    Text("Regular:", style = MaterialTheme.typography.bodySmall)
                    Text("${comparison.withoutInline.executionTimeNs / 1000} µs", 
                        color = Color(0xFFF44336))
                    Text("Objects: ${comparison.withoutInline.objectsAllocated}")
                }
            }
            
            Spacer(Modifier.height(4.dp))
            Text("⚡ Speedup: ${String.format("%.2f", comparison.speedup)}x",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall)
        }
    }
}
