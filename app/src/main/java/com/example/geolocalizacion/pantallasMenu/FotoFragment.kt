package com.example.geolocalizacion.pantallasMenu

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.geolocalizacion.BuildConfig
import com.example.geolocalizacion.MainActivity
import com.example.geolocalizacion.R
import com.example.geolocalizacion.clases.ObjPolygono
import com.example.geolocalizacion.clases.ObraAPI
import com.example.geolocalizacion.utilidades.DBSqliteHelperLocal
import com.example.geolocalizacion.utilidades.Network
import com.google.android.gms.location.*
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDate
import kotlin.collections.ArrayList


class FotoFragment() : Fragment(R.layout.fragment_foto){
    // Asignación de widgets a las variables de la vista de fotografía
    private lateinit var ListaObrasObj:ArrayList<ObraAPI>
    private lateinit var ObraSeleccionada: ObraAPI
    private lateinit var imageBitmap:Bitmap
    private var tomoFoto:Boolean = false
    private lateinit var fotoBase64: String
    private lateinit var imageViewTmp: ImageView
    private lateinit var txtBase64: TextView
    private lateinit var  txtLatitud: TextView
    private lateinit var  txtLongitud: TextView
    private lateinit var txtMensajeFoto:TextView
    private lateinit var labelDescripcion:TextView
    private lateinit var descripcionFoto:EditText
    private lateinit var btnGuardarFoto:Button
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

    private lateinit var fileExt: File

    // En el momento en el que se convica el fragment se empieza a correr los permisos y funciones de localización
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
            Log.d(TAG, "${it.key} = ${it.value}")
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
                    //Log.e("paso","Cargó datos ubicacion")
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    // Evento de crear Vista
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // se carga la vista y se obtienen los Id de los elementos de la vista


        var lista:ArrayList<String> = ArrayList<String>()
        val spinner = view.findViewById<Spinner>(R.id.spinnerObras)
        val btnFoto = view.findViewById<Button>(R.id.btnFotoTomar)
        this.imageViewTmp = view.findViewById(R.id.imageTmp)
        this.txtBase64 = view.findViewById(R.id.txtMensaje)
        this.imageViewTmp.isVisible = false
        val cont:Context = requireContext()
        this.txtLatitud = view.findViewById(R.id.txtLatitud)
        this.txtLongitud = view.findViewById(R.id.txtLongitud)
        this.btnGuardarFoto = view.findViewById<Button>(R.id.btnGuardaFotoDB)
        this.txtMensajeFoto = view.findViewById(R.id.txtMensaje)
        this.labelDescripcion = view.findViewById(R.id.labelFotoDescripcion)
        this.descripcionFoto = view.findViewById(R.id.txtDescripcionFoto)
        this.txtMensajeFoto.isVisible = false
        btnGuardarFoto.isVisible = false
        labelDescripcion.isVisible = false
        descripcionFoto.isVisible = false
        // Se Cargan las obras al SPINNER
        ListaObrasObj = DBSqliteHelperLocal(cont).getAllObras()
        ListaObrasObj.forEach {
            lista.add(it.numeroObra)
        }

        if (lista.size == 0){
            val dialogB:AlertDialog.Builder = AlertDialog.Builder(requireContext())
            dialogB.setTitle("Error")
                .setMessage("Se ha detectado que no se ha descargado las obras, regrese, conectese a internet y vuelva a entrar con su usuario y contraseña")
                .setNeutralButton("Continuar"){ dialogInterface,it ->
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                }
                .show()
        }

        val aaObras = ArrayAdapter<String>(cont, android.R.layout.simple_spinner_dropdown_item,lista)
        spinner.adapter = aaObras

        //Seleccionar Obra
        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posicion: Int, p3: Long) {
                ObraSeleccionada = ListaObrasObj.get(posicion)
                Log.e("Objeto Seleccionado", ObraSeleccionada.toString())
                imageViewTmp.isVisible = false
                tomoFoto = false

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        //Evento de boton para tomar fotos
        btnFoto.setOnClickListener {
            var localizacionDato:Boolean = false
            val nombreArchivo:String = ObraSeleccionada.clave+".json"
            val txtJson:String = Network.getStringJson(requireContext(), nombreArchivo)
            val objPolCoordenadas = Gson().fromJson(txtJson, ObjPolygono::class.java)
            objPolCoordenadas.geometry.coordinates.forEach {
                var arrayEjeX:ArrayList<Double> = ArrayList<Double>()
                var arrayEjeY:ArrayList<Double> = ArrayList<Double>()
                it.forEach { it2->
                    arrayEjeX.add(it2[0])
                    arrayEjeY.add(it2[1])
                }
                if(Network.puntoEnPoligono(arrayEjeX.size, arrayEjeX, arrayEjeY, LongitudGlobal, LatitudGlobal)){
                    localizacionDato = true
                }
            }

            if (localizacionDato){
                txtMensajeFoto.isVisible = true
                txtMensajeFoto.text = "Comprobando Ubicación"

                val intentCamara = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                    it.resolveActivity(requireActivity().packageManager).also { component ->
                        val photoFile: File = createPhotoFile()
                        fileExt = photoFile
                        val photoUri: Uri = FileProvider.getUriForFile(requireContext(),BuildConfig.APPLICATION_ID+".fileprovider",photoFile)
                        it.putExtra(MediaStore.EXTRA_OUTPUT,photoUri)
                    }
                }
                //intentCamara.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(File("/sdcard/tmp")))
                ///intentCamara.putExtra("android.intent.extras.CAMERA_FACING", 1);
                startForResult.launch(intentCamara)
            }else{
                AlertDialog.Builder(requireContext()).setTitle("Error")
                    .setMessage("Usted no se encuentra dentro del municipio: '"+ObraSeleccionada.municipio+"'")
                    .setNeutralButton("Continuar"){ dialogInterface,it ->
                        dialogInterface.cancel()
                    }
                    .show()
            }


        }

        //evento para guardar fotografias
        btnGuardarFoto.setOnClickListener {
            val fechaDia:Int = LocalDate.now().dayOfMonth
            val fechaMEs:Int = LocalDate.now().monthValue
            var mesGuardar:Int = 0
            val nombreMeses:ArrayList<String> = arrayListOf("Diciembre","Enero", "Febrero","Marzo","Abril","Mayo", "Junio","Julio", "Agosto","Septiembre","Octubre","Noviembre","Diciembre")
            if (fechaDia <= 10){
                var mesActual:String = nombreMeses.get(fechaMEs)
                var mesAnterior:String = nombreMeses.get(fechaMEs - 1)
                AlertDialog.Builder(requireContext()).setTitle("Advertencia")
                    .setMessage("¿En que mes quiere que sea asignada la fotografía?")
                    .setNeutralButton(mesAnterior){ dialog, it->
                        if(fechaMEs == 1) mesGuardar = 12 else mesGuardar = fechaMEs - 1
                        Log.e("mes Anterior",mesGuardar.toString())
                        guardarFotoDB(mesGuardar)
                    }.setPositiveButton(mesActual){ dialog, it->
                        Log.e("mes Anterior",fechaMEs.toString())
                        guardarFotoDB(fechaMEs)
                    }.show()
            }else{
                guardarFotoDB(fechaMEs)
            }
        }
    }

    private fun createPhotoFile(): File {
        val dir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val file = File.createTempFile("IMG_${System.currentTimeMillis()}_",".jpg",dir)
        return file
    }

    //cuando el evento de fotos se usa y es posible usar la camara
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result: ActivityResult ->
        txtMensajeFoto.isVisible = false
        txtMensajeFoto.text = ""
        if(result.resultCode == Activity.RESULT_OK){
            //obtenemos los datos de la foto
            val intent = result.data
            //this.imageBitmap = intent?.extras?.get("data") as Bitmap
            val imagenBitTmp: Bitmap = BitmapFactory.decodeFile(fileExt.toString())
            var tmpHeight:Int = 0
            if (imagenBitTmp.width > 900){
                val porcentaje:Double = ((1.0/imagenBitTmp.width.toInt()) * 900)
                tmpHeight = (imagenBitTmp.height * porcentaje).toInt()
            }else{
                tmpHeight = imagenBitTmp.height
            }
            this.imageBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(fileExt.toString()),900,tmpHeight,false)
            val baos = ByteArrayOutputStream()
            this.imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b = baos.toByteArray()
            this.fotoBase64 = Base64.encodeToString(b, Base64.DEFAULT)
            //mostramos el temporal
            this.imageViewTmp.setImageBitmap(this.imageBitmap)
            this.imageViewTmp.isVisible = true
            //pasamos la foto a base64  ya que así se manda a servidor
            fileExt.delete()
            this.tomoFoto = true
            this.btnGuardarFoto.isVisible = true
            this.labelDescripcion.isVisible = true
            this.descripcionFoto.isVisible = true
        }
    }

    //cuando se sale del fragment se para la opción de localización cada segundo para que el celular no use recursos
    override fun onStop() {
        super.onStop()
        //detener actualizacion de ubicación
        fusedLocationClient.removeLocationUpdates(callback!!)
    }

    private fun guardarFotoDB(mesDato:Int){
        val Descripcion:String = descripcionFoto.text.toString().trim()
        if(Descripcion.isNotEmpty()){
            DBSqliteHelperLocal(requireContext()).agregarFoto(
                ObraSeleccionada.numeroObra,
                ObraSeleccionada.idobra,
                ObraSeleccionada.idmunicipio,
                ObraSeleccionada.municipio,
                "",
                fotoBase64,
                LatitudGlobal,
                LongitudGlobal,
                Descripcion,
                mesDato
            )
            ObraSeleccionada.numeroObra
            this.tomoFoto = false
            this.btnGuardarFoto.isVisible = false
            fotoBase64 = ""
            btnGuardarFoto.isVisible = false
            this.imageViewTmp.isVisible = false
            descripcionFoto.setText("")
            labelDescripcion.isVisible = false
            descripcionFoto.isVisible = false
            AlertDialog.Builder(requireContext()).setTitle("Guardado")
                .setMessage("Se ha guardado la fotografía correctamente")
                .setNeutralButton("Continuar"){ dialogInterface,it ->
                    dialogInterface.cancel()
                }.show()
        }else{
            AlertDialog.Builder(requireContext()).setTitle("Error")
                .setMessage("Debe agregar una descripción")
                .setNeutralButton("Continuar"){ dialogInterface,it ->
                    dialogInterface.cancel()
                }.show()
        }
    }
}