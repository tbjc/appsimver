package com.example.geolocalizacion.utilidades



import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.geolocalizacion.R
import com.example.geolocalizacion.clases.FotoObj
import java.util.*
import kotlin.collections.ArrayList




class FotoAdapter(val fotoObra:ArrayList<FotoObj>):RecyclerView.Adapter<FotoAdapter.FotoHolder>(){

    private var objSeleccionados:ArrayList<FotoObj> = fotoObra
    private var objSeleccionadosBooleano:ArrayList<Boolean> = ArrayList<Boolean>()

    init {
        objSeleccionados.forEach {
            objSeleccionadosBooleano.add(false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FotoHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return FotoHolder(layoutInflater.inflate(R.layout.item_foto,parent,false))
    }

    override fun onBindViewHolder(holder: FotoHolder, position: Int) {
        Log.e("pos","selectected")
        holder.render(fotoObra[position])
        holder.itemView.setOnClickListener {
            if(objSeleccionadosBooleano.get(position)){
                objSeleccionadosBooleano.set(position,false)
                it.setBackgroundColor(Color.parseColor("#e1e1e1"))
            }else{
                objSeleccionadosBooleano.set(position,true)
                it.setBackgroundColor(Color.parseColor("#A8CFAB"))
            }
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

    class FotoHolder(val view: View):RecyclerView.ViewHolder(view){
        var seleccionado:Boolean = false
        fun render(fotoobj:FotoObj){
            var numObra: TextView = view.findViewById<TextView>(R.id.TxtNumObraRow)
            numObra.text = fotoobj.numeroObra
            view.findViewById<TextView>(R.id.txtLatItem).text = fotoobj.latitud
            view.findViewById<TextView>(R.id.txtLongItem).text = fotoobj.longitud
            var imageBytes = Base64.decode(fotoobj.foto64, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            view.findViewById<ImageView>(R.id.FotoObraRow).setImageBitmap(decodedImage)
        }
    }

}