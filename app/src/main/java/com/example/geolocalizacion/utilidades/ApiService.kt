package com.example.geolocalizacion.utilidades

import com.example.geolocalizacion.clases.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    fun getAllPost(@Body request:RequestLogin): Call<ResponseLogin>

    @GET("getObras")
    fun getAllObras():Call<List<ObraAPI>>

    @POST("cargarObras")
    fun subirObras(@Body listaObras:ArrayList<FotoObj>, @Header("Authorization") tokenAuth:String): Call<ResponseSubirFotos>

    @POST("cargarUbicaciones")
    fun subirUbicaciones(@Body listaUbicaciones:ArrayList<UbicacionesObj>, @Header("Authorization") tokenAuth:String): Call<ResponseSubirFotos>
}