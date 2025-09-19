package azari.amirhossein.filmora.data.repository

import azari.amirhossein.filmora.data.models.authentication.CreateGuestSession
import azari.amirhossein.filmora.data.models.authentication.CreateSessionRequest
import azari.amirhossein.filmora.data.models.authentication.CreateSessionResponse
import azari.amirhossein.filmora.data.models.authentication.RequestTokenResponse
import azari.amirhossein.filmora.data.models.authentication.ValidateLoginRequest
import azari.amirhossein.filmora.data.network.ApiServices
import azari.amirhossein.filmora.domain.repository.AuthRepository
import azari.amirhossein.filmora.utils.network.Failure
import azari.amirhossein.filmora.utils.network.Resource
import azari.amirhossein.filmora.utils.extensions.toResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: ApiServices
) : AuthRepository {

    override fun createRequestToken(): Flow<Resource<RequestTokenResponse>> = flow {
        emit(Resource.Loading)
        val response = api.createRequestToken()
        emit(response.toResource())

    }.handleErrors().flowOn(Dispatchers.IO)

    override fun validateLogin(request: ValidateLoginRequest): Flow<Resource<RequestTokenResponse>> =
        flow {
            emit(Resource.Loading)
            val response = api.validateLogin(request)
            emit(response.toResource())
        }.handleErrors().flowOn(Dispatchers.IO)

    override fun createSession(request: CreateSessionRequest): Flow<Resource<CreateSessionResponse>> =
        flow {
            emit(Resource.Loading)
            val response = api.createSession(request)
            emit(response.toResource())
        }.handleErrors().flowOn(Dispatchers.IO)

    override fun createGuestSession(): Flow<Resource<CreateGuestSession>> = flow {
        emit(Resource.Loading)
        val response = api.createGuestSession()
        emit(response.toResource())
    }.handleErrors().flowOn(Dispatchers.IO)

    private fun <T> Flow<Resource<T>>.handleErrors(): Flow<Resource<T>> {
        return this.catch { e ->
            val failure = when (e) {
                is IOException -> Failure.NetworkConnection
                is HttpException -> Failure.ServerError
                else -> Failure.UnknownError
            }
            emit(Resource.Error(failure))
        }.flowOn(Dispatchers.IO)
    }
}
