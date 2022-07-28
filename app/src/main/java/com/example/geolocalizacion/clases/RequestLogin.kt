package com.example.geolocalizacion.clases

class RequestLogin (usuario:String, pass:String){
    var Usuario:String = ""
    var Contrasena:String = ""
    init {
        this.Usuario = usuario;
        this.Contrasena = pass
    }
}