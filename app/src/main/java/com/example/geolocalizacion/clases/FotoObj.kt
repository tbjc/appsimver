package com.example.geolocalizacion.clases

data class FotoObj (
    val id:Int,
    val numeroObra:String,
    val idObra:Int,
    val latitud:String,
    val longitud:String,
    val cadena:String,
    val foto64: String,
    val mes:Int
    )

class fotoPostRequest(
    val ObraId:Int,
    val NumeroObra:String,
    val DescripcionFoto:String,
    val Mes:Int,
    val Latitud:Double,
    val Longitud:Double,
    val UserID:Int,
    val Imagen:String,
    val Metadatos:String
)

class metadatos(
    val aperture:String,
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