package com.example.ticketapp.data.repository

import com.example.ticketapp.data.model.Movie
import com.example.ticketapp.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository untuk semua operasi CRUD film.
 * Menggunakan table "movies" di Supabase.
 */
class MovieRepository {

    private val client = SupabaseClientProvider.client

    /**
     * Ambil semua film, urut berdasarkan created_at terbaru
     */
    suspend fun getAllMovies(): Result<List<Movie>> {
        return withContext(Dispatchers.IO) {
            try {
                val movies = client.postgrest["movies"]
                    .select {
                        order("created_at", Order.DESCENDING)
                    }
                    .decodeList<Movie>()
                Result.success(movies)
            } catch (e: Exception) {
                Result.failure(Exception("Gagal mengambil data film: ${e.message}"))
            }
        }
    }

    /**
     * Ambil film berdasarkan genre
     */
    suspend fun getMoviesByGenre(genre: String): Result<List<Movie>> {
        return withContext(Dispatchers.IO) {
            try {
                val movies = client.postgrest["movies"]
                    .select {
                        filter { eq("genre", genre) }
                        order("created_at", Order.DESCENDING)
                    }
                    .decodeList<Movie>()
                Result.success(movies)
            } catch (e: Exception) {
                Result.failure(Exception("Gagal memfilter film: ${e.message}"))
            }
        }
    }

    /**
     * Ambil detail satu film berdasarkan ID
     */
    suspend fun getMovieById(id: String): Result<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val movie = client.postgrest["movies"]
                    .select {
                        filter { eq("id", id) }
                    }
                    .decodeSingle<Movie>()
                Result.success(movie)
            } catch (e: Exception) {
                Result.failure(Exception("Film tidak ditemukan: ${e.message}"))
            }
        }
    }

    /**
     * Tambah film baru (Admin only)
     */
    suspend fun addMovie(movie: Movie): Result<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val inserted = client.postgrest["movies"]
                    .insert(movie) {
                        select()
                    }
                    .decodeSingle<Movie>()
                Result.success(inserted)
            } catch (e: Exception) {
                Result.failure(Exception("Gagal menambah film: ${e.message}"))
            }
        }
    }

    /**
     * Update data film (Admin only)
     */
    suspend fun updateMovie(movie: Movie): Result<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val updated = client.postgrest["movies"]
                    .update(movie) {
                        filter { eq("id", movie.id) }
                        select()
                    }
                    .decodeSingle<Movie>()
                Result.success(updated)
            } catch (e: Exception) {
                Result.failure(Exception("Gagal mengupdate film: ${e.message}"))
            }
        }
    }

    /**
     * Hapus film berdasarkan ID (Admin only)
     */
    suspend fun deleteMovie(id: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                client.postgrest["movies"]
                    .delete {
                        filter { eq("id", id) }
                    }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(Exception("Gagal menghapus film: ${e.message}"))
            }
        }
    }

    /**
     * Cari film berdasarkan judul
     */
    suspend fun searchMovies(query: String): Result<List<Movie>> {
        return withContext(Dispatchers.IO) {
            try {
                val movies = client.postgrest["movies"]
                    .select {
                        filter {
                            ilike("title", "%$query%")
                        }
                    }
                    .decodeList<Movie>()
                Result.success(movies)
            } catch (e: Exception) {
                Result.failure(Exception("Pencarian gagal: ${e.message}"))
            }
        }
    }
}