package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.AccountRepository
import azari.amirhossein.filmora.models.acccount.ResponseAccountDetails
import azari.amirhossein.filmora.utils.NetworkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedAccountViewModel @Inject constructor(
    private val repository: AccountRepository,
) : ViewModel(){

    private val _accountDetails = MutableStateFlow<NetworkRequest<ResponseAccountDetails>>(NetworkRequest.Loading())
    val accountDetails: StateFlow<NetworkRequest<ResponseAccountDetails>> = _accountDetails

    init {
        fetchAccountDetails()
    }


    fun fetchAccountDetails() {
        viewModelScope.launch {
            repository.getAccountDetails().collect { result ->
                _accountDetails.value = result
            }
        }
    }

}