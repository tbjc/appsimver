package com.example.geolocalizacion.clases

data class FotoObj (
    val id:Int,
    val numeroObra:String,
    val idObra:Int,
    val latitud:String,
    val longitud:String,
    val cadena:String,
    val foto64: String,
    val mes:Int,
    val descripcion:String,
    val fecha:String
    )

class fotoPostRequest(
    val obraId:Int,
    val numeroObra:String,
    val descripcionFoto:String,
    val Latitud:Double,
    val Longitud:Double,
    val UserId:Int,
    val Imagen:String
)

class imagenJson(
    val filename:String,
    val json_metadata:String
    )

class metadatos(
    val aperture:String,
    val mesDato:Int,
    val datetime:String,
    val exposureTime:String,
    val flash:String,
    val focalLength:String,
    val gpsAltitude:String?,
    val gpsAltitudeRef:String?,
    val gpsLatitude:String?,
    val gpsLatitudeRef:String?,
    val gpsLongitude:String?,
    val gpsLongitudeRef:String?,
    val gpsProcessingMethod:String?,
    val gpsTimestamp:String?,
    val iso:String,
    val make:String,
    val model:String,
    val orientation:String,
    val whiteBalance:String
)