package com.example.geolocalizacion

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.view.isVisible
import com.example.geolocalizacion.clases.ObraAPI
import com.example.geolocalizacion.clases.RequestLogin
import com.example.geolocalizacion.utilidades.ApiService
import com.example.geolocalizacion.utilidades.DBSqliteHelperLocal
import com.example.geolocalizacion.utilidades.Network
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PantallaCarga : AppCompatActivity() {

    lateinit var dbConn: DBSqliteHelperLocal
    private lateinit var btnDato:Button
    private lateinit var btnContinuar:Button
    private lateinit var tokenStr:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_pantalla_carga)
        supportActionBar!!.title = "Carga de Obras"
        tokenStr = intent.getStringExtra("token").toString()
        btnDato = findViewById<Button>(R.id.btnTemp)
        btnContinuar = findViewById<Button>(R.id.btnContinuar)
        btnDato.isVisible = false
        btnContinuar.isVisible = false
        val textDato:TextView = findViewById<TextView>(R.id.textMensaje)
        textDato.setText("Comprobando...")

        dbConn = DBSqliteHelperLocal(this)

        val numeroRegistros = dbConn.numeroObrasEnBase()
        Log.e("Numero_Obras_app",numeroRegistros.toString())
        if(numeroRegistros == 0){
            cargarObrasLocalDB(textDato)
        }else{
            btnDato.isVisible = true
            btnContinuar.isVisible = true
            textDato.setText("Ya tiene registradas $numeroRegistros obras en la aplicación")
        }

        //Botón volver a descargar datos de obras
        btnDato.setOnClickListener {
            it.isVisible = false
            btnContinuar.isVisible = false
            dbConn.limpiarTablaObras()
            cargarObrasLocalDB(textDato)
        }

        //Botón Continuar
        btnContinuar.setOnClickListener {
            val pasePantalla = Intent(this,Base_Menu::class.java)
            pasePantalla.putExtra("hayConexion","S")
            pasePantalla.putExtra("token",tokenStr)
            startActivity(pasePantalla)
        }
    }

    fun cargarObrasLocalDB (textDato:TextView){
        textDato.setText("Descargando Obras de tu municipio...")
        val service = Network.FuncionApi()
        service.getAllObras().enqueue(object: Callback<List<ObraAPI>>{
            override fun onResponse(call: Call<List<ObraAPI>>, response: Response<List<ObraAPI>>) {
                var postStr:List<ObraAPI> = response?.body()!!
                postStr.forEach{
                    dbConn.agregarObra(it.numeroObra, it.idobra, it.idmunicipio, it.municipio)
                    Log.e("Obras API Consulta",it.toString())
                }
                textDato.setText("Se descargaron ${postStr.size} obras a la aplicación, puede continuar")
                btnDato.isVisible = true
                btnContinuar.isVisible = true
            }
            override fun onFailure(call: Call<List<ObraAPI>>, t: Throwable) {
                TODO("Not yet implemented")
                btnDato.isVisible = true
            }
        })
    }
}