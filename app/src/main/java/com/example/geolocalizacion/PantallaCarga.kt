package com.example.geolocalizacion

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.geolocalizacion.clases.ObraDatoAPI
import com.example.geolocalizacion.utilidades.DBSqliteHelperLocal
import com.example.geolocalizacion.utilidades.Network
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PantallaCarga : AppCompatActivity() {

    lateinit var dbConn: DBSqliteHelperLocal
    private lateinit var btnDato:Button
    private lateinit var btnContinuar:Button
    private lateinit var tokenStr:String
    private lateinit var nombreUsuario:String
    private var idUsuario:String = ""
    private var municipioId:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_pantalla_carga)
        supportActionBar!!.title = "Carga de Obras"
        tokenStr = intent.getStringExtra("token").toString()
        nombreUsuario = intent.getStringExtra("usuario").toString()
        this.idUsuario = intent.getStringExtra("IdUsuario").toString()
        municipioId = intent.getIntExtra("idMunicipio",0)
        btnDato = findViewById(R.id.btnTemp)
        btnContinuar = findViewById(R.id.btnContinuar)
        btnDato.isVisible = false
        btnContinuar.isVisible = false
        val textDato:TextView = findViewById<TextView>(R.id.textMensaje)
        textDato.setText("Comprobando...")

        dbConn = DBSqliteHelperLocal(this)

        val numeroRegistros = dbConn.numeroObrasEnBase()
        //Log.e("Numero_Obras_app",numeroRegistros.toString())
        if(numeroRegistros == 0){
            cargarObrasLocalDB(textDato)
        }else{
            btnDato.isVisible = true
            btnContinuar.isVisible = true
            textDato.setText("Ya tiene registradas $numeroRegistros obras en la aplicaci??n")
        }

        //Bot??n volver a descargar datos de obras
        btnDato.setOnClickListener {
            it.isVisible = false
            btnContinuar.isVisible = false
            dbConn.limpiarTablaObras()
            cargarObrasLocalDB(textDato)
        }

        //Bot??n Continuar
        btnContinuar.setOnClickListener {
            val pasePantalla = Intent(this,Base_Menu::class.java)
            pasePantalla.putExtra("hayConexion","S")
            pasePantalla.putExtra("usuario",nombreUsuario)
            pasePantalla.putExtra("token",tokenStr)
            pasePantalla.putExtra("idUsuario",this.idUsuario)
            startActivity(pasePantalla)
        }
    }

    private fun cargarObrasLocalDB (textDato:TextView){
        textDato.setText("Descargando Obras de tu municipio...")
        val service = Network.FuncionApi()
        service.getAllObras(nombreUsuario,"Bearer "+tokenStr).enqueue(object: Callback<List<ObraDatoAPI>>{
            override fun onResponse(call: Call<List<ObraDatoAPI>>, response: Response<List<ObraDatoAPI>>) {
                val postStr:List<ObraDatoAPI> = response.body()!!
                postStr.forEach{
                    dbConn.agregarObra(it.NumeroObra, it.ObraId, municipioId, it.Localidad, it.Clave, it.Fondo, it.Localidad)
                    //Log.e("Obras API Consulta",it.toString())
                }
                textDato.text = "Se descargaron ${postStr.size} obras a la aplicaci??n, puede continuar"
                btnDato.isVisible = true
                btnContinuar.isVisible = true
            }
            override fun onFailure(call: Call<List<ObraDatoAPI>>, t: Throwable) {
                btnDato.isVisible = true
            }
        })
    }
}