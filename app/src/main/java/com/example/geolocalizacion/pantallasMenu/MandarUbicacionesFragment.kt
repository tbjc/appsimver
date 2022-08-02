package com.example.geolocalizacion.pantallasMenu

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geolocalizacion.R
import com.example.geolocalizacion.clases.ResponseSubirFotos
import com.example.geolocalizacion.clases.UbicacionesObj
import com.example.geolocalizacion.utilidades.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MandarUbicacionesFragment(token:String, IdUsuario:Int) : Fragment(R.layout.fragment_mandar_ubicaciones) {

    private lateinit var ListasUbicaciones:ArrayList<UbicacionesObj>
    private lateinit var rvUbicaciones:RecyclerView
    private lateinit var valAdapter:UbicacionAdapter
    private lateinit var dialogB:AlertDialog.Builder
    private lateinit var dialogB2:AlertDialog.Builder
    private var tokenStr:String = token
    private val idUsuario:Int = IdUsuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        IniciarRecycler(view)
        val buttonMandar: Button = view.findViewById(R.id.btnMandarUbicaciones)
        dialogB = AlertDialog.Builder(requireContext())
        dialogB2 = AlertDialog.Builder(requireContext())
        buttonMandar.setOnClickListener {
            val listaUbicaciones = valAdapter.ubicacionesRequestDatos(idUsuario)
            if(listaUbicaciones.size > 0){
                dialogB.setTitle("Confirmar").setMessage("¿Está seguro de enviar las ubicaciones seleccionadas?")
                    .setNegativeButton("Enviar", DialogInterface.OnClickListener{ dialog, id ->
                        dialog.cancel()
                        LoadingScreen.displayLoadingWithText(requireContext(),"Enviando ubicaciones",false)
                        val service = Network.FuncionApi()
                        val peticion:String = "Bearer "+tokenStr
                        service.subirUbicaciones(listaUbicaciones,peticion).enqueue(object :
                            Callback<ResponseSubirFotos> {
                            override fun onResponse(call: Call<ResponseSubirFotos>, response: Response<ResponseSubirFotos>) {
                                if(response.isSuccessful){
                                    val res = response.body()!!
                                    LoadingScreen.hideLoading()
                                    if(res.valido){
                                        IniciarRecycler(view)
                                        MostrarMensajeSencillo("ubicaciones subidas",res.mensaje)
                                    }else{
                                        MostrarMensajeSencillo("Error",res.mensaje)
                                    }
                                    Log.e("salio",res.mensaje)
                                }else{
                                    LoadingScreen.hideLoading()
                                    MostrarMensajeSencillo("Error","Salga de la aplicación y vuelva a entrar con su usuario y contraseña")
                                }
                            }
                            override fun onFailure(call: Call<ResponseSubirFotos>, t: Throwable) {
                                Log.e("respuesta","No salió")
                                LoadingScreen.hideLoading()
                                MostrarMensajeSencillo("Error","Ha ocurrido un error, verifique su conexión o salga y vuelva a entrar")
                            }
                        })

                    }).setPositiveButton("No", DialogInterface.OnClickListener{ dialog, id ->
                        dialog.cancel()
                    }).show()
            }
        }

        val buttonEliminar:Button = view.findViewById(R.id.btnDelUbicaciones)
        buttonEliminar.setOnClickListener {
            val listaFoto = valAdapter.ObrasSeleccionadasArray()
            if(listaFoto.size > 0){
                dialogB.setTitle("Confirmar").setMessage("¿Está seguro de eliminar las ubicaciones seleccionadas?")
                    .setNegativeButton("Eliminar", DialogInterface.OnClickListener{ dialog, id ->
                        listaFoto.forEach { fotoDato->
                            DBSqliteHelperLocal(requireContext()).eliminaUbicacion(fotoDato.id)
                        }
                        IniciarRecycler(view)
                        dialog.cancel()
                    }).setPositiveButton("No",DialogInterface.OnClickListener{ dialog, id ->
                        dialog.cancel()
                    }).show()
            }
        }
    }

    fun IniciarRecycler(view: View){
        ListasUbicaciones = DBSqliteHelperLocal(requireContext()).getallUbicaciones()
        rvUbicaciones = view.findViewById(R.id.reciclerUbicaciones)
        rvUbicaciones.layoutManager = LinearLayoutManager(requireContext())
        valAdapter = UbicacionAdapter(ListasUbicaciones)
        rvUbicaciones.adapter = valAdapter
    }

    fun MostrarMensajeSencillo(titulo:String,mensajeValor:String){
        dialogB2.setTitle(titulo)
            .setMessage(mensajeValor)
            .setNeutralButton("Continuar"){ dialogInterface,it ->
                dialogInterface.cancel()
            }
            .show()
    }

}