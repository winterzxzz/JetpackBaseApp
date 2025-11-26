package com.example.jetpackbaseapp.presentation.screens.optimization.cache

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jetpackbaseapp.domain.model.CacheSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CacheOptimizationScreen(
    viewModel: CacheOptimizationViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var inputId by remember { mutableStateOf("1") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cache 3 cấp") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = inputId,
                onValueChange = { inputId = it },
                label = { Text("Input ID của Item") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ví dụ: 1, 5, 10, ...") }
            )

            // ==================== HƯỚNG DẪN ====================
            // Database của bạn TRỐNG ban đầu!
            //
            // Lần đầu tiên:
            // ├─ Nhấn "Tải (có Cache)" ID=5
            // ├─ Chương trình tìm: Memory ❌ → Disk ❌ → Network ✅ (~1500ms)
            // ├─ Lưu Item #5 vào: Disk database + Memory cache
            // └─ Hiển thị kết quả
            //
            // Lần thứ 2:
            // ├─ Nhấn "Tải (có Cache)" ID=5 (item đó)
            // ├─ Chương trình tìm: Memory ✅ (~ns) - CỰC NHANH!
            // └─ Không cần fetch network
            //
            // Nếu muốn test nhanh:
            // 1. Nhấn "Preload Data" → Tạo sẵn 10 items trong database
            // 2. Nhập ID=1-10 → Lấy ngay từ Disk (nhanh ~ms)
            // 3. Lấy lần 2 → Memory (cực nhanh ~ns)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val id = inputId.toIntOrNull() ?: 1
                        viewModel.loadDataWithCache(id)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isLoading
                ) {
                    Text(if (state.isLoading) "Đang tải..." else "Tải (có Cache)")
                }

                OutlinedButton(
                    onClick = {
                        val id = inputId.toIntOrNull() ?: 1
                        viewModel.loadDataWithoutCache(id)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isLoading
                ) {
                    Text("Tải (Không Cache)")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.preloadCache(10) },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isLoading
                ) {
                    Text("Preload Data (1-10)")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val id = inputId.toIntOrNull() ?: 1
                        viewModel.loadDataWithCache(id)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isLoading
                ) {
                    Text(if (state.isLoading) "Đang tải..." else "Tải (có Cache)")
                }

                OutlinedButton(
                    onClick = {
                        val id = inputId.toIntOrNull() ?: 1
                        viewModel.loadDataWithoutCache(id)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isLoading
                ) {
                    Text("Tải (Không Cache)")
                }
            }

            if (state.error.isNotEmpty()) {
                Card(
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

            StatusAndHistory(state = state, viewModel = viewModel)
        }
    }
}

@Composable
private fun StatusAndHistory(
    state: CacheState,
    viewModel: CacheOptimizationViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Memory: ${state.memoryCacheSize} | Disk: ${state.diskCacheSize}")
                Row {
                    TextButton(onClick = { viewModel.clearMemoryCache() }) {
                        Text("Xoá Mem")
                    }
                    TextButton(onClick = { viewModel.clearDiskCache() }) {
                        Text("Xoá Disk")
                    }
                }
            }
        }

        if (state.loadHistory.isNotEmpty()) {
            Text("Lịch sử tải:", style = MaterialTheme.typography.titleMedium)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 300.dp)) {
                items(state.loadHistory) { item ->
                    HistoryCard(item)
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(data: com.example.jetpackbaseapp.domain.model.CacheDemo) {
    val (color, label) = when (data.source) {
        CacheSource.MEMORY_CACHE -> Color(0xFF4CAF50) to "MEM"
        CacheSource.DISK_CACHE -> Color(0xFFFF9800) to "DISK"
        CacheSource.NETWORK -> Color(0xFFF44336) to "NET"
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = data.title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(16.dp))
            Text("$label - ${formatNanosToText(data.loadTimeNs)}", color = color, style = MaterialTheme.typography.labelLarge)
        }
    }
}

private fun formatNanosToText(nanos: Long): String {
    return when {
        nanos < 1000 -> "$nanos ns"
        nanos < 1_000_000 -> String.format("%.1f µs", nanos / 1000.0)
        else -> String.format("%.2f ms", nanos / 1_000_000.0)
    }
}
