package com.xavi.marvelheroes.data.api

import com.xavi.marvelheroes.data.model.CharacterDTO
import com.xavi.marvelheroes.data.model.MarvelResponseDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface CharacterListService {

    @GET(MarvelAPI.Character.path)
    suspend fun characters(
        @Query("nameStartsWith") queryName: String?,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("ts") ts: String,
        @Query("hash") hash: String,
        @Query("apikey") apikey: String,
    ): MarvelResponseDTO<CharacterDTO>
}
