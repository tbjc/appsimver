package com.example.geolocalizacion.utilidades

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.geolocalizacion.R
import com.example.geolocalizacion.clases.FotoObj
import com.example.geolocalizacion.clases.UbicacionesObj
import com.example.geolocalizacion.clases.fotoPostRequest

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
            }else{
                objSeleccionadosBooleano.set(position,true)
            }
            notifyDataSetChanged()
        }
        if (objSeleccionadosBooleano.get(position)){
            holder.cardview.setBackgroundResource(R.drawable.style_item_selected)
        }else{
            holder.cardview.setBackgroundResource(R.drawable.style_item_foto)
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

    fun ubicacionesRequestDatos(IdUsuario:Int):ArrayList<fotoPostRequest>{
        var returnObras: ArrayList<UbicacionesObj> = ArrayList<UbicacionesObj>()
        objSeleccionadosBooleano.forEachIndexed { index, estaSeleccionado->
            if (estaSeleccionado){
                returnObras.add(objSeleccionados.get(index))
            }
        }
        var returnObrasDatos: ArrayList<fotoPostRequest> = ArrayList<fotoPostRequest>()
        returnObras.forEach {
            var objUbicacion:fotoPostRequest = fotoPostRequest(
                it.idObra,
                it.numero,
                "",
                it.latitud.toDouble(),
                it.longitud.toDouble(),
                IdUsuario,
                ""
            )
            returnObrasDatos.add(objUbicacion)
        }

        return returnObrasDatos
    }

    class UbicacionHolder (val view: View):RecyclerView.ViewHolder(view){
        var seleccionado:Boolean = false
        var cardview:ConstraintLayout = view.findViewById(R.id.UbicacionLayout)
        fun render(ubicacionObj:UbicacionesObj){
            var numObra: TextView = view.findViewById<TextView>(R.id.txtItemNumObra)
            numObra.text = ubicacionObj.numero
            view.findViewById<TextView>(R.id.txtItemLatitud).text = ubicacionObj.latitud
            view.findViewById<TextView>(R.id.txtItemLongitud).text = ubicacionObj.longitud
        }
    }
}