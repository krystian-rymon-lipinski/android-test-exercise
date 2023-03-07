package com.krystianrymonlipinski.testexercise.di

import com.krystianrymonlipinski.testexercise.retrofit.HttpService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
object HttpModule {

    private const val INFO_BASE_URL = " https://dev.tapptic.com/test/"

    @ViewModelScoped
    @Provides
    fun provideHttpService() : HttpService {
        return Retrofit.Builder()
            .baseUrl(INFO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HttpService::class.java)
    }
}