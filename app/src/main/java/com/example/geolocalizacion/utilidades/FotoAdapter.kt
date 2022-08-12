package com.example.geolocalizacion.utilidades

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.geolocalizacion.R
import com.example.geolocalizacion.clases.FotoObj
import com.example.geolocalizacion.clases.fotoPostRequest
import com.example.geolocalizacion.clases.imagenJson
import com.example.geolocalizacion.clases.metadatos
import com.google.gson.Gson
import kotlin.collections.ArrayList

class FotoAdapter(val fotoObra:ArrayList<FotoObj>):RecyclerView.Adapter<FotoAdapter.FotoHolder>(){
    private var objSeleccionados:ArrayList<FotoObj> = fotoObra
    private var objSeleccionadosBooleano:ArrayList<Boolean> = ArrayList<Boolean>()
    private var listaVistas:ArrayList<View> = ArrayList()

    init {
        objSeleccionados.forEach {
            objSeleccionadosBooleano.add(false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FotoHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.item_foto,parent,false)
        return FotoHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: FotoHolder, position: Int) {
        Log.e("pos","selectected")
        holder.render(fotoObra[position])
        holder.itemView.setOnClickListener {
            if(objSeleccionadosBooleano.get(position)){
                objSeleccionadosBooleano.set(position,false)
            }else{
                objSeleccionadosBooleano.set(position,true)
            }
            notifyDataSetChanged()
        }
        //Evento de mantener presionado el item
        holder.itemView.setOnLongClickListener{
            //Toast.makeText(holder.view.context, "evento detectado", Toast.LENGTH_SHORT).show()
            val builder = AlertDialog.Builder(holder.view.context)
            val inflater = LayoutInflater.from(holder.view.context)
            //se obtiene la vista
            val vistaContenido = inflater.inflate(R.layout.detalle_foto,null)
            //se llenan los componentes del dialog que se mostrará
            vistaContenido.findViewById<TextView>(R.id.dialogNumObra).text = fotoObra[position].numeroObra
            vistaContenido.findViewById<TextView>(R.id.dialogFecha).text = fotoObra[position].fecha
            //se obtiene el mes al que se asigna la foto
            val mesesData:ArrayList<String> = arrayListOf("Enero", "Febrero","Marzo","Abril","Mayo", "Junio","Julio", "Agosto","Septiembre","Octubre","Noviembre","Diciembre")
            vistaContenido.findViewById<TextView>(R.id.dialogMes).text = mesesData[fotoObra[position].mes-1]
            vistaContenido.findViewById<TextView>(R.id.dialogDescripcion).text = fotoObra[position].descripcion
            //se convierte la foto de base64 a bytes
            var imageBytes = Base64.decode(fotoObra[position].foto64, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            vistaContenido.findViewById<ImageView>(R.id.dialogFoto).setImageBitmap(decodedImage)
            //vistaContenido.setBackgroundResource(R.drawable.style_item_foto)
            //se asigna la vista al dialog
            builder.setView(vistaContenido).setTitle("Datos de obra")
                .setNeutralButton("Cerrar",DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                }).setPositiveButton("Ubicación", DialogInterface.OnClickListener { dialog, which ->
                    //se manda a abrir en el navegador la url de la ubicacion de la foto en googlemaps
                    val urlDato = "https://www.google.com.mx/maps/search/"+fotoObra[position].latitud+",+"+fotoObra[position].longitud+"/data=!3m1!1e3"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(urlDato)
                    vistaContenido.context.startActivity(intent)
                })
            builder.create()
            builder.show()
            true
        }
        //se cambia el estilo del item para saber si está seleccionado o no
        if(objSeleccionadosBooleano.get(position)){
            //está seleccionado
            holder.cardView.setBackgroundResource(R.drawable.style_item_selected)
        }else{
            //no está seleccionado
            holder.cardView.setBackgroundResource(R.drawable.style_item_foto)
        }
    }

    override fun getItemCount(): Int {
        return fotoObra.size
    }

    fun ObrasSeleccionadasArray():ArrayList<FotoObj>{
        var returnObras: ArrayList<FotoObj> = ArrayList<FotoObj>()
        objSeleccionadosBooleano.forEachIndexed { index, estaSeleccionado->
            if (estaSeleccionado){
                returnObras.add(objSeleccionados.get(index))
            }
        }
        return returnObras
    }

    fun obrasRequestDatos(IdUsuario:Int):ArrayList<fotoPostRequest>{
        var returnObras: ArrayList<FotoObj> = ArrayList<FotoObj>()
        objSeleccionadosBooleano.forEachIndexed { index, estaSeleccionado->
            if (estaSeleccionado){
                returnObras.add(objSeleccionados.get(index))
            }
        }
        var objDatos:ArrayList<fotoPostRequest> = ArrayList<fotoPostRequest>()
        returnObras.forEach {
            val metadatosStr: String = Gson().toJson(metadatos(
                "2.8",
                it.mes,
                it.fecha,
                "0.01",
                "0.01",
                "0.01",
                "null",
                "null",
                it.latitud,
                "null",
                it.longitud,
                "null",
                "null",
                "null",
                "100",
                "smartphone",
                "null",
                "1",
                "0"
            ))

            val imagenJson:imagenJson = imagenJson(
                it.foto64,
                metadatosStr
            )

            val objFoto:fotoPostRequest = fotoPostRequest(
                it.idObra,
                it.numeroObra,
                it.descripcion,
                it.latitud.toDouble(),
                it.longitud.toDouble(),
                IdUsuario,
                Gson().toJson(imagenJson)
            )
            objDatos.add(objFoto)
        }
        return objDatos
    }

    class FotoHolder(val view: View):RecyclerView.ViewHolder(view){
        var seleccionado:Boolean = false
        val cardView:LinearLayout = view.findViewById(R.id.LayoutFoto)
        fun render(fotoobj:FotoObj){
            val numObra: TextView = view.findViewById(R.id.TxtNumObraRow)
            numObra.text = fotoobj.numeroObra
            view.findViewById<TextView>(R.id.txtLatItem).text = fotoobj.latitud
            view.findViewById<TextView>(R.id.txtLongItem).text = fotoobj.longitud
            val meses:ArrayList<String> = arrayListOf("Enero", "Febrero","Marzo","Abril","Mayo", "Junio","Julio", "Agosto","Septiembre","Octubre","Noviembre","Diciembre")
            view.findViewById<TextView>(R.id.txtMes).text = "Mes : " + meses.get(fotoobj.mes - 1)
            var imageBytes = Base64.decode(fotoobj.foto64, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            view.findViewById<ImageView>(R.id.FotoObraRow).setImageBitmap(decodedImage)
        }
    }
}