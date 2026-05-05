package com.example.actividadandroid.activities.data

import com.example.actividadandroid.activities.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.Serializable


object UsuarioRepository {
    @Serializable
    data class UsuarioData(
        val id: String,
        val nombre: String,
        val apellido: String,
        val correo: String? = null,
        val rol: String = "cliente",
        val foto_url: String? = null
    )

    suspend fun existeUsuario(userId: String): Boolean {
        return try {
            val resultado = SupabaseClient.client
                .postgrest["usuarios"]
                .select(Columns.raw("id")){
                    filter{eq("id", userId)}
                }
                .decodeList<Map<String,String>>()
            resultado.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun insertarUsuario(id: String, nombre: String, apellido: String, correo: String){
        SupabaseClient.client.postgrest["usuarios"].insert(
            UsuarioData(
                id = id,
                nombre = nombre,
                apellido = apellido,
                correo = correo
            )
        )
    }

    suspend fun obtenerUsuario(userId: String): UsuarioData? {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return null
        return try {
            SupabaseClient.client
                .postgrest["usuarios"]
                .select {
                    filter { eq("id", userId) }
                }
                .decodeSingle<UsuarioData>()
            }catch (e: Exception){
                null
        }
    }


    suspend fun obtenerRolActual(): String{
        return try {
            val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return "cliente"
            val resultado = SupabaseClient.client
                .postgrest["usuarios"]
                .select(Columns.raw("rol")){
                    filter{eq("id", userId)}
                }
                .decodeList<Map<String,String>>()
            resultado.firstOrNull()?.get("rol") ?: "cliente"
            }catch (e: Exception){
            "cliente"
        }
    }
}