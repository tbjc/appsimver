package com.example.geolocalizacion

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import com.example.geolocalizacion.clases.RequestLogin
import com.example.geolocalizacion.clases.ResponseLogin
import com.example.geolocalizacion.utilidades.Network
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var textoMensaje: TextView
    private lateinit var dialogB:AlertDialog.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main)
        val botonEntrar = findViewById<Button>(R.id.btnEntrar)
        val botonSinDatos = findViewById<Button>(R.id.btnSinDatos)
        val textUsuarioEdit = findViewById<TextView>(R.id.EditUsuarioLogin)
        val textPassEdit = findViewById<TextView>(R.id.EditPassLogin)
        textoMensaje = findViewById<TextView>(R.id.MensajePeticion)
        dialogB = AlertDialog.Builder(this)
        //Login botón

        botonEntrar.setOnClickListener {
            val usuario = textUsuarioEdit?.text.toString()
            val password = textPassEdit?.text.toString()
            if (textUsuarioEdit.text.isNotBlank()){
                if(textPassEdit.text.isNotBlank()){

                    var urlServer:String = "http://192.168.0.14/proyectoApi/public/";
                    solicitudLogin(urlServer,usuario,password);
                }else{
                    Toast.makeText(this,"Falta la contraseña",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Falta el nombre de Usuario",Toast.LENGTH_SHORT).show()
            }
        }

        botonSinDatos.setOnClickListener {
            val pasePantalla = Intent(this,Base_Menu::class.java)
            pasePantalla.putExtra("hayConexion","N")
            startActivity(pasePantalla)
        }
    }

    private fun solicitudLogin(url:String, usuario:String, pass:String){
        val requestLogin = RequestLogin(usuario.trim(), pass.trim())

        val service = Network.FuncionApi()
        val cont = this;
        var post: ResponseLogin?
        textoMensaje.text = "Cargando..."
        service.getAllPost(requestLogin).enqueue(object : Callback<ResponseLogin>{
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                textoMensaje.text = ""
                if(response.isSuccessful){
                    post = response.body()
                    if(post?.pasa == true){
                        val intent = Intent(cont, PantallaCarga::class.java)
                        intent.putExtra("token",post?.token)
                        startActivity(intent)
                    }else{
                        dialogB.setTitle("Error")
                            .setMessage(post?.msj)
                            .setNeutralButton("Continuar"){ dialogInterface,it ->
                                dialogInterface.cancel()
                            }
                            .show()
                    }
                }else{
                    Toast.makeText(cont,"Error al comunicarse con SIMVER, comuniquese con el ORFIS",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                Log.i("peticion Fin","se finalizó peticion")
                textoMensaje.text = ""
                dialogB.setTitle("Error")
                    .setMessage("No se ha podico comunicar con el servidor, intente mas tarde o comuníquese con el ORFIS")
                    .setNeutralButton("Continuar"){ dialogInterface,it ->
                        dialogInterface.cancel()
                    }
                    .show()
            }
        })
    }

    override fun onBackPressed() {

    }
}