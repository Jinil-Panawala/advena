package com.example.advena.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.auth.Auth

// Connect to supabase db
object SupabaseClient {

    val client = createSupabaseClient(
        supabaseUrl = ApiKeys.SUPABASE_URL,
        supabaseKey = ApiKeys.SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Realtime)
    }
}

