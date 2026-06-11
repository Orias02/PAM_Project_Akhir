package com.example.tikectapp.data.repository


import com.example.tikectapp.data.model.Promo
import com.example.tikectapp.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PromoRepository {

    private val client = SupabaseClientProvider.client

    suspend fun getAllPromos(): Result<List<Promo>> {
        return withContext(Dispatchers.IO) {
            try {

                val promos = client.postgrest["promo"]
                    .select {
                        order("created_at", Order.DESCENDING)
                    }
                    .decodeList<Promo>()

                Result.success(promos)

            } catch (e: Exception) {

                Result.failure(
                    Exception("Gagal mengambil promo: ${e.message}")
                )
            }
        }
    }

    suspend fun addPromo(
        promo: Promo
    ): Result<Promo> {

        return withContext(Dispatchers.IO) {
            try {

                val inserted = client.postgrest["promo"]
                    .insert(promo) {
                        select()
                    }
                    .decodeSingle<Promo>()

                Result.success(inserted)

            } catch (e: Exception) {

                Result.failure(
                    Exception("Gagal menambah promo: ${e.message}")
                )
            }
        }
    }

    suspend fun updatePromo(
        promo: Promo
    ): Result<Promo> {

        return withContext(Dispatchers.IO) {
            try {

                val updated = client.postgrest["promo"]
                    .update(promo) {
                        filter {
                            eq("id", promo.id)
                        }
                        select()
                    }
                    .decodeSingle<Promo>()

                Result.success(updated)

            } catch (e: Exception) {

                Result.failure(
                    Exception("Gagal mengupdate promo: ${e.message}")
                )
            }
        }
    }

    suspend fun deletePromo(
        id: Long
    ): Result<Unit> {

        return withContext(Dispatchers.IO) {
            try {

                client.postgrest["promo"]
                    .delete {
                        filter {
                            eq("id", id)
                        }
                    }

                Result.success(Unit)

            } catch (e: Exception) {

                Result.failure(
                    Exception("Gagal menghapus promo: ${e.message}")
                )
            }
        }
    }
}