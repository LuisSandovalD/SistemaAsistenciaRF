package com.example.sistemaasistenciarf.ui.user

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemaasistenciarf.R
import com.example.sistemaasistenciarf.data.model.Usuario
import java.io.File

class UsuarioAdapter(
    private var listaUsuarios: List<Usuario>,
    private val onEditar: (Usuario) -> Unit,
    private val onEliminar: (Usuario) -> Unit,
    private val onVerAsistencia: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.tvNombre)
        val correo: TextView = itemView.findViewById(R.id.tvCorreo)
        val imagenRostro: ImageView = itemView.findViewById(R.id.imgRostro)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
        val btnVerAsistencia: Button = itemView.findViewById(R.id.btnVerAsistencia)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = listaUsuarios[position]
        holder.nombre.text = "${usuario.nombre} ${usuario.apellido}"
        holder.correo.text = usuario.correo

        try {
            val archivo = File(usuario.rutaRostro ?: "")
            if (archivo.exists()) {
                val bitmap = BitmapFactory.decodeFile(archivo.absolutePath)
                holder.imagenRostro.setImageBitmap(bitmap)
            } else {
                holder.imagenRostro.setImageResource(R.drawable.ic_launcher_background)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            holder.imagenRostro.setImageResource(R.drawable.ic_launcher_background)
        }

        holder.btnEditar.setOnClickListener { onEditar(usuario) }
        holder.btnEliminar.setOnClickListener { onEliminar(usuario) }
        holder.btnVerAsistencia.setOnClickListener { onVerAsistencia(usuario) } // 👈 añadido
    }

    override fun getItemCount(): Int = listaUsuarios.size

    fun actualizarLista(nuevaLista: List<Usuario>) {
        listaUsuarios = nuevaLista
        notifyDataSetChanged()
    }
}