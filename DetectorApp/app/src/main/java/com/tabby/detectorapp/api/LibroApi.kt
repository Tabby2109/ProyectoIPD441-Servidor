package com.tabby.detectorapp.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface LibroApi {
    @Multipart
    @POST("/predict")
    fun enviarImagenConModo(
        @Part image: MultipartBody.Part,
        @Part("modo") modo: RequestBody
    ): Call<RespuestaServidor>
}
