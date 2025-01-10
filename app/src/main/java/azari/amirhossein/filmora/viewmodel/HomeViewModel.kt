package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.HomeRepository
import azari.amirhossein.filmora.models.HomePageData
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.NetworkChecker
import azari.amirhossein.filmora.utils.NetworkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    networkChecker: NetworkChecker
) : ViewModel() {

    private val _homePageData =MutableLiveData<NetworkRequest<HomePageData>>()
    val homePageData: LiveData<NetworkRequest<HomePageData>> = _homePageData

    private val _randomMoviePoster = MutableLiveData<String?>()
    val randomMoviePoster: LiveData<String?> = _randomMoviePoster

    private val _randomTvPoster = MutableLiveData<String?>()
    val randomTvPoster: LiveData<String?> = _randomTvPoster

    val isNetworkAvailable = networkChecker.startMonitoring()
    private var isDataLoaded = false


    fun combinedData() {
        if (isDataLoaded) return

        viewModelScope.launch {
            isNetworkAvailable.collect { isAvailable ->
                if (isAvailable) {
                    repository.getRemoteData().collect { result ->
                        _homePageData.postValue(result)
                        result.data?.let { selectRandomPosters(it) }
                        filterTrendingMovies(result.data)
                        isDataLoaded = true
                    }
                } else {
                    _homePageData.postValue(repository.getCachedData())
                }
            }
        }
    }
    private fun selectRandomPosters(data: HomePageData?) {
        val baseUrl = Constants.Network.IMAGE_BASE_URL
        val movieResults = data?.recommendedMovies?.data?.results.orEmpty()
        val tvResults = data?.recommendedTvs?.data?.results.orEmpty()

        val randomMoviePoster = movieResults.randomOrNull()?.backdropPath
        val randomTvPoster = tvResults.randomOrNull()?.backdropPath

        _randomMoviePoster.postValue(randomMoviePoster?.let { baseUrl + Constants.ImageSize.ORIGINAL+ it })
        _randomTvPoster.postValue(randomTvPoster?.let { baseUrl + Constants.ImageSize.ORIGINAL+ it })
    }
    private fun filterTrendingMovies(data: HomePageData?) {
        val trendingMovies = data?.trending?.data?.results.orEmpty()
        val filteredTrendingMovies = trendingMovies
            .filter { it.mediaType == "movie" || it.mediaType == "tv" }

        data?.trending?.data?.results = filteredTrendingMovies
    }
}


