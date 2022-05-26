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

//private suspend fun <T : Any> PagingData<T>.collectDataForTest(): List<T> {
//    val dcb = object : DifferCallback {
//        override fun onChanged(position: Int, count: Int) {}
//        override fun onInserted(position: Int, count: Int) {}
//        override fun onRemoved(position: Int, count: Int) {}
//    }
//    val items = mutableListOf<T>()
//    val dif = object : PagingDataDiffer<T>(dcb, TestCoroutineDispatcher()) {
//        override suspend fun presentNewList(
//            previousList: NullPaddedList<T>,
//            newList: NullPaddedList<T>,
//            newCombinedLoadStates: CombinedLoadStates,
//            lastAccessedIndex: Int,
//            onListPresentable: () -> Unit
//        ): Int? {
//            for (idx in 0 until newList.size)
//                items.add(newList.getFromStorage(idx))
//            onListPresentable()
//            return null
//        }
//    }
//    dif.collectFrom(this)
//    return items
//}
