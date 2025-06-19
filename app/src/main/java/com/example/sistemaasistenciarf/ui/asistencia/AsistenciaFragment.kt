package com.example.sistemaasistenciarf.ui.asistencia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sistemaasistenciarf.databinding.FragmentAsistenciaBinding
import com.example.sistemaasistenciarf.recognition.FaceDetectorManager
import com.example.sistemaasistenciarf.data.model.Asistencia

class AsistenciaFragment : Fragment() {

    private lateinit var binding: FragmentAsistenciaBinding
    private lateinit var viewModel: AsistenciaViewModel
    private lateinit var detectorManager: FaceDetectorManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAsistenciaBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[AsistenciaViewModel::class.java]

        viewModel.asistencias.observe(viewLifecycleOwner) {
            binding.rvAsistencias.adapter = AsistenciaAdapter(it)
        }

        detectorManager = FaceDetectorManager(requireContext(), viewLifecycleOwner, binding.previewView) { resultado ->
            val (nombre, fecha, hora) = resultado.split(",")
            viewModel.registrarAsistencia(Asistencia(nombre = nombre, fecha = fecha, hora = hora))
        }

        detectorManager.iniciar()
        return binding.root
    }
}