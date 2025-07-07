package com.tabby.detectorapp.api

import com.google.gson.annotations.SerializedName

data class RespuestaServidor(
    val texto_mas_parecido: String,
    val titulo: String,
    val autor: String,
    val genero: String,
    val descripcion: String,
    val similitud: Double,
    val modo: String,
    val imagen_procesada: String?,

    @SerializedName("links_compra")
    val linksCompra: List<LinkCompra> = emptyList()
)
