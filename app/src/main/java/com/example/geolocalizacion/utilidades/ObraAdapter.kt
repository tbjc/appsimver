package com.example.geolocalizacion.utilidades

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.geolocalizacion.R
import com.example.geolocalizacion.clases.FotoObj
import com.example.geolocalizacion.clases.ObraAPI

class ObraAdapter(val ObrasObj:ArrayList<ObraAPI>): RecyclerView.Adapter<ObraAdapter.ObraHolder>() {
    private var objSeleccionados:ArrayList<ObraAPI> = ObrasObj
    private var objSeleccionadosBooleano:ArrayList<Boolean> = ArrayList<Boolean>()
    init {
        objSeleccionados.forEach {
            objSeleccionadosBooleano.add(false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObraHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.item_obra,parent,false)
        return ObraHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: ObraHolder, position: Int) {
        holder.render(ObrasObj[position])
        holder.itemView.setOnClickListener {
            if(objSeleccionadosBooleano.get(position)){
                objSeleccionadosBooleano.set(position,false)
            }else{
                objSeleccionadosBooleano.set(position,true)
            }
            notifyDataSetChanged()
        }
        if(objSeleccionadosBooleano.get(position)){
            holder.cardview.setBackgroundResource(R.drawable.style_item_selected)
        }else{
            holder.cardview.setBackgroundResource(R.drawable.style_item_foto)
        }
    }

    override fun getItemCount(): Int {
        return ObrasObj.size
    }

    class ObraHolder (val view: View):RecyclerView.ViewHolder(view){
        var seleccionado:Boolean = false
        var cardview: ConstraintLayout = view.findViewById(R.id.ObraLayout)
        fun render(ObraObj: ObraAPI){
            var numObra: TextView = view.findViewById<TextView>(R.id.item_num_obra)
            numObra.text = ObraObj.numeroObra
            view.findViewById<TextView>(R.id.item_fondo).text = ObraObj.fondo
            view.findViewById<TextView>(R.id.item_localidad).text = ObraObj.localidad
        }
    }
}