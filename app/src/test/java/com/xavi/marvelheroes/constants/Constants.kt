package com.xavi.marvelheroes.constants

import com.xavi.marvelheroes.data.model.CharacterDTO
import com.xavi.marvelheroes.data.model.MarvelResponseDTO
import com.xavi.marvelheroes.data.model.PageDTO
import com.xavi.marvelheroes.data.model.ThumbnailDTO
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.model.CharactersDomainModel
import com.xavi.marvelheroes.domain.model.PageDomainModel
import com.xavi.marvelheroes.domain.model.ThumbnailDomainModel

val thumbnailDTO = ThumbnailDTO(
    path = "foo",
    extension = "bar"
)

val thumbnail = ThumbnailDomainModel(url = "foo.bar")

val otherThumbnailDTO = ThumbnailDTO(
    path = "baz",
    extension = "foz"
)

val otherThumbnail = ThumbnailDomainModel(url = "baz.foz")

val characterDTO = CharacterDTO(
    id = "foo",
    name = "bar",
    thumbnail = thumbnailDTO
)

val character = CharacterDomainModel(
    id = "foo",
    name = "bar",
    thumbnail = thumbnail
)

val otherCharacterDTO = CharacterDTO(
    id = "foo",
    name = "bar",
    thumbnail = otherThumbnailDTO
)

val otherCharacter = CharacterDomainModel(
    id = "foo",
    name = "bar",
    thumbnail = otherThumbnail
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
