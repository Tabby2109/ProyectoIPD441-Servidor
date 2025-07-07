package com.tabby.detectorapp.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LinkCompra(
    val origen: String,
    val precio: String,
    val similitud: Double,
    val url: String
) : Parcelable

