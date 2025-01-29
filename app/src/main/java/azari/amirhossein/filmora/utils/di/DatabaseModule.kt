package azari.amirhossein.filmora.utils.di

import android.content.Context
import androidx.room.Room
import azari.amirhossein.filmora.data.database.AppDatabase
import azari.amirhossein.filmora.data.database.AppDao
import azari.amirhossein.filmora.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.Database.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDao(database: AppDatabase): AppDao {
        return database.ado()
    }
}