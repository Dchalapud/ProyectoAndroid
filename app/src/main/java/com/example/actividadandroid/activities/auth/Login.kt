package com.example.actividadandroid.activities.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.example.actividadandroid.R
import com.example.actividadandroid.activities.SupabaseClient
import com.example.actividadandroid.activities.main.MainActivity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.launch
import java.nio.channels.spi.AsynchronousChannelProvider.provider
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.actividadandroid.activities.data.CredencialesManager

class Login : AppCompatActivity() {

    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnIngresar: Button
    private lateinit var tvRegistro: TextView
    private lateinit var tvRecuperarContrasena: TextView
    private lateinit var tvIngresarConGoogle: TextView
    private lateinit var tvHuella: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val rootView = findViewById<ScrollView>(R.id.main)
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

        etCorreo = findViewById(R.id.etUsuario)
        etContrasena = findViewById(R.id.etPassword)
        btnIngresar = findViewById(R.id.btnIngresar)
        tvRegistro = findViewById(R.id.tvRegistro)
        tvRecuperarContrasena = findViewById(R.id.tvRecuperarC)
        tvIngresarConGoogle = findViewById(R.id.tvIngresarConGoogle)

        // Referencia al boton de huella
        tvHuella = findViewById(R.id.in_huella)

        // Llamar en onCreate para verificar al crear la Activity
        configurarVisibilidadHuella()

        // Listener del boton de huella
        tvHuella.setOnClickListener { mostrarDialogoHuella() }



        findViewById<TextView>(R.id.tvRegistro).setOnClickListener {
            startActivity(Intent(this, Registro::class.java))
        }

        findViewById<Button>(R.id.btnIngresar).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Inicio con datos Supabase

        btnIngresar.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()


            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
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

            // Validacion con base de datos Supa

            lifecycleScope.launch {

                try {
                    SupabaseClient.client.auth.signInWith(Email) {
                        email = correo
                        password = contrasena

                    }
                    runOnUiThread {
                        Toast.makeText(this@Login, "Inicio de sesión exitoso", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this@Login, MainActivity::class.java))
                        finish()
                    }
                    // AGREGAR ESTAS DOS LINEAS:
                    CredencialesManager.guardarCredenciales(
                        this@Login, correo, contrasena
                    )
                    irAPantallaPrincipal()
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(
                            this@Login,
                            "Error en el inicio de sesión: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        // Inicio sesion con Google

        tvIngresarConGoogle.setOnClickListener {
            iniciarSesionConGoogle()
        }
    }

    override fun onResume() {
        super.onResume()
        configurarVisibilidadHuella()
    }

    private fun iniciarSesionConGoogle() {
        lifecycleScope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId("509686705875-m8cgtvk8hhgvvtoupftk472bf8u2rm2t.apps.googleusercontent.com")
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                val credentialManager = CredentialManager.create(this@Login)
                val result = credentialManager.getCredential(this@Login, request)
                val googleIdTokenCredential = GoogleIdTokenCredential. createFrom(result.credential.data)

                SupabaseClient.client.auth.signInWith(IDToken) {
                    idToken = googleIdTokenCredential.idToken
                    provider = Google
                }

                runOnUiThread {
                    Toast.makeText(this@Login, "Inicio de sesión con Google exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@Login, MainActivity::class.java))
                    finish()
                }
            }catch (e: Exception){
                runOnUiThread {
                    Toast.makeText(this@Login, "Error en el inicio de sesión con Google: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    //FUNCIONES

    //irAPantallaPrincipal
    private fun irAPantallaPrincipal() {
        runOnUiThread {
            startActivity(Intent(this@Login, MainActivity::class.java))
            finishAffinity()
        }
    }

    //VisibilidadHuella

    private fun configurarVisibilidadHuella() {
        // Verificar si hay credenciales guardadas localmente
        val huellaActiva = CredencialesManager.huellaActiva(this)
        // Verificar si el dispositivo tiene sensor biometrico disponible
        val biometricManager = BiometricManager.from(this)
        val biometriaDisponible = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        ) == BiometricManager.BIOMETRIC_SUCCESS
        // Mostrar solo si AMBAS condiciones son verdaderas
        tvHuella.visibility = if (huellaActiva && biometriaDisponible)
            android.view.View.VISIBLE
        else
            android.view.View.GONE
    }

    //mostrarDialogoHuella

    private fun mostrarDialogoHuella() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                // Huella reconocida correctamente
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    val correo = CredencialesManager.obtenerCorreo(this@Login)
                    val contrasena =
                        CredencialesManager.obtenerContrasena(this@Login)

                    if (correo != null && contrasena != null) {
                        // Hacer signIn real con las credenciales guardadas
                        lifecycleScope.launch {
                            try {
                                SupabaseClient.client.auth.signInWith(Email) {
                                    email = correo
                                    password = contrasena
                                }
                                irAPantallaPrincipal()
                            } catch (e: Exception) {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@Login,
                                        "Error al iniciar sesion: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    } else {
                        // No hay credenciales, limpiar y ocultar la huella
                        Toast.makeText(
                            this@Login,
                            "Sesion expirada. Inicia sesion con tu correo.",
                            Toast.LENGTH_LONG
                        ).show()
                        CredencialesManager.limpiarCredenciales(this@Login)
                        configurarVisibilidadHuella()
                    }
                }
                // Error irrecuperable del sensor
                override fun onAuthenticationError(
                    errorCode: Int, errString: CharSequence
                ) {
                    // Ignorar si el usuario cancelo voluntariamente
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                        errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        Toast.makeText(
                            this@Login,
                            "Error biometrico: $errString",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                // Huella leida pero no reconocida, puede reintentar
                override fun onAuthenticationFailed() {
                    Toast.makeText(this@Login,
                        "Huella no reconocida, intenta de nuevo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        // Configuracion visual del dialogo nativo de Android
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Acceso con huella")
            .setSubtitle("Usa tu huella dactilar para ingresar")
            .setNegativeButtonText("Cancelar")
            .build()
        biometricPrompt.authenticate(promptInfo)
    }


}