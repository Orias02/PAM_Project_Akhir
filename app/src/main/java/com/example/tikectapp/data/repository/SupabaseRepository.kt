package com.example.ticketapp.data.remote

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

/**
 * Supabase Client Singleton
 *
 * SETUP INSTRUCTIONS:
 * 1. Buka https://supabase.com dan buat project baru
 * 2. Di dashboard Supabase, pergi ke Settings > API
 * 3. Copy "Project URL" dan "anon/public key"
 * 4. Ganti SUPABASE_URL dan SUPABASE_KEY di bawah ini
 *
 * DATABASE SETUP (jalankan di Supabase SQL Editor):
 * Lihat file supabase_setup.sql di root project
 */
object SupabaseClientProvider {

    // ⚠️ GANTI dengan URL dan KEY project Supabase kamu
    private const val SUPABASE_URL = "https://zllhqerezatlvtehnrvf.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InpsbGhxZXJlemF0bHZ0ZWhucnZmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzg3Njc3MDMsImV4cCI6MjA5NDM0MzcwM30.wz5XjCu6OSerdRrba_eQAvHIMq_Ri-1XJNTJrLD6964"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }
}