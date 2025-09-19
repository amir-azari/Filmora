package azari.amirhossein.filmora.domain.repository

import azari.amirhossein.filmora.data.models.authentication.CreateGuestSession
import azari.amirhossein.filmora.data.models.authentication.CreateSessionRequest
import azari.amirhossein.filmora.data.models.authentication.CreateSessionResponse
import azari.amirhossein.filmora.data.models.authentication.RequestTokenResponse
import azari.amirhossein.filmora.data.models.authentication.ValidateLoginRequest
import azari.amirhossein.filmora.utils.network.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun createRequestToken(): Flow<Resource<RequestTokenResponse>>
    fun validateLogin(request: ValidateLoginRequest): Flow<Resource<RequestTokenResponse>>
    fun createSession(request: CreateSessionRequest): Flow<Resource<CreateSessionResponse>>
    fun createGuestSession(): Flow<Resource<CreateGuestSession>>

}