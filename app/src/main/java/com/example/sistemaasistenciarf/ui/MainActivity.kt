package com.example.sistemaasistenciarf.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.sistemaasistenciarf.R
import com.example.sistemaasistenciarf.auth.DBHelper
import com.example.sistemaasistenciarf.data.local.database.AppDatabase
import com.example.sistemaasistenciarf.data.model.UsuarioAdmin
import com.example.sistemaasistenciarf.hideSystemUI
import com.example.sistemaasistenciarf.recognition.DeteccionFacialActivity
import com.example.sistemaasistenciarf.ui.admin.EditarAdminActivity
import com.example.sistemaasistenciarf.ui.user.FormularioUsuarioActivity
import com.example.sistemaasistenciarf.ui.user.ListaUsuariosActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var db: AppDatabase
    private var adminActual: UsuarioAdmin? = null


    private val editarAdminLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        adminActual?.id?.let { idAdmin ->
            CoroutineScope(Dispatchers.IO).launch {
                val actualizado = db.adminDao().obtenerTodos().firstOrNull { it.id == idAdmin }
                runOnUiThread {
                    actualizado?.let {
                        adminActual = it
                        findViewById<TextView>(R.id.tv_saludo)?.text =
                            "Hola, ${it.nombre} ${it.apellido}"



                        val totalUsuarios = db.usuarioDao().contarUsuarios()

                        val tvUsuarios = findViewById<TextView>(R.id.tvCantidadUsuarios)

                        runOnUiThread {
                            if (totalUsuarios > 0) {
                                tvUsuarios.text = totalUsuarios.toString()
                            } else {
                                tvUsuarios.text = "Sin registros en la BD"
                            }
                        }

                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.obtenerBaseDeDatos(this)
        adminActual = intent.getSerializableExtra("admin") as? UsuarioAdmin

        configurarBottomNav()

        adminActual?.let {
            findViewById<TextView>(R.id.tv_saludo)?.text = "Hola, ${it.nombre} ${it.apellido}"
        }

        hideSystemUI()

    }



    private fun configurarBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.btn_registerUser -> {
                    startActivity(Intent(this, FormularioUsuarioActivity::class.java))
                    true
                }

                R.id.btnVerUsuarios -> {
                    startActivity(Intent(this, ListaUsuariosActivity::class.java))
                    true
                }

                R.id.btnEditarAdmin -> {
                    adminActual?.let { admin ->
                        val intent = Intent(this, EditarAdminActivity::class.java)
                        intent.putExtra("admin", admin)
                        editarAdminLauncher.launch(intent)
                    } ?: run {
                        Toast.makeText(this, "No hay administrador registrado", Toast.LENGTH_SHORT).show()
                    }
                    true
                }

                R.id.btnTomarAsistencia -> {
                    startActivity(Intent(this, DeteccionFacialActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }
}
