package com.example.ticketapp.data.repository

import com.example.ticketapp.data.model.AdminCredentials
import com.example.ticketapp.data.model.UserProfile
import com.example.ticketapp.data.model.UserRole
import com.example.ticketapp.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {

    companion object {
        private var adminLoggedIn = false
    }

    private val client = SupabaseClientProvider.client

    suspend fun signIn(
        username: String,
        password: String
    ): Result<UserProfile> {

        return withContext(Dispatchers.IO) {

            try {

                // ================= ADMIN LOGIN =================

                if (
                    username == AdminCredentials.USERNAME &&
                    password == AdminCredentials.PASSWORD
                ) {

                    adminLoggedIn = true

                    val admin = UserProfile(
                        id = "admin-id",
                        username = AdminCredentials.USERNAME,
                        email = AdminCredentials.EMAIL,
                        role = UserRole.ADMIN
                    )

                    return@withContext Result.success(admin)
                }

                // ================= USER LOGIN =================

                val profiles = client.postgrest["profiles"]
                    .select(
                        columns = Columns.list(
                            "email",
                            "username",
                            "role",
                            "id"
                        )
                    ) {
                        filter {
                            eq("username", username)
                        }
                    }
                    .decodeList<UserProfile>()

                if (profiles.isEmpty()) {

                    return@withContext Result.failure(
                        Exception("Username tidak ditemukan")
                    )
                }

                val userEmail = profiles.first().email

                client.auth.signInWith(Email) {

                    this.email = userEmail
                    this.password = password
                }

                val authUser =
                    client.auth.currentUserOrNull()
                        ?: return@withContext Result.failure(
                            Exception("Login gagal")
                        )

                val profile = client.postgrest["profiles"]
                    .select {
                        filter {
                            eq("id", authUser.id)
                        }
                    }
                    .decodeSingle<UserProfile>()

                Result.success(profile)

            } catch (e: Exception) {

                Result.failure(
                    Exception("Login gagal: ${e.message}")
                )
            }
        }
    }

    suspend fun register(
        email: String,
        username: String,
        password: String
    ): Result<UserProfile> {

        return withContext(Dispatchers.IO) {

            try {

                val existingUsers = client.postgrest["profiles"]
                    .select {
                        filter {
                            eq("username", username)
                        }
                    }
                    .decodeList<UserProfile>()

                if (existingUsers.isNotEmpty()) {

                    return@withContext Result.failure(
                        Exception("Username sudah digunakan")
                    )
                }

                client.auth.signUpWith(Email) {

                    this.email = email
                    this.password = password
                }

                val authUser =
                    client.auth.currentUserOrNull()
                        ?: return@withContext Result.failure(
                            Exception("Registrasi gagal")
                        )

                val newProfile = UserProfile(
                    id = authUser.id,
                    username = username,
                    email = email,
                    role = UserRole.USER
                )

                client.postgrest["profiles"]
                    .insert(newProfile)

                Result.success(newProfile)

            } catch (e: Exception) {

                Result.failure(
                    Exception("Registrasi gagal: ${e.message}")
                )
            }
        }
    }

    suspend fun signOut() {

        withContext(Dispatchers.IO) {

            try {

                adminLoggedIn = false

                client.auth.signOut()

            } catch (_: Exception) {

            }
        }
    }

    suspend fun getCurrentUser(): UserProfile? {

        return withContext(Dispatchers.IO) {

            try {

                // ================= ADMIN SESSION =================

                if (adminLoggedIn) {

                    return@withContext UserProfile(
                        id = "admin-id",
                        username = AdminCredentials.USERNAME,
                        email = AdminCredentials.EMAIL,
                        role = UserRole.ADMIN
                    )
                }

                // ================= USER SESSION =================

                val authUser =
                    client.auth.currentUserOrNull()
                        ?: return@withContext null

                client.postgrest["profiles"]
                    .select {
                        filter {
                            eq("id", authUser.id)
                        }
                    }
                    .decodeSingleOrNull<UserProfile>()

            } catch (e: Exception) {

                null
            }
        }
    }
}