package com.example.jetpackbaseapp.presentation.screens.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackbaseapp.domain.model.User
import com.example.jetpackbaseapp.domain.repository.UserRepository
import com.example.jetpackbaseapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UsersState())
    val state: StateFlow<UsersState> = _state.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            userRepository.getUsers().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = UsersState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _state.value = UsersState(users = result.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        _state.value = UsersState(error = result.message ?: "Unknown error")
                    }
                }
            }
        }
    }
}

data class UsersState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val error: String = ""
)
