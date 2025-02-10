package azari.amirhossein.filmora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import azari.amirhossein.filmora.data.repository.MovieSectionRepository
import azari.amirhossein.filmora.models.movie.ResponseMovieType
import azari.amirhossein.filmora.utils.NetworkRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieSectionViewModel @Inject constructor(
    private val repository: MovieSectionRepository
) : ViewModel() {

    private val _movies = MutableStateFlow<NetworkRequest<PagingData<ResponseMovieType>>>(NetworkRequest.Loading())
    val movies: StateFlow<NetworkRequest<PagingData<ResponseMovieType>>> = _movies

    fun getMovies(sectionType: String): Flow<NetworkRequest<PagingData<ResponseMovieType>>> {
        viewModelScope.launch {
            _movies.value = NetworkRequest.Loading()

            repository.getMovies(sectionType)
                .cachedIn(viewModelScope)
                .collectLatest { result ->
                    _movies.value = NetworkRequest.Success(result)
                }
        }
        return movies
    }
}
