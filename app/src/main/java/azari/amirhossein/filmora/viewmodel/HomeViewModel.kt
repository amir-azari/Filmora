package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import azari.amirhossein.filmora.data.repository.HomeRepository
import azari.amirhossein.filmora.models.CombinedData
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

    private val _combineData =MutableLiveData<NetworkRequest<CombinedData>>()
    val combineData: LiveData<NetworkRequest<CombinedData>> = _combineData

    private val _randomMoviePoster = MutableLiveData<String?>()
    val randomMoviePoster: LiveData<String?> = _randomMoviePoster

    private val _randomTvPoster = MutableLiveData<String?>()
    val randomTvPoster: LiveData<String?> = _randomTvPoster

    val isNetworkAvailable = networkChecker.startMonitoring()


    fun combinedData() {
        viewModelScope.launch {
            isNetworkAvailable.collect { isAvailable ->
                if (isAvailable) {
                    repository.getCombinedData().collect { result ->
                        _combineData.postValue(result)
                        result.data?.let { selectRandomPosters(it) }
                        filterTrendingMovies(result.data)
                    }
                } else {
                    _combineData.postValue(NetworkRequest.Error(Constants.Message.NO_INTERNET_CONNECTION))
                }
            }
        }
    }
    private fun selectRandomPosters(data: CombinedData?) {
        val baseUrl = Constants.Network.IMAGE_BASE_URL
        val movieResults = data?.recommendedMovies?.data?.results.orEmpty()
        val tvResults = data?.recommendedTvs?.data?.results.orEmpty()

        val randomMoviePoster = movieResults.randomOrNull()?.backdropPath
        val randomTvPoster = tvResults.randomOrNull()?.backdropPath

        _randomMoviePoster.postValue(randomMoviePoster?.let { baseUrl + Constants.ImageSize.ORIGINAL+ it })
        _randomTvPoster.postValue(randomTvPoster?.let { baseUrl + Constants.ImageSize.ORIGINAL+ it })
    }
    private fun filterTrendingMovies(data: CombinedData?) {
        val trendingMovies = data?.trendingMovies?.data?.results.orEmpty()
        val filteredTrendingMovies = trendingMovies
            .filter { it.mediaType == "movie" || it.mediaType == "tv" }

        data?.trendingMovies?.data?.results = filteredTrendingMovies
    }
}


