package com.xavi.marvelheroes.data.api

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.RemoteMediator
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.xavi.marvelheroes.BuildConfig.APPLICATION_ID
import com.xavi.marvelheroes.BuildConfig.BASE_URL
import com.xavi.marvelheroes.BuildConfig.NETWORK_LOGGING
import com.xavi.marvelheroes.domain.model.ErrorDomainModel
import com.xavi.marvelheroes.domain.model.Failure
import com.xavi.marvelheroes.domain.utils.DTO
import com.xavi.marvelheroes.domain.utils.DataSource
import com.xavi.marvelheroes.domain.utils.NetworkClient
import com.xavi.marvelheroes.domain.utils.Predicate
import com.xavi.marvelheroes.domain.utils.Repository
import com.xavi.marvelheroes.domain.utils.State
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.Date
import java.util.concurrent.TimeUnit

// region Client

class RetrofitConfiguration : NetworkClient<Retrofit> {
    companion object {
        const val CLIENT = "retrofit_client"
        private const val TIMEOUT: Long = 100
    }

    override fun client() = initRetrofit(initMoshi(), initClient())

    private fun initMoshi() = Moshi.Builder()
        .add(Date::class.java, Rfc3339DateJsonAdapter())
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private fun initClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        if (NETWORK_LOGGING) logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        else logging.setLevel(HttpLoggingInterceptor.Level.NONE)

        val headers = Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("User-Agent", APPLICATION_ID)
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(headers)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    private fun initRetrofit(moshi: Moshi, client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(client)
        .build()
}

// endregion

// region Repository

interface RetrofitRepository<API, T, R : DTO> : Repository<T, R, RetrofitPagedPredicate<API, T, R>>

// endregion

// region DataSource

abstract class RetrofitPagedSource<API, LIST : Any, T, R : DTO> :
    PagingSource<Int, LIST>(),
    RetrofitDataSource<API, T, R>

@ExperimentalPagingApi
abstract class RetrofitMediator<API, T, R : DTO, S : Any> :
    RemoteMediator<Int, S>(),
    RetrofitDataSource<API, T, R>

interface RetrofitDataSource<API, T, R : DTO> :
    DataSource<Retrofit, T, R, RetrofitPagedPredicate<API, T, R>> {

    @Suppress("TooGenericExceptionCaught")
    override suspend fun fetch(predicate: RetrofitPagedPredicate<API, T, R>): State<T> {
        return try {
            val service = networkClient.client().create(predicate.service())
            val endpoint = predicate.endpoint()
            val dto = endpoint(service)
            State.Success(predicate.mapper().map(dto))
        } catch (e: Throwable) {
            val error: Throwable = when (e) {
                is ConnectException -> Failure.NetworkError
                is UnknownHostException -> Failure.NetworkError
                is HttpException -> handlerHttpException(e)
                else -> Failure.Unexpected(e.message)
            }
            State.Error(error)
        }
    }

    @Suppress("SwallowedException")
    private fun handlerHttpException(e: HttpException): Throwable {
        return try {
            val moshi = Moshi.Builder().build()
            val adapter: JsonAdapter<ErrorDomainModel> = moshi.adapter(ErrorDomainModel::class.java)
            val error = e.response()?.errorBody()?.string()?.let {
                adapter.fromJson(it)
            }
            return error?.let {
                Failure.ResponseError(it)
            } ?: Failure.Unexpected(e.message())
        } catch (ex: JsonDataException) {
            Failure.MalformedError(e.response()?.errorBody()?.string())
        }
    }
}

// endregion

// region Predicate

interface RetrofitPagedPredicate<API, T, R : DTO> : Predicate<T, R> {
    fun service(): Class<API>
    fun endpoint(): suspend (API) -> R
}

// endregion
