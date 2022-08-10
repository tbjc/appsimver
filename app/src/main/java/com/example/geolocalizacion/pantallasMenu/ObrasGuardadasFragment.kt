package com.example.geolocalizacion.pantallasMenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geolocalizacion.R
import com.example.geolocalizacion.utilidades.DBSqliteHelperLocal
import com.example.geolocalizacion.utilidades.ObraAdapter

class ObrasGuardadasFragment() : Fragment(R.layout.fragment_obras_guardadas) {
    lateinit var rvObras: RecyclerView
    lateinit var btnDescargarObras:Button
    lateinit var btnEliminar:Button
    private lateinit var dialogB: AlertDialog.Builder
    private lateinit var dialogB2: AlertDialog.Builder
    private var objSeleccionadosBooleano:ArrayList<Boolean> = ArrayList<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnDescargarObras = view.findViewById(R.id.btnDescargarObras)
        btnEliminar = view.findViewById(R.id.btnEliminaObras)
        rvObras = view.findViewById(R.id.reciclerObras)
        val listaObras = DBSqliteHelperLocal(requireContext()).getAllObras()
        rvObras.layoutManager = LinearLayoutManager(requireContext())
        val valAdapter = ObraAdapter(listaObras)
        rvObras.adapter = valAdapter
    }

}