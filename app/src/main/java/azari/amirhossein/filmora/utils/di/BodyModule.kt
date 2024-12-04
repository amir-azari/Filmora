package azari.amirhossein.filmora.utils.di

import azari.amirhossein.filmora.models.authentication.RequestLogin
import azari.amirhossein.filmora.models.authentication.RequestSession
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object BodyModule {
    @Provides
    fun bodyLogin() = RequestLogin()

    @Provides
    fun bodySession() = RequestSession()
}