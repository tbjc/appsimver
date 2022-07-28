package com.example.geolocalizacion.utilidades

import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.example.geolocalizacion.R
import com.example.geolocalizacion.clases.FotoObj
import com.example.geolocalizacion.clases.UbicacionesObj

class UbicacionAdapter(val UbicacionObj:ArrayList<UbicacionesObj>): RecyclerView.Adapter<UbicacionAdapter.UbicacionHolder>() {

    private var objSeleccionados:ArrayList<UbicacionesObj> = UbicacionObj
    private var objSeleccionadosBooleano:ArrayList<Boolean> = ArrayList<Boolean>()

    init {
        objSeleccionados.forEach {
            objSeleccionadosBooleano.add(false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UbicacionHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UbicacionHolder(layoutInflater.inflate(R.layout.item_ubicacion, parent, false))
    }

    override fun onBindViewHolder(holder: UbicacionHolder, position: Int) {
        holder.render(UbicacionObj[position])
        holder.itemView.setOnClickListener {
            if(objSeleccionadosBooleano.get(position)){
                objSeleccionadosBooleano.set(position,false)
                it.setBackgroundResource(R.drawable.style_item_foto)
            }else{
                objSeleccionadosBooleano.set(position,true)
                it.setBackgroundResource(R.drawable.style_item_selected)
            }
        }
    }

    override fun getItemCount(): Int {
        return UbicacionObj.size
    }

    fun ObrasSeleccionadasArray():ArrayList<UbicacionesObj>{
        var returnObras: ArrayList<UbicacionesObj> = ArrayList<UbicacionesObj>()
        objSeleccionadosBooleano.forEachIndexed { index, estaSeleccionado->
            if (estaSeleccionado){
                returnObras.add(objSeleccionados.get(index))
            }
        }
        return returnObras
    }

    class UbicacionHolder (val view: View):RecyclerView.ViewHolder(view){
        var seleccionado:Boolean = false
        fun render(ubicacionObj:UbicacionesObj){
            var numObra: TextView = view.findViewById<TextView>(R.id.txtItemNumObra)
            numObra.text = ubicacionObj.numero
            view.findViewById<TextView>(R.id.txtItemLatitud).text = ubicacionObj.latitud
            view.findViewById<TextView>(R.id.txtItemLongitud).text = ubicacionObj.longitud
        }
    }

}