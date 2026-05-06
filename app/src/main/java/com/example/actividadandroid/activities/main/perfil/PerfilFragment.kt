package com.example.actividadandroid.activities.main.perfil

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.example.actividadandroid.R
import com.example.actividadandroid.activities.data.UsuarioRepository
import kotlinx.coroutines.launch

class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvNombre = view.findViewById<TextView>(R.id.tv_nombre)
        val tvCorreo = view.findViewById<TextView>(R.id.tv_correo)
        val ivFoto = view.findViewById<ImageView>(R.id.iv_foto_perfil)
        val btnEditar = view.findViewById<Button>(R.id.btn_editar_perfil)
        val tvRol = view.findViewById<TextView>(R.id.tv_rol)

        // 🔹 Cargar datos del usuario
        lifecycleScope.launch {
            val usuario = UsuarioRepository.obtenerUsuarioActual()

            usuario?.let {
                tvNombre.text = "${it.nombres} ${it.apellidos}"
                tvCorreo.text = it.correo ?: "Sin correo"
                tvRol.text = "Rol: ${it.rol}"

                // Cargar imagen con Coil
                ivFoto.load(it.foto_url) {
                    placeholder(R.mipmap.ic_launcher_round)
                    error(R.mipmap.ic_launcher_round)
                    transformations(CircleCropTransformation())
                }
            }
        }

        // 🔹 Ir a editar perfil
        btnEditar.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EditarPerfilFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}