package com.example.geolocalizacion.utilidades

import com.example.geolocalizacion.clases.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("api/LoginTokenAPI/login")
    fun getAllPost(@Body request:RequestLogin): Call<ResponseLogin>

    @GET("api/ConsultaObra/obtieneObras")
    fun getAllObras(@Query("nombreUsuario") usuario:String, @Header("Authorization") tokenAuth:String):Call<List<ObraDatoAPI>>

    @POST("api/crudgeo/guardaFotosObras")
    fun subirObras(@Body listaFotosObraModel:ArrayList<fotoPostRequest>, @Header("Authorization") tokenAuth:String): Call<ResponseSubirFotos>

    @POST("api/crudgeo/guardaListaObras")
    fun subirUbicaciones(@Body listaObraModel:ArrayList<fotoPostRequest>, @Header("Authorization") tokenAuth:String): Call<ResponseSubirFotos>
}