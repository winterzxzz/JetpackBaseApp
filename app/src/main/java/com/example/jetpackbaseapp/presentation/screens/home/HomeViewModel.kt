package com.example.jetpackbaseapp.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackbaseapp.domain.model.Post
import com.example.jetpackbaseapp.domain.repository.PostRepository
import com.example.jetpackbaseapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            postRepository.getPosts().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = HomeState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _state.value = HomeState(posts = result.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        _state.value = HomeState(error = result.message ?: "Unknown error")
                    }
                }
            }
        }
    }
}

data class HomeState(
    val isLoading: Boolean = false,
    val posts: List<Post> = emptyList(),
    val error: String = ""
)
