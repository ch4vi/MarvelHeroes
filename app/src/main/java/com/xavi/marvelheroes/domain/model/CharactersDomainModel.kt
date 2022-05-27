package com.xavi.marvelheroes.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CharactersDomainModel(
    val page: PageDomainModel,
    val characters: List<CharacterDomainModel>
) : Parcelable