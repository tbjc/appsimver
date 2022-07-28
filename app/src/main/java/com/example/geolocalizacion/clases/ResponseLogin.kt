package com.example.geolocalizacion.clases

class ResponseLogin (pasa:Boolean, token:String, msj:String){
    var pasa:Boolean = false
    var token:String = ""
    var msj:String = ""

    init {
        this.pasa = pasa
        this.token = token
        this.msj = msj
    }
}

class ResponseSubirFotos(var pasa:Boolean, var msj:String)