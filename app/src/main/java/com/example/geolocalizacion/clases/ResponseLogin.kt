package com.example.geolocalizacion.clases

class ResponseLogin (
    val mensaje:String,
    val valido:Boolean,
    val token:String,
    val userId:Int,
    val nombreMunicipio:String,
    val municipioId:Int)


class ResponseSubirFotos(var valido:Boolean, var mensaje:String)