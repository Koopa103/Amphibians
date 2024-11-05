package com.example.amphibians.ui.screens

import android.net.http.HttpException
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import androidx.compose.foundation.layout.fillMaxSize
import androidx.lifecycle.viewModelScope
import com.example.amphibians.AmptApplication
import com.example.amphibians.model.Amphibian
import kotlinx.coroutines.launch
import com.example.amphibians.data.AmphibiansRepository

sealed interface AmphibiansUiState {
    data class Success(val amphibians: List<Amphibian>) : AmphibiansUiState
    object Error : AmphibiansUiState
    object Loading : AmphibiansUiState
}

class AmphibiansViewModel(private val amphibiansRepository: AmphibiansRepository) : ViewModel() {

    private val _amphibiansUiState = MutableStateFlow<AmphibiansUiState>(AmphibiansUiState.Loading)
    val amphibiansUiState: StateFlow<AmphibiansUiState> get() = _amphibiansUiState.asStateFlow()

    init {
        getAmphibians()
    }

    fun getAmphibians() {
        viewModelScope.launch {
            _amphibiansUiState.value = AmphibiansUiState.Loading
            _amphibiansUiState.value = try {
                val amphibians = amphibiansRepository.getAmphibians()
                AmphibiansUiState.Success(amphibians)
            } catch (e: IOException) {
                AmphibiansUiState.Error
            } catch (e: HttpException) {
                AmphibiansUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                        as AmptApplication)
                val amphibiansRepository = application.container.amphibiansRepository
                AmphibiansViewModel(amphibiansRepository = amphibiansRepository)
            }
        }
    }
}