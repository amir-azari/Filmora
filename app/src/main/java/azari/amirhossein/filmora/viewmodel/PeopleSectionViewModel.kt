package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import azari.amirhossein.filmora.data.repository.PeopleSectionRepository
import azari.amirhossein.filmora.data.repository.TvSectionRepository
import azari.amirhossein.filmora.models.celebtiry.ResponsePeopleType
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
class PeopleSectionViewModel @Inject constructor(
    private val repository: PeopleSectionRepository
) : ViewModel() {

    private val _peoples = MutableStateFlow<NetworkRequest<PagingData<ResponsePeopleType>>>(NetworkRequest.Loading())
    val peoples: StateFlow<NetworkRequest<PagingData<ResponsePeopleType>>> = _peoples

    fun getTvs(sectionType: String): Flow<NetworkRequest<PagingData<ResponsePeopleType>>> {
        viewModelScope.launch {
            _peoples.value = NetworkRequest.Loading()

            repository.getPeoples(sectionType)
                .cachedIn(viewModelScope)
                .collectLatest { result ->
                    _peoples.value = NetworkRequest.Success(result)
                }
        }
        return peoples
    }
}
