package com.example.jetpackbaseapp.presentation.screens.optimization

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptimizationMenuScreen(
    onNavigate: (String) -> Unit
) {
    val demos = listOf(
        OptimizationDemo(
            title = "1. Hạn chế Đệ quy",
            description = "So sánh Recursive vs Iterative (Fibonacci)",
            icon = Icons.Default.Build,
            route = "recursion_demo",
            color = MaterialTheme.colorScheme.primary
        ),
        OptimizationDemo(
            title = "2. Bộ nhớ đệm (Cache)",
            description = "Memory Cache → Disk Cache → Network",
            icon = Icons.Default.List,
            route = "cache_demo",
            color = MaterialTheme.colorScheme.secondary
        ),
        OptimizationDemo(
            title = "3. Data Type tối ưu",
            description = "Primitive vs Boxed Types, Memory Optimization",
            icon = Icons.Default.Info,
            route = "datatype_demo",
            color = MaterialTheme.colorScheme.tertiary
        ),
        OptimizationDemo(
            title = "4. Tối ưu Nâng cao 🚀",
            description = "Value Classes, Inline, Strong Skipping, Baseline Profile",
            icon = Icons.Default.Build,
            route = "advanced_demo",
            color = MaterialTheme.colorScheme.error
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Optimization Techniques",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📱 Mobile App Optimization",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Demo các kỹ thuật tối ưu mã nguồn Android",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Demo List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(demos) { demo ->
                    OptimizationDemoCard(
                        demo = demo,
                        onClick = { onNavigate(demo.route) }
                    )
                }
            }
        }
    }
}

data class OptimizationDemo(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String,
    val color: androidx.compose.ui.graphics.Color
)

@Composable
fun OptimizationDemoCard(
    demo: OptimizationDemo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = demo.color.copy(alpha = 0.1f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = demo.icon,
                        contentDescription = null,
                        tint = demo.color,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = demo.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = demo.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
