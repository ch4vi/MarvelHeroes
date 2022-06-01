package com.xavi.marvelheroes.constants

import com.xavi.marvelheroes.data.db.CharacterDB
import com.xavi.marvelheroes.data.model.CharacterDTO
import com.xavi.marvelheroes.data.model.MarvelResponseDTO
import com.xavi.marvelheroes.data.model.PageDTO
import com.xavi.marvelheroes.data.model.ThumbnailDTO
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.model.CharactersDomainModel
import com.xavi.marvelheroes.domain.model.PageDomainModel

val thumbnailDTO = ThumbnailDTO(
    path = "foo",
    extension = "bar"
)

val otherThumbnailDTO = ThumbnailDTO(
    path = "baz",
    extension = "foz"
)

val characterDTO = CharacterDTO(
    id = "foo",
    name = "bar",
    description = "foobar",
    thumbnail = thumbnailDTO
)

val character = CharacterDomainModel(
    id = "foo",
    name = "bar",
    description = "foobar",
    thumbnail = "foo.bar"
)

val characterDB = CharacterDB(
    id = "foo",
    name = "bar",
    description = "foobar",
    thumbnail = "foo.bar"
)

val otherCharacterDTO = CharacterDTO(
    id = "foz",
    name = "baz",
    description = "foobar",
    thumbnail = otherThumbnailDTO
)

val otherCharacter = CharacterDomainModel(
    id = "foz",
    name = "baz",
    description = "foobar",
    thumbnail = "baz.foz"
)

val otherCharacterDB = CharacterDB(
    id = "foz",
    name = "baz",
    description = "foobar",
    thumbnail = "baz.foz"
)

val characterPageDTO = PageDTO(
    offset = 0,
    limit = 2,
    total = 2,
    count = 2,
    results = listOf(characterDTO, otherCharacterDTO)
)

val characterPage = PageDomainModel(
    offset = 0,
    limit = 2,
    total = 2,
    count = 2
)

val characterListDTO = MarvelResponseDTO(
    code = 200,
    status = "OK",
    copyright = "",
    attributionText = "",
    data = characterPageDTO
)

val charactersList = CharactersDomainModel(
    page = characterPage,
    characters = listOf(character, otherCharacter)
)
