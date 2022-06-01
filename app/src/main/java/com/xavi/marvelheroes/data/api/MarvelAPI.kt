package com.xavi.marvelheroes.data.api

sealed class MarvelAPI {

    class Character : MarvelAPI() {
        companion object {
            const val path: String = "characters"
        }
    }

    class CharacterDetail : MarvelAPI() {
        companion object {
            const val path: String = "characters/{characterId}"
        }
    }
}
