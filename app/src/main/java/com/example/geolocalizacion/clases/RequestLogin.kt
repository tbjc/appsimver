package com.example.geolocalizacion.clases

class RequestLogin (usuario:String, pass:String){
    var usuario:String = ""
    var password:String = ""
    init {
        this.usuario = usuario;
        this.password = pass
    }
}