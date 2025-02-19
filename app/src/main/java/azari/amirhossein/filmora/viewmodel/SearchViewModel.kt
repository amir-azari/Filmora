package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import azari.amirhossein.filmora.data.repository.SearchRepository
import azari.amirhossein.filmora.models.search.SearchResultPageData
import azari.amirhossein.filmora.models.celebtiry.ResponsePopularCelebrity
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkChecker
import azari.amirhossein.filmora.utils.NetworkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val networkChecker: NetworkChecker
    ) : ViewModel() {


    private val _query = MutableStateFlow("")

    private val _searchAllResults = MutableStateFlow<NetworkRequest<SearchResultPageData>?>(null)
    val searchAllResults: StateFlow<NetworkRequest<SearchResultPageData>?> = _searchAllResults.asStateFlow()

    private val _searchMoviesResults = MutableStateFlow<PagingData<ResponseMoviesList.Result>>(PagingData.empty())
    val searchMoviesResults: StateFlow<PagingData<ResponseMoviesList.Result>> = _searchMoviesResults.asStateFlow()

    private val _searchTvResults = MutableStateFlow<PagingData<ResponseTvsList.Result>>(PagingData.empty())
    val searchTvResults: StateFlow<PagingData<ResponseTvsList.Result>> = _searchTvResults.asStateFlow()

    private val _searchPeopleResults = MutableStateFlow<PagingData<ResponsePopularCelebrity.Result>>(PagingData.empty())
    val searchPeopleResults: StateFlow<PagingData<ResponsePopularCelebrity.Result>> = _searchPeopleResults.asStateFlow()

    private val _cachedResults = MutableStateFlow<PagingData<SearchResultPageData>?>(null)

    private val _visibleTabs = MutableStateFlow<List<Int>>(emptyList())
    val visibleTabs: StateFlow<List<Int>> = _visibleTabs.asStateFlow()

    private val _currentTabPosition = MutableStateFlow(0)
    val currentTabPosition: StateFlow<Int> = _currentTabPosition.asStateFlow()

    init {
        networkChecker.startMonitoring()
    }

    fun search(query: String) {
        if (networkChecker.isNetworkAvailable.value){
            if (_query.value == query && _cachedResults.value != null) {
                return
            }
            _query.value = query
            _searchAllResults.value = NetworkRequest.Loading()

            viewModelScope.launch {
                searchRepository.searchAll(query).collect { result ->
                    when (result) {
                        is NetworkRequest.Success -> {
                            _searchAllResults.value = result
                        }
                        is NetworkRequest.Error -> _searchAllResults.value = result
                        else -> Unit
                    }
                }

            }
            viewModelScope.launch {
                searchRepository.searchMovies(query)
                    .cachedIn(viewModelScope)
                    .collectLatest { _searchMoviesResults.value = it }
            }

            viewModelScope.launch {
                searchRepository.searchTvShows(query)
                    .cachedIn(viewModelScope)
                    .collectLatest { _searchTvResults.value = it }
            }

            viewModelScope.launch {
                searchRepository.searchPeople(query)
                    .cachedIn(viewModelScope)
                    .collectLatest { _searchPeopleResults.value = it }
            }
        }else _searchAllResults.value = NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION)


    }

    fun updateTabPosition(position: Int) {
        _currentTabPosition.value = position
    }
    fun createVisibleTabs(item: SearchResultPageData) {
        val hasMovies = !item.movies?.results.isNullOrEmpty()
        val hasTvShows = !item.tvShows?.results.isNullOrEmpty()
        val hasPeople = !item.people?.results.isNullOrEmpty()

        val tabs = buildList {
            if (hasMovies && hasTvShows && hasPeople) {
                add(0)
            }
            if (hasMovies) add(1)
            if (hasTvShows) add(2)
            if (hasPeople) add(3)
        }
        _visibleTabs.value = tabs
    }

    fun updateAllSearchResults(data: SearchResultPageData) {
        createVisibleTabs(data)
    }
    
    override fun onCleared() {
        super.onCleared()
        networkChecker.stopMonitoring()
    }
}

