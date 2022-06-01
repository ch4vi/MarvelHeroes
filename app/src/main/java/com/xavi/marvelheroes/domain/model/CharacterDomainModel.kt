package com.xavi.marvelheroes.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CharacterDomainModel(
    val id: String,
    val name: String?,
    val description: String?,
    val thumbnail: String?
) : Parcelable
