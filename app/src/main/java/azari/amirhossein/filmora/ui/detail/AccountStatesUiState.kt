package azari.amirhossein.filmora.ui.detail

import azari.amirhossein.filmora.models.detail.ResponseAccountStates

sealed class AccountStatesUiState {
    object Loading : AccountStatesUiState()
    data class Success(val data: ResponseAccountStates) : AccountStatesUiState()
    data class Error(val message: String) : AccountStatesUiState()
}