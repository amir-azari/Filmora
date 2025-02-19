package azari.amirhossein.filmora.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import azari.amirhossein.filmora.data.SessionManager
import azari.amirhossein.filmora.data.source.RemoteDataSource
import azari.amirhossein.filmora.models.search.SearchResultPageData
import azari.amirhossein.filmora.models.celebtiry.ResponsePopularCelebrity
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import azari.amirhossein.filmora.paging.SearchCelebritiesPagingSource
import azari.amirhossein.filmora.paging.SearchMoviesPagingSource
import azari.amirhossein.filmora.paging.SearchTvShowsPagingSource
import azari.amirhossein.filmora.utils.NetworkRequest
import azari.amirhossein.filmora.utils.NetworkResponse
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.zip
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(private val remote: RemoteDataSource, private val sessionManager: SessionManager,
) {

    fun searchAll(query: String): Flow<NetworkRequest<SearchResultPageData>> = flow {
        emit(NetworkRequest.Loading())

        try {
            flow { emit(remote.searchMovie(1, query)) }
                .zip(flow { emit(remote.searchTv(1, query)) }) { moviesResponse, tvResponse ->
                    Pair(
                        NetworkResponse(moviesResponse).handleNetworkResponse(),
                        NetworkResponse(tvResponse).handleNetworkResponse()
                    )
                }
                .zip(flow { emit(remote.searchPeople(1, query)) }) { (movies, tv), peopleResponse ->
                    Triple(
                        movies,
                        tv,
                        NetworkResponse(peopleResponse).handleNetworkResponse()
                    )
                }
                .collect { (movies, tv, people) ->
                    val searchResult = SearchResultPageData(
                        movies = (movies as? NetworkRequest.Success)?.data,
                        tvShows = (tv as? NetworkRequest.Success)?.data,
                        people = (people as? NetworkRequest.Success)?.data
                    )

                    emit(NetworkRequest.Success(searchResult))
                }

        } catch (e: Exception) {
            emit(NetworkRequest.Error(e.localizedMessage ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)


    fun searchMovies(query: String): Flow<PagingData<ResponseMoviesList.Result>> {
        return Pager(
            config = PagingConfig(
                pageSize = 1,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SearchMoviesPagingSource(remote, sessionManager, query) }
        ).flow.flowOn(Dispatchers.IO)
    }

    fun searchTvShows(query: String): Flow<PagingData<ResponseTvsList.Result>> {
        return Pager(
            config = PagingConfig(
                pageSize = 1,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SearchTvShowsPagingSource(remote, sessionManager, query) }
        ).flow.flowOn(Dispatchers.IO)
    }

    fun searchPeople(query: String): Flow<PagingData<ResponsePopularCelebrity.Result>> {
        return Pager(
            config = PagingConfig(
                pageSize = 1,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SearchCelebritiesPagingSource(remote, sessionManager, query) }
        ).flow.flowOn(Dispatchers.IO)
    }
}

