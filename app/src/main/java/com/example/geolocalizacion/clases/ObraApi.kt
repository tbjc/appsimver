package com.example.geolocalizacion.clases

class ObraAPI(
    var numeroObra:String,
    var idobra:Int,
    var municipio:String,
    var idmunicipio:Int,
    var clave:String,
    var fondo:String,
    var localidad:String
    )

class ObraDatoAPI(
    val ObraId:Int,
    val NumeroObra:String,
    val Descripcion:String,
    val Fondo:String,
    val Localidad:String,
    val Latitud:Double,
    val Longitud:Double,
    val Activo:Boolean,
    val Clave:String
)