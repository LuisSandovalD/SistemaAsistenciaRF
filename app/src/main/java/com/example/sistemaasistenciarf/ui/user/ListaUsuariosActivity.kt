package com.example.sistemaasistenciarf.ui.user

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sistemaasistenciarf.R
import com.example.sistemaasistenciarf.data.model.Usuario
import com.example.sistemaasistenciarf.ui.user.FormularioUsuarioActivity
import com.example.sistemaasistenciarf.ui.asistencia.ListaAsistenciasActivity
import com.example.sistemaasistenciarf.viewmodel.UsuarioViewModel

class ListaUsuariosActivity : AppCompatActivity() {

    private val usuarioViewModel: UsuarioViewModel by viewModels()
    private lateinit var adaptador: UsuarioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_usuarios)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerUsuarios)

        // Inicializar el adaptador con las acciones de editar, eliminar y ver asistencia
        adaptador = UsuarioAdapter(
            listaUsuarios = emptyList(),
            onEditar = { usuario: Usuario ->
                val intent = Intent(this, FormularioUsuarioActivity::class.java)
                intent.putExtra("usuario", usuario)
                startActivity(intent)
            },
            onEliminar = { usuario: Usuario ->
                usuarioViewModel.eliminar(usuario)
                Toast.makeText(this, "✅ Usuario eliminado", Toast.LENGTH_SHORT).show()
            },
            onVerAsistencia = { usuario: Usuario ->
                val intent = Intent(this, ListaAsistenciasActivity::class.java)
                intent.putExtra("usuarioId", usuario.id)
                intent.putExtra("nombreUsuario", usuario.nombre)
                startActivity(intent)
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ListaUsuariosActivity)
            adapter = adaptador
        }

        // Observar los cambios en la lista de usuarios
        usuarioViewModel.usuarios.observe(this) { lista ->
            adaptador.actualizarLista(lista)
        }
    }
}