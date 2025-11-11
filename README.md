# JetpackBaseApp - MVVM Architecture Guide

A modern Android application built with **Jetpack Compose**, **MVVM architecture**, **Hilt dependency injection**, **Room database**, and **Retrofit** for networking.

## 📚 Table of Contents
- [Project Structure](#project-structure)
- [Architecture Overview](#architecture-overview)
- [Getting Started](#getting-started)
- [How to Add New Features](#how-to-add-new-features)
- [Code Examples](#code-examples)
- [Best Practices](#best-practices)

---

## 📁 Project Structure

```
jetpackbaseapp/
├── data/
│   ├── local/
│   │   ├── dao/              # Database Access Objects
│   │   ├── entity/           # Room entities
│   │   └── database/         # Database class
│   ├── remote/
│   │   ├── api/              # Retrofit API interfaces
│   │   └── dto/              # Data Transfer Objects
│   └── repository/           # Repository implementations
├── domain/
│   ├── model/                # Domain models
│   ├── repository/           # Repository interfaces
│   └── usecase/              # Business logic (optional)
├── di/                       # Dependency Injection modules
├── presentation/
│   ├── screens/              # UI screens
│   │   ├── home/
│   │   ├── detail/
│   │   └── users/
│   ├── navigation/           # Navigation setup
│   ├── components/           # Reusable UI components
│   └── theme/                # Compose theme
└── util/                     # Utility classes
```

---

## 🏗️ Architecture Overview

This project follows **Clean Architecture** principles with **MVVM** pattern:

### Layers:
1. **Presentation Layer** (`presentation/`)
   - UI (Compose screens)
   - ViewModels
   - State management

2. **Domain Layer** (`domain/`)
   - Business models
   - Repository interfaces
   - Use cases (business logic)

3. **Data Layer** (`data/`)
   - Repository implementations
   - API services (Retrofit)
   - Local database (Room)
   - DTOs and Entities

### Data Flow:
```
UI (Compose) → ViewModel → Repository → (API/Database) → ViewModel → UI
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Kotlin 1.9+
- Min SDK: 24 (Android 7.0)
- Target SDK: 36

### Dependencies
The project uses:
- **Jetpack Compose** - Modern UI toolkit
- **Hilt** - Dependency injection
- **Retrofit** - REST API client
- **Room** - Local database
- **Navigation Compose** - Screen navigation
- **Coroutines & Flow** - Asynchronous programming

### Build & Run
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Run the app

---

## 🛠️ How to Add New Features

### 1. Adding a New API Endpoint

#### Step 1: Create DTO (Data Transfer Object)
```kotlin
// data/remote/dto/ProductDto.kt
package com.example.jetpackbaseapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Double
)
```

#### Step 2: Add API method
```kotlin
// data/remote/api/ApiService.kt
interface ApiService {
    // Existing methods...
    
    @GET("products")
    suspend fun getProducts(): Response<List<ProductDto>>
    
    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<ProductDto>
}
```

#### Step 3: Create Domain Model
```kotlin
// domain/model/Product.kt
package com.example.jetpackbaseapp.domain.model

data class Product(
    val id: Int,
    val name: String,
    val price: Double
)
```

#### Step 4: Create Repository Interface
```kotlin
// domain/repository/ProductRepository.kt
package com.example.jetpackbaseapp.domain.repository

import com.example.jetpackbaseapp.domain.model.Product
import com.example.jetpackbaseapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<Resource<List<Product>>>
    fun getProductById(id: Int): Flow<Resource<Product>>
}
```

#### Step 5: Implement Repository
```kotlin
// data/repository/ProductRepositoryImpl.kt
package com.example.jetpackbaseapp.data.repository

import com.example.jetpackbaseapp.data.remote.api.ApiService
import com.example.jetpackbaseapp.domain.model.Product
import com.example.jetpackbaseapp.domain.repository.ProductRepository
import com.example.jetpackbaseapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ApiService
) : ProductRepository {

    override fun getProducts(): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getProducts()
            if (response.isSuccessful) {
                val products = response.body()?.map { dto ->
                    Product(
                        id = dto.id,
                        name = dto.name,
                        price = dto.price
                    )
                } ?: emptyList()
                emit(Resource.Success(products))
            } else {
                emit(Resource.Error("Error: ${response.code()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        } catch (e: IOException) {
            emit(Resource.Error("Connection error: ${e.localizedMessage}"))
        }
    }

    override fun getProductById(id: Int): Flow<Resource<Product>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getProductById(id)
            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) {
                    val product = Product(
                        id = dto.id,
                        name = dto.name,
                        price = dto.price
                    )
                    emit(Resource.Success(product))
                } else {
                    emit(Resource.Error("Product not found"))
                }
            } else {
                emit(Resource.Error("Error: ${response.code()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        } catch (e: IOException) {
            emit(Resource.Error("Connection error: ${e.localizedMessage}"))
        }
    }
}
```

#### Step 6: Register in Hilt Module
```kotlin
// di/RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    // Existing providers...
    
    @Provides
    @Singleton
    fun provideProductRepository(api: ApiService): ProductRepository {
        return ProductRepositoryImpl(api)
    }
}
```

---

### 2. Adding a New Screen

#### Step 1: Create ViewModel
```kotlin
// presentation/screens/products/ProductsViewModel.kt
package com.example.jetpackbaseapp.presentation.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackbaseapp.domain.model.Product
import com.example.jetpackbaseapp.domain.repository.ProductRepository
import com.example.jetpackbaseapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductsState())
    val state: StateFlow<ProductsState> = _state.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            productRepository.getProducts().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = ProductsState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _state.value = ProductsState(
                            products = result.data ?: emptyList()
                        )
                    }
                    is Resource.Error -> {
                        _state.value = ProductsState(
                            error = result.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }
}

data class ProductsState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String = ""
)
```

#### Step 2: Create Screen UI
```kotlin
// presentation/screens/products/ProductsScreen.kt
package com.example.jetpackbaseapp.presentation.screens.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jetpackbaseapp.domain.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel = hiltViewModel(),
    onProductClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Products") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.error.isNotEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.error,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadProducts() }) {
                            Text("Retry")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.products) { product ->
                            ProductItem(
                                product = product,
                                onClick = { onProductClick(product.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$${product.price}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
```

#### Step 3: Add to Navigation
```kotlin
// presentation/navigation/BottomNavItem.kt
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    // Existing items...
    
    object Products : BottomNavItem(
        route = "products",
        title = "Products",
        icon = Icons.Default.ShoppingCart
    )
}
```

```kotlin
// presentation/navigation/BottomNavGraph.kt
@Composable
fun BottomNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Posts.route,
        modifier = modifier
    ) {
        // Existing routes...
        
        composable(route = BottomNavItem.Products.route) {
            ProductsScreen(
                onProductClick = { productId ->
                    // Navigate to product detail
                }
            )
        }
    }
}
```

---

### 3. Adding Room Database Entity

#### Step 1: Create Entity
```kotlin
// data/local/entity/ProductEntity.kt
package com.example.jetpackbaseapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val price: Double,
    val timestamp: Long = System.currentTimeMillis()
)
```

#### Step 2: Create DAO
```kotlin
// data/local/dao/ProductDao.kt
package com.example.jetpackbaseapp.data.local.dao

import androidx.room.*
import com.example.jetpackbaseapp.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductById(productId: Int): Flow<ProductEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
}
```

#### Step 3: Update Database
```kotlin
// data/local/database/AppDatabase.kt
@Database(
    entities = [
        PostEntity::class, 
        UserEntity::class,
        ProductEntity::class  // Add new entity
    ],
    version = 2,  // Increment version
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao  // Add DAO
}
```

#### Step 4: Provide DAO in Hilt
```kotlin
// di/DatabaseModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    // Existing providers...
    
    @Provides
    @Singleton
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }
}
```

#### Step 5: Use in Repository (Offline-First)
```kotlin
// data/repository/ProductRepositoryImpl.kt
class ProductRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val productDao: ProductDao
) : ProductRepository {

    override fun getProducts(): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading())
        
        // Load from cache first
        val localProducts = productDao.getAllProducts().first()
        if (localProducts.isNotEmpty()) {
            emit(Resource.Loading(data = localProducts.map { it.toDomain() }))
        }
        
        // Fetch from network
        try {
            val response = api.getProducts()
            if (response.isSuccessful) {
                val products = response.body()?.map { it.toDomain() } ?: emptyList()
                
                // Save to database
                productDao.insertProducts(products.map { it.toEntity() })
                
                emit(Resource.Success(products))
            } else {
                if (localProducts.isNotEmpty()) {
                    emit(Resource.Success(localProducts.map { it.toDomain() }))
                } else {
                    emit(Resource.Error("Error: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            if (localProducts.isNotEmpty()) {
                emit(Resource.Success(localProducts.map { it.toDomain() }))
            } else {
                emit(Resource.Error(e.localizedMessage ?: "Error"))
            }
        }
    }
}

// Extension functions
fun ProductEntity.toDomain() = Product(id, name, price)
fun Product.toEntity() = ProductEntity(id, name, price)
```

---

## 📖 Code Examples

### State Management Pattern
```kotlin
// Always use StateFlow for UI state
data class ScreenState(
    val isLoading: Boolean = false,
    val data: List<Item> = emptyList(),
    val error: String = ""
)

class ScreenViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    
    private val _state = MutableStateFlow(ScreenState())
    val state: StateFlow<ScreenState> = _state.asStateFlow()
    
    fun loadData() {
        viewModelScope.launch {
            repository.getData().collect { result ->
                _state.value = when (result) {
                    is Resource.Loading -> ScreenState(isLoading = true)
                    is Resource.Success -> ScreenState(data = result.data ?: emptyList())
                    is Resource.Error -> ScreenState(error = result.message ?: "Error")
                }
            }
        }
    }
}
```

### Composable Screen Pattern
```kotlin
@Composable
fun MyScreen(
    viewModel: MyViewModel = hiltViewModel(),
    onNavigate: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = { TopAppBar(title = { Text("Title") }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> LoadingIndicator()
                state.error.isNotEmpty() -> ErrorScreen(state.error)
                else -> ContentScreen(state.data, onNavigate)
            }
        }
    }
}
```

### Repository Pattern
```kotlin
// Always return Flow<Resource<T>>
override fun getData(): Flow<Resource<List<Item>>> = flow {
    emit(Resource.Loading())
    
    try {
        // Load from cache
        val cached = dao.getData().first()
        if (cached.isNotEmpty()) {
            emit(Resource.Loading(data = cached))
        }
        
        // Fetch from network
        val response = api.getData()
        if (response.isSuccessful) {
            val data = response.body() ?: emptyList()
            dao.insert(data)
            emit(Resource.Success(data))
        } else {
            emit(Resource.Error("Error: ${response.code()}"))
        }
    } catch (e: Exception) {
        emit(Resource.Error(e.localizedMessage ?: "Error"))
    }
}
```

---

## ✅ Best Practices

### 1. **Separation of Concerns**
- Keep UI logic in Composables
- Keep business logic in ViewModels
- Keep data logic in Repositories
- Never call API or Database directly from ViewModel

### 2. **Naming Conventions**
- **Screens**: `HomeScreen`, `DetailScreen`
- **ViewModels**: `HomeViewModel`, `DetailViewModel`
- **Repositories**: `PostRepository`, `PostRepositoryImpl`
- **DTOs**: `PostDto`, `UserDto`
- **Entities**: `PostEntity`, `UserEntity`
- **Domain Models**: `Post`, `User`

### 3. **State Management**
- Use `StateFlow` for UI state
- Use `MutableStateFlow` internally
- Expose immutable `StateFlow` publicly
- Always handle Loading/Success/Error states

### 4. **Error Handling**
- Always wrap API calls in try-catch
- Handle `HttpException` for network errors
- Handle `IOException` for connection errors
- Show user-friendly error messages

### 5. **Offline-First**
- Load from database first (instant UI)
- Then fetch from network
- Save network response to database
- Fallback to cache on network failure

### 6. **Dependency Injection**
- Use constructor injection with `@Inject`
- Mark classes with `@HiltViewModel` or `@Singleton`
- Create modules for providing dependencies
- Never use static or global variables

### 7. **Navigation**
- Use type-safe routes with sealed classes
- Pass IDs as navigation arguments
- Use `popBackStack()` for back navigation
- Save state on bottom navigation

### 8. **Compose Best Practices**
- Use `Modifier.fillMaxSize()` for full screen
- Use `Scaffold` for consistent layout
- Extract reusable components
- Use `collectAsState()` for Flow observation

---

## 🔧 Changing API Base URL

Edit `di/NetworkModule.kt`:
```kotlin
private const val BASE_URL = "https://your-api.com/"
```

---

## 📱 Testing the App

### Test Features:
1. **Posts Screen**: Loads posts from JSONPlaceholder API
2. **Users Screen**: Loads users from JSONPlaceholder API
3. **Detail Screen**: Shows post details
4. **Offline Mode**: Turn off internet, app still works with cached data
5. **Bottom Navigation**: Switch between Posts and Users tabs

---

## 🤝 Contributing

When adding new features:
1. Follow the existing folder structure
2. Use the same naming conventions
3. Implement offline-first if using network
4. Handle all loading/error states
5. Update this README with examples

---

## 📄 License

This project is a base template for learning purposes.

---

## 📞 Support

For questions about this architecture:
- Check the code examples above
- Review existing implementations in the project
- Follow Android best practices

---

**Happy Coding! 🚀**
