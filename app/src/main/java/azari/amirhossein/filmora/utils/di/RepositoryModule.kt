package azari.amirhossein.filmora.utils.di

import azari.amirhossein.filmora.data.repository.AuthRepositoryImpl
import azari.amirhossein.filmora.data.repository.ContentRepositoryImpl
import azari.amirhossein.filmora.domain.repository.AuthRepository
import azari.amirhossein.filmora.domain.repository.ContentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindContentRepository(contentRepositoryImpl: ContentRepositoryImpl): ContentRepository
}