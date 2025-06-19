package com.example.sistemaasistenciarf.ui.asistencia

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemaasistenciarf.data.model.Asistencia
import com.example.sistemaasistenciarf.databinding.ItemAsistenciaBinding

class AsistenciaAdapter(private val asistencias: List<Asistencia>) :
    RecyclerView.Adapter<AsistenciaAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemAsistenciaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(asistencia: Asistencia) {
            binding.tvNombre.text = asistencia.nombre
            binding.tvFecha.text = asistencia.fecha
            binding.tvHora.text = asistencia.hora
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAsistenciaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = asistencias.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(asistencias[position])
    }
}