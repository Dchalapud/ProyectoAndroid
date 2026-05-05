package com.example.actividadandroid.activities.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.actividadandroid.R
import com.example.actividadandroid.activities.SupabaseClient
import com.example.actividadandroid.activities.data.UsuarioRepository
import com.example.actividadandroid.activities.main.MainActivity
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import org.slf4j.MDC.put


class Registro : AppCompatActivity() {

    private lateinit var etNombres: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var etReContrasena: EditText
    private lateinit var btnRegistrarse: Button
    private lateinit var etTerminos: CheckBox


    @Serializable
    data class UsuarioData(
        val id: String,
        val nombres: String,
        val apellidos: String,
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_registro)
        val rootView = findViewById<ViewGroup>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())

            val bottomPadding = maxOf(systemBars.bottom, imeInsets.bottom)

            rootView.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                bottomPadding
            )
            insets

        }

        etNombres = findViewById(R.id.re_nombres)
        etApellidos = findViewById(R.id.re_apellidos)
        etCorreo = findViewById(R.id.re_correo)
        etContrasena = findViewById(R.id.re_contrasena)
        etReContrasena = findViewById(R.id.re_recontrasena)
        etTerminos = findViewById(R.id.re_terminos)
        btnRegistrarse = findViewById(R.id.btn_registrarse)

        btnRegistrarse.setOnClickListener {

            val nombres = etNombres.text.toString().trim()
            val apellidos = etApellidos.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()
            val reContrasena = etReContrasena.text.toString().trim()
            val terminos = etTerminos.isChecked


            if (nombres.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || reContrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (!etTerminos.isChecked) {
                Toast.makeText(this, "Debes aceptar Terminos y condiciones", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (contrasena != reContrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (contrasena.length < 8) {
                Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 8 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    SupabaseClient.client.auth.signUpWith(Email) {
                        email = correo
                        password = contrasena
                        data = buildJsonObject {
                            put("nombres", nombres)
                            put("apellidos", apellidos)
                        }
                    }

                    val userId = SupabaseClient.client.auth.currentUserOrNull()?.id?:""
                    UsuarioRepository.insertarUsuario(userId,nombres,apellidos,correo)


                    runOnUiThread {
                        Toast.makeText(this@Registro, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@Registro, Login::class.java))
                        finish()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(
                            this@Registro,
                            "Error en el registro: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}