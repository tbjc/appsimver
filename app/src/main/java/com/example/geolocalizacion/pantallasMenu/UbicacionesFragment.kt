package com.example.geolocalizacion.pantallasMenu

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.geolocalizacion.MainActivity
import com.example.geolocalizacion.R
import com.example.geolocalizacion.clases.ObraAPI
import com.example.geolocalizacion.utilidades.DBSqliteHelperLocal
import com.google.android.gms.location.*


class UbicacionesFragment : Fragment(R.layout.fragment_ubicaciones){
    private lateinit var ListaObrasObj:ArrayList<ObraAPI>
    private lateinit var ObraSeleccionada: ObraAPI
    private lateinit var spinerObras:Spinner
    private lateinit var  txtLatitud: TextView
    private lateinit var  txtLongitud: TextView
    // Cargando Los permisos que van a ser para poder obtener la ubicación
    private val permisoFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
    private val permisoCoarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION
    private val CODIGO_SOLICITUD_PERMISO = 100
    // Cargando las variables de ubicación
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest?= null
    private var LatitudGlobal:Double = 0.0
    private var LongitudGlobal:Double = 0.0
    private var callback: LocationCallback? = null

    override fun onStart() {
        super.onStart()
        fusedLocationClient = FusedLocationProviderClient(requireContext())
        if (!validarPermisosUbicacion()){
            Log.e("paso","Validó Permiso")
            ObtenerUbicacion()
        }else{
            Log.e("paso","No tenía Permiso")
            pedirPermiso()
        }
    }
    // Validar los permisos
    private fun validarPermisosUbicacion():Boolean{
        val permiso1 = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        val permiso2 = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        return  permiso1 && permiso2
    }
    // Se piden los permisos
    private fun pedirPermiso(){
        Log.e("paso","Pide Permiso")
        requestMultiplePermissions.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }
    // Se se comprueban los permisos uno a uno para que sean validados y poder usarse
    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        Log.e("paso","Solicita Cada Permiso")
        permissions.entries.forEach {
            Log.d(ContentValues.TAG, "${it.key} = ${it.value}")
        }
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true && permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            Log.e("paso","Ya hay permiso")
            ObtenerUbicacion()
        } else {
            Log.e("paso","Rechazó Permiso")
        }
    }

    //@SuppressLint("MissingPermission")
    // declaración de la variable donde se estarán cargando las ubicaciones cada vez que sea convocado
    private fun ObtenerUbicacion(){
        Log.e("paso","Ejecuta Obtener ubicacion")
        callback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for(ubicacion in locationResult?.locations!!){
                    Log.e("paso","Cargó datos ubicacion")
                    LatitudGlobal = ubicacion.latitude
                    LongitudGlobal = ubicacion.longitude
                    txtLatitud.text = ubicacion.latitude.toString()
                    txtLongitud.text = ubicacion.longitude.toString()
                    //Toast.makeText(requireContext(),ubicacion.latitude.toString()+"::"+ubicacion.longitude.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
        iniciaLocationRequest()
    }
    // se marca que cada segundo se pedirá la ubicación
    @SuppressLint("MissingPermission")
    private fun iniciaLocationRequest(){
        Log.e("paso","Se ejecuta la funcion de ubicacion continua")
        val locationRequestDato = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 1000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationClient.requestLocationUpdates(locationRequestDato,callback!!,null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.txtLatitud = view.findViewById(R.id.txtLatitud2)
        this.txtLongitud = view.findViewById(R.id.txtLongitud2)
        var lista:ArrayList<String> = ArrayList<String>()
        val spinner = view.findViewById<Spinner>(R.id.spinnerObrasUbicacion)

        ListaObrasObj = DBSqliteHelperLocal(requireContext()).getAllObras()
        ListaObrasObj.forEach {
            lista.add(it.numeroObra)
        }
        if (lista.size == 0){
            val dialogB: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            dialogB.setTitle("Error")
                .setMessage("Se ha detectado que no se ha descargado las obras, regrese, conectese a internet y vuelva a entrar con su usuario y contraseña")
                .setNeutralButton("Continuar"){ dialogInterface,it ->
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                }
                .show()
        }

        val aaObras = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item,lista)
        spinner.adapter = aaObras

        //Seleccionar Obra
        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posicion: Int, p3: Long) {
                ObraSeleccionada = ListaObrasObj.get(posicion)
                Log.e("Objeto Seleccionado", ObraSeleccionada.toString())
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        val btnAddUbicacion = view.findViewById<Button>(R.id.botonAddUbicacion)
        btnAddUbicacion.setOnClickListener {
            if(DBSqliteHelperLocal(requireContext()).numeroUbicacionesObra(ObraSeleccionada.numeroObra) == 0){
                DBSqliteHelperLocal(requireContext()).agregarUbicacion(ObraSeleccionada.numeroObra, ObraSeleccionada.idobra, LatitudGlobal.toString(), LongitudGlobal.toString())
                AlertDialog.Builder(requireContext()).setTitle("Guardado")
                    .setMessage("Se ha guardado la ubicacion correctamente")
                    .setNeutralButton("Continuar"){ dialogInterface,it ->
                        dialogInterface.cancel()
                    }
                    .show()
            }else{
                AlertDialog.Builder(requireContext()).setTitle("Error")
                    .setMessage("Ya existe una ubicación guardada de esta obra")
                    .setNeutralButton("Continuar"){ dialogInterface,it ->
                        dialogInterface.cancel()
                    }
                    .show()
            }

        }
    }

    override fun onStop() {
        super.onStop()
        //detener actualizacion de ubicación
        fusedLocationClient.removeLocationUpdates(callback!!)
    }
}