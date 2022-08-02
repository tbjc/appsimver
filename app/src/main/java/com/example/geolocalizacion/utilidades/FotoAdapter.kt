package com.example.geolocalizacion.utilidades

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
        if(objSeleccionadosBooleano.get(position)){
            holder.cardView.setBackgroundResource(R.drawable.style_item_selected)
        }else{
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