package com.example.geolocalizacion.utilidades

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.example.geolocalizacion.clases.FotoObj
import com.example.geolocalizacion.clases.ObraAPI
import com.example.geolocalizacion.clases.UbicacionesObj

class DBSqliteHelperLocal(context:Context): SQLiteOpenHelper(context,"obras.db", null, 1){
    override fun onCreate(db: SQLiteDatabase?) {
        val ordenCreacion = "CREATE TABLE IF NOT EXISTS  obras (_id INTEGER PRIMARY KEY AUTOINCREMENT, numero TEXT, idObra TEXT, idMunicipio TEXT, municipio TEXT)"
        db!!.execSQL(ordenCreacion)
        val creacionTablaFotos = "CREATE TABLE IF NOT EXISTS Fotos (_id INTEGER PRIMARY KEY AUTOINCREMENT, numeroObra TEXT, idObra TEXT,idMunicipio TEXT, municipio TEXT, objJson TEXT, fotoBase64 TEXT)"
        db!!.execSQL(creacionTablaFotos)
        val tablaUbicaciones = "CREATE TABLE IF NOT EXISTS Ubicaciones(_id INTEGER PRIMARY KEY AUTOINCREMENT, numero TEXT, idObra TEXT, latitud TEXT, longitud TEXT)"
        db!!.execSQL(tablaUbicaciones)
       // val tablaUbicaciones = "CREATE TABLE IF NOT EXISTS ubicaciones"
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val ordenBorrado = "DROP TABLE IF EXISTS obras"
        db!!.execSQL(ordenBorrado)
        val ordenBorrado2 = "DROP TABLE IF EXISTS Fotos"
        db!!.execSQL(ordenBorrado2)
        val ordenBorrado3 = "DROP TABLE IF EXISTS ubicaciones"
        db!!.execSQL(ordenBorrado3)
        onCreate(db)
    }

    fun agregarObra(numero:String, idObra:Int, idMunicipio:Int, municipio:String){
        val datos = ContentValues()
        datos.put("numero",numero)
        datos.put("idObra",idObra.toString())
        datos.put("idMunicipio",idMunicipio.toString())
        datos.put("municipio",municipio)

        val db = this.writableDatabase
        db.insert("obras",null,datos)
        db.close()
    }

    fun limpiarTablaObras(){
        val db = this.writableDatabase
        val ordenBorrado1 = "DELETE FROM obras"
        db!!.execSQL(ordenBorrado1)
        db.close()
    }

    fun numeroObrasEnBase():Int{
        var NumeroObras:Int = 0
        val dbConsulta = this.readableDatabase
        val sqlStr =  dbConsulta.rawQuery("SELECT COUNT(_id) numeroObra FROM obras",null)
        if(sqlStr.moveToFirst()){
            do {
                NumeroObras = sqlStr.getInt(0)
            }while (sqlStr.moveToNext())
        }
        dbConsulta.close()
        return NumeroObras
    }

    fun getAllObras():ArrayList<ObraAPI>{
        var listaObras = ArrayList<ObraAPI>()
        val dbConsulta = this.readableDatabase
        val sqlStr =  dbConsulta.rawQuery("SELECT * FROM obras",null)
        if(sqlStr.moveToFirst()){
            do {
                val numeroObra = sqlStr.getInt(1).toString()
                val idObra = sqlStr.getInt(2).toInt()
                val idMunicipio = sqlStr.getInt(3).toInt()
                val municipio = sqlStr.getInt(4).toString()
                var obraObj = ObraAPI(numeroObra,idObra,municipio,idMunicipio)
                listaObras.add(obraObj)
            }while (sqlStr.moveToNext())
        }
        dbConsulta.close()
        return listaObras
    }

    fun agregarFoto(numero:String, idObra:Int, idMunicipio:Int, municipio:String, objJson:String, fotoBase64: String){
        val datos = ContentValues()
        datos.put("numeroObra",numero)
        datos.put("idObra",idObra.toString())
        datos.put("idMunicipio",idMunicipio.toString())
        datos.put("municipio",municipio)
        datos.put("objJson",objJson)
        datos.put("fotoBase64",fotoBase64)

        val db = this.writableDatabase
        db.insert("Fotos",null,datos)
        db.close()
    }

    fun getAllFotos():ArrayList<FotoObj>{
        var arrayFoto = ArrayList<FotoObj>()
        val dbConsulta = this.readableDatabase
        val sqlStr =  dbConsulta.rawQuery("SELECT * FROM Fotos",null)
        if(sqlStr.moveToFirst()){
            do {
                val id = sqlStr.getInt(0).toInt()
                val numeroObra = sqlStr.getInt(1).toString()
                val idObra = sqlStr.getInt(2).toInt()
                val idMunicipio = sqlStr.getInt(3).toInt()
                val municipio = sqlStr.getInt(4).toString()
                val objJson = sqlStr.getInt(5).toString()
                val foto64 = sqlStr.getString(6)
                //Log.e("Foto64",foto64.toString())
                var fotoObj = FotoObj(id,numeroObra,idObra,"19.00000000000","-94.00000000000",objJson,foto64.toString())
                arrayFoto.add(fotoObj)
            }while (sqlStr.moveToNext())
        }
        dbConsulta.close()
        return arrayFoto
    }

    fun eliminaFoto(id:Int){
        val db = this.writableDatabase
        val ordenBorrado1 = "DELETE FROM Fotos where _id = "+id
        Log.e("SQL_DELETE", ordenBorrado1)
        db!!.execSQL(ordenBorrado1)
        db.close()
    }

    fun agregarUbicacion(numero:String, idObra:Int, latitud:String, longitud:String){
        val datos = ContentValues()
        datos.put("numero",numero)
        datos.put("idObra",idObra.toString())
        datos.put("latitud",latitud)
        datos.put("longitud",longitud)
        val db = this.writableDatabase
        db.insert("Ubicaciones",null,datos)
        db.close()
    }

    fun getallUbicaciones():ArrayList<UbicacionesObj>{
        var arrayUbicacion = ArrayList<UbicacionesObj>()
        val dbConsulta = this.readableDatabase
        val sqlStr =  dbConsulta.rawQuery("SELECT * FROM Ubicaciones",null)
        if(sqlStr.moveToFirst()){
            do {
                val id = sqlStr.getInt(0).toInt()
                val numeroObra = sqlStr.getInt(1).toString()
                val idObra = sqlStr.getInt(2).toInt()
                val latitud = sqlStr.getString(3)
                val longitud = sqlStr.getString(4)
                var ubicacionObj = UbicacionesObj(id,numeroObra,idObra,latitud,longitud)
                arrayUbicacion.add(ubicacionObj)
            }while (sqlStr.moveToNext())
        }
        dbConsulta.close()
        return arrayUbicacion
    }

    fun eliminaUbicacion(id:Int){
        val db = this.writableDatabase
        val ordenBorrado = "DELETE FROM Ubicaciones where _id = "+id
        Log.e("SQL_DELETE", ordenBorrado)
        db!!.execSQL(ordenBorrado)
        db.close()
    }
}