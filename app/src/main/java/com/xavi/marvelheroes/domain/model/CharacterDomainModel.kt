package com.xavi.marvelheroes.domain.model

data class CharacterDomainModel(
    val id: String,
    val name: String?,
    val thumbnail: ThumbnailDomainModel?
)

data class CharactersDomainModel(
    val page: PageDomainModel,
    val characters: List<CharacterDomainModel>
)
