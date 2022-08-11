package com.example.geolocalizacion.pantallasMenu

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log

import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geolocalizacion.R
import com.example.geolocalizacion.clases.FotoObj
import com.example.geolocalizacion.clases.ResponseSubirFotos
import com.example.geolocalizacion.utilidades.DBSqliteHelperLocal
import com.example.geolocalizacion.utilidades.FotoAdapter
import com.example.geolocalizacion.utilidades.LoadingScreen
import com.example.geolocalizacion.utilidades.Network
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MandarFotosFragment(token:String, IdUsuario:Int) : Fragment(R.layout.fragment_mandar_fotos){
    lateinit var ListaFotos:ArrayList<FotoObj>
    lateinit var rvFotos: RecyclerView
    lateinit var valAdapter: FotoAdapter
    private lateinit var dialogB:AlertDialog.Builder
    private lateinit var dialogB2:AlertDialog.Builder
    private var tokenStr:String = token
    private var IdUsuario: Int = 0

    init {
        this.IdUsuario = IdUsuario
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        IniciarRecycler(view)
        Log.e("TokenData",tokenStr)
        dialogB = AlertDialog.Builder(requireContext())
        dialogB2 = AlertDialog.Builder(requireContext())
        val buttonMandar:Button = view.findViewById(R.id.btnMandarFotos)
        buttonMandar.setOnClickListener {
            val listaFoto = valAdapter.obrasRequestDatos(IdUsuario)
            if(listaFoto.size > 0){
                dialogB.setTitle("Confirmar").setMessage("¿Está seguro de enviar estas fotos?")
                    .setNegativeButton("Enviar", DialogInterface.OnClickListener{ dialog, id ->
                        dialog.cancel()
                        LoadingScreen.displayLoadingWithText(requireContext(),"Enviando Imagenes",false)
                        val service = Network.FuncionApi()
                        val peticion:String = "Bearer "+tokenStr
                        service.subirObras(listaFoto,peticion).enqueue(object : Callback<ResponseSubirFotos>{
                            override fun onResponse(call: Call<ResponseSubirFotos>,response: Response<ResponseSubirFotos>) {
                                if (response.isSuccessful){
                                    val res = response.body()!!
                                    LoadingScreen.hideLoading()
                                    if(res.valido){
                                        IniciarRecycler(view)
                                        MostrarMensajeSencillo("Fotos subidas",res.mensaje)
                                    }else{
                                        MostrarMensajeSencillo("Error",res.mensaje)
                                    }
                                    Log.e("salio",res.mensaje)
                                }else{
                                    LoadingScreen.hideLoading()
                                    if(response.code() == 401){
                                        MostrarMensajeSencillo("Error sesión","Salga de la aplicación y vuelva a entrar con su usuario y contraseña")
                                    }
                                }
                            }
                            override fun onFailure(call: Call<ResponseSubirFotos>, t: Throwable) {
                                Log.e("respuesta","No salió")
                                LoadingScreen.hideLoading()
                                MostrarMensajeSencillo("Error","Ha ocurrido un error, verifique su conexión o salga y vuelva a entrar")
                            }
                        })

                    }).setPositiveButton("No",DialogInterface.OnClickListener{ dialog, id ->
                        dialog.cancel()
                    }).show()
            }else{
                MostrarMensajeSencillo("Error","Debe seleccionar al menos una imagen")
            }
        }

        val buttonEliminar:Button = view.findViewById(R.id.btnDelFotos)
        buttonEliminar.setOnClickListener {
            val listaFoto = valAdapter.ObrasSeleccionadasArray()
            if(listaFoto.size > 0){
                dialogB.setTitle("Confirmar").setMessage("¿Está seguro de eliminar estas fotos?")
                    .setNegativeButton("Eliminar", DialogInterface.OnClickListener{ dialog, id ->
                        listaFoto.forEach { fotoDato->
                            DBSqliteHelperLocal(requireContext()).eliminaFoto(fotoDato.id)
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
        ListaFotos = DBSqliteHelperLocal(requireContext()).getAllFotos()
        rvFotos = view.findViewById(R.id.reciclerFotos)
        rvFotos.layoutManager = LinearLayoutManager(requireContext())
        valAdapter = FotoAdapter(ListaFotos)
        rvFotos.adapter = valAdapter
    }

    fun MostrarMensajeSencillo(titulo:String,mensajeValor:String){
        dialogB2.setTitle(titulo)
            .setMessage(mensajeValor)
            .setNeutralButton("Continuar"){ dialogInterface,it ->
                dialogInterface.cancel()
            }
            .show()
    }

    private fun onItemClick(position:Int){
        Toast.makeText(requireContext(), "ObraSeleccionada::"+ListaFotos[position].numeroObra, Toast.LENGTH_SHORT).show()
    }

}