package com.example.geolocalizacion.clases

data class ObjPolygono (val type:String, val properties:Propiedades, val geometry:Geometria)

class Propiedades(val CVE_ENT:String, val CVE_MUN:String, val CVEGEO:String, val NOMGEO:String)

class Geometria(val type:String, val coordinates:ArrayList<ArrayList<ArrayList<Double>>>)