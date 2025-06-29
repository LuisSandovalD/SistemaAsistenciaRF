package com.example.sistemaasistenciarf.ui.asistencia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemaasistenciarf.R
import com.example.sistemaasistenciarf.data.model.Asistencia
import java.text.SimpleDateFormat
import java.util.*

class AsistenciaAdapter(private var lista: List<Asistencia>) :
    RecyclerView.Adapter<AsistenciaAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreUsuario)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFechaHora)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_asistencia, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val asistencia = lista[position]
        holder.tvNombre.text = asistencia.nombreUsuario

        val formatoEntrada = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formatoSalida = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

        val fechaFormateada = try {
            val fecha = formatoEntrada.parse(asistencia.fechaHora)
            formatoSalida.format(fecha!!)
        } catch (e: Exception) {
            asistencia.fechaHora // Si falla el parseo, se muestra el string original
        }

        holder.tvFecha.text = fechaFormateada
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Asistencia>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
