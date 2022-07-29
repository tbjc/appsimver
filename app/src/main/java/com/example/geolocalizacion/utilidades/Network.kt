package com.example.geolocalizacion.utilidades

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.geolocalizacion.clases.RequestLogin
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate

class Network {
    companion object{
        fun hayRed(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = connectivityManager.activeNetwork ?: return false
                val networkCapabilities =
                    connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
                return when {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                val activeNetwork = connectivityManager.activeNetworkInfo ?: return false
                return activeNetwork.isConnectedOrConnecting
            }
        }

        fun FuncionApi():ApiService{
            //val requestLogin = RequestLogin(usuario, pass)
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.231.116/TokenSIMVER/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service = retrofit.create<ApiService>(ApiService::class.java)
            return service
        }

        fun puntoEnPoligono(nvert:Int, vertx:ArrayList<Double>, verty:ArrayList<Double>, testx:Double, testy:Double ):Boolean{
            var i:Int = 0
            var j:Int = nvert -1
            var c:Boolean = false
            //Log.e("revision1:","i:${i}, j:${j}, c:${c},")
            while (i < nvert){
                //og.e("revision2:","i:${i}, j:${j}, c:${c}, vertx[i]:${vertx[i]}, vertx[j]:${vertx[j]}, verty[i]:${verty[i]}, verty[j]:${verty[j]},  testx: ${testx}, testy: ${testy}")

                if(((verty[i] > testy) != (verty[j]>testy)) &&(testx > (vertx[j]- vertx[i])*(testy-verty[i])/ (verty[j]-verty[i])+ vertx[i])){
                    c = !c
                }
                //Esto no se toca
                j = i++
            }
            return c
        }

        fun getStringJson(context:Context, nombreArchivo:String):String{
            var jsonString:String = ""
            try{
                jsonString = context.assets.open(nombreArchivo).bufferedReader().use {
                    it.readText()
                }
            }catch(e:Exception){
                e.printStackTrace()
                return ""
            }
            return jsonString
        }
    }
}