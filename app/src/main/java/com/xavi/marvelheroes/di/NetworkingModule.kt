package com.xavi.marvelheroes.di

import com.xavi.marvelheroes.data.api.RetrofitConfiguration
import com.xavi.marvelheroes.domain.utils.NetworkClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val retrofitModule = module {
    single<NetworkClient<Retrofit>>(named(RetrofitConfiguration.CLIENT)) {
        RetrofitConfiguration()
    }
}
