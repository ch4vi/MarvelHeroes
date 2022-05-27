package com.xavi.marvelheroes.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ThumbnailDomainModel(val url: String) : Parcelable
