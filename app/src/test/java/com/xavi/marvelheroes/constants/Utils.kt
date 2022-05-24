package com.xavi.marvelheroes.constants

import com.xavi.marvelheroes.domain.utils.NetworkClient
import io.mockk.every
import io.mockk.mockk
import retrofit2.Retrofit

fun <T> mockFetch(
    networkClient: NetworkClient<Retrofit>,
    apiClass: Class<T>,
    service: T
) {
    val client = mockk<Retrofit>()
    every { networkClient.client() } returns client
    every { client.create(apiClass) } returns service
}
