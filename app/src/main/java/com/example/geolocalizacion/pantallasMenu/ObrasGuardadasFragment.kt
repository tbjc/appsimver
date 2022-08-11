package com.example.geolocalizacion.pantallasMenu

import android.app.Dialog
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
import com.example.geolocalizacion.clases.ObraAPI
import com.example.geolocalizacion.utilidades.DBSqliteHelperLocal
import com.example.geolocalizacion.utilidades.ObraAdapter

class ObrasGuardadasFragment() : Fragment(R.layout.fragment_obras_guardadas) {
    lateinit var rvObras: RecyclerView
    lateinit var btnDescargarObras:Button
    lateinit var btnEliminar:Button
    private lateinit var dialogB: AlertDialog.Builder
    private lateinit var dialogB2: AlertDialog.Builder
    private var objSeleccionados:ArrayList<ObraAPI> = ArrayList<ObraAPI>()
    private lateinit var listaObras:ArrayList<ObraAPI>
    private lateinit var adapterObras:ObraAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //btnDescargarObras = view.findViewById(R.id.btnDescargarObras)
        btnEliminar = view.findViewById(R.id.btnEliminaObras)
        rvObras = view.findViewById(R.id.reciclerObras)
        dialogB = AlertDialog.Builder(requireContext())
        cargaRecicler()

        btnEliminar.setOnClickListener {
            Log.e("Accion","Eliminar Seleccionado")
            val obrasEliminar = adapterObras.obrasSeleccionadas()
            if ((obrasEliminar.size == listaObras.size) || (obrasEliminar.size == 0)){
                val dialogError = AlertDialog.Builder(requireContext())
                if(obrasEliminar.size == listaObras.size){
                    dialogError.setTitle("Error").setMessage("Debe dejar al menos una obra almacenada")
                        .setNeutralButton("Continuar",DialogInterface.OnClickListener { dialog, which ->
                            dialog.cancel()
                        }).show()
                }else if(obrasEliminar.size == 0){
                    dialogError.setTitle("Error").setMessage("Debes seleccionar una obra a eliminar")
                        .setNeutralButton("Continuar",DialogInterface.OnClickListener { dialog, which ->
                            dialog.cancel()
                        }).show()
                }
            }else{
                val msj:String = "¿Está seguro de querer eliminar ${obrasEliminar.size} obra(s)?";
                val dialogConfirm = AlertDialog.Builder(requireContext())
                dialogConfirm.setTitle("Confirmación").setMessage(msj)
                    .setNegativeButton("No",DialogInterface.OnClickListener { dialog, which ->
                        dialog.cancel()
                    }).setPositiveButton("Si",DialogInterface.OnClickListener { dialog, which ->
                        val dbConn = DBSqliteHelperLocal(requireContext())
                        obrasEliminar.forEach {
                            dbConn.eliminarObra(it.numeroObra)
                        }
                        cargaRecicler()
                        dialog.cancel()
                    }).show()
            }
        }
    }

    private fun cargaRecicler(){
        listaObras = DBSqliteHelperLocal(requireContext()).getAllObras()
        rvObras.layoutManager = LinearLayoutManager(requireContext())
        adapterObras = ObraAdapter(listaObras)
        rvObras.adapter = adapterObras
    }

}