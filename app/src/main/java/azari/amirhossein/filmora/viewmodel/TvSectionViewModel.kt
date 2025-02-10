package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import azari.amirhossein.filmora.data.repository.TvSectionRepository
import azari.amirhossein.filmora.models.tv.ResponseTvType
import azari.amirhossein.filmora.utils.NetworkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvSectionViewModel @Inject constructor(
    private val repository: TvSectionRepository
) : ViewModel() {

    private val _tvs = MutableStateFlow<NetworkRequest<PagingData<ResponseTvType>>>(NetworkRequest.Loading())
    val tvs: StateFlow<NetworkRequest<PagingData<ResponseTvType>>> = _tvs

    fun getTvs(sectionType: String): Flow<NetworkRequest<PagingData<ResponseTvType>>> {
        viewModelScope.launch {
            _tvs.value = NetworkRequest.Loading()

            repository.getMovies(sectionType)
                .cachedIn(viewModelScope)
                .collectLatest { result ->
                    _tvs.value = NetworkRequest.Success(result)
                }
        }
        return tvs
    }
}
