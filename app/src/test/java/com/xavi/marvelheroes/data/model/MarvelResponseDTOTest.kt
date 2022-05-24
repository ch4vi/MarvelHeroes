package com.xavi.marvelheroes.data.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.xavi.marvelheroes.constants.marvelResponseDTOResponse
import org.junit.Before
import org.junit.Test

class MarvelResponseDTOTest {

    private lateinit var parser: Moshi

    @Before
    fun setUp() {
        parser = Moshi.Builder().build()
    }

    @Test
    fun `GIVEN response when parsing THEN dto is valid`() {
        val characterResponseTyped = Types.newParameterizedType(MarvelResponseDTO::class.java, CharacterDTO::class.java)
        val adapter = parser.adapter<MarvelResponseDTO<CharacterDTO>>(characterResponseTyped)
        val output = adapter.fromJson(marvelResponseDTOResponse)

        assert(output != null)
    }
}
