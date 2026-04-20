package com.example.actividadandroid.activities

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.supabaseJson


object SupabaseClient {

    val client = createSupabaseClient(
        supabaseUrl = "https://aplwzbowwgwkscwxupli.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFwbHd6Ym93d2d3a3Njd3h1cGxpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzY0NTg3OTUsImV4cCI6MjA5MjAzNDc5NX0.WvfbYfaCnIkDHqxNRn80ogM8DcKBGFid9Lo42jcd_6I"
    )

    {
        install(Postgrest)
        install(Auth)
    }


}