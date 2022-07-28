package com.example.geolocalizacion.clases

class ObraAPI(numeroObra:String, idobra:Int, municipio:String, idmunicipio:Int) {
    var numeroObra:String = "";
    var idobra:Int = 0;
    var municipio:String = "";
    var idmunicipio:Int = 0;

    init {
        this.numeroObra = numeroObra
        this.idobra = idobra
        this.municipio = municipio
        this.idmunicipio = idmunicipio
    }

}