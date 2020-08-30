package com.jay.nearbysample.di

import android.app.Application
import androidx.annotation.NonNull
import androidx.room.Room
import com.jay.nearbysample.room.AppDatabase
import com.jay.nearbysample.room.dao.LikedDao
import com.jay.nearbysample.room.dao.WardrobeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * Module which provides all required dependencies about local Room Database
 */
@Module
@InstallIn(ApplicationComponent::class)
class PersistenceModule {

    @Provides
    @Singleton
    fun provideDatabase(@NonNull application: Application): AppDatabase {
        return Room
            .databaseBuilder(application, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .build()
    }

    @Provides
    @Singleton
    fun provideWardrobeDao(@NonNull database: AppDatabase): WardrobeDao {
        return database.getWardrobeDao()
    }

    @Provides
    @Singleton
    fun provideLikedDao(@NonNull database: AppDatabase): LikedDao {
        return database.getLikedDao()
    }

    //Add future DAO providers here...
}
