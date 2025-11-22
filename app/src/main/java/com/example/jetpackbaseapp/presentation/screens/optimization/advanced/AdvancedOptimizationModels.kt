package com.example.jetpackbaseapp.presentation.screens.optimization.advanced

import androidx.compose.runtime.Stable

/**
 * Models cho Advanced Optimizations Demo
 */

@Stable
data class AdvancedOptimizationState(
    val isLoading: Boolean = false,
    val valueClassResults: List<ValueClassComparison> = emptyList(),
    val inlineFunctionResults: List<InlineFunctionComparison> = emptyList(),
    val strongSkippingDemo: StrongSkippingStats = StrongSkippingStats(),
    val error: String = ""
)

data class ValueClassComparison(
    val testName: String,
    val withValueClass: PerformanceMetrics,
    val withoutValueClass: PerformanceMetrics,
    val memoryReduction: Double // Percentage
)

data class InlineFunctionComparison(
    val testName: String,
    val withInline: PerformanceMetrics,
    val withoutInline: PerformanceMetrics,
    val speedup: Double
)

data class PerformanceMetrics(
    val executionTimeNs: Long,
    val objectsAllocated: Long,
    val memoryUsedBytes: Long
)

data class StrongSkippingStats(
    val recompositionsSkipped: Int = 0,
    val recompositionsExecuted: Int = 0,
    val skipRate: Float = 0f
)
