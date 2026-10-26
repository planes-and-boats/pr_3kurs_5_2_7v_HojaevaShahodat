package com.dignerdranch.anroid.pract_5_2_hojaeva

import android.content.Context
import kotlinx.coroutines.flow.Flow

class MovieRepository(private val dao: MovieDao) {
    val allMovies: Flow<List<Movie>> = dao.getAllMovies()

    suspend fun insert(movie: Movie) = dao.insertMovie(movie)
    suspend fun update(movie: Movie) = dao.updateMovie(movie)
    suspend fun delete(movie: Movie) = dao.deleteMovie(movie)
}

object RepoProvider {
    @Volatile private var repo: MovieRepository? = null
    fun get(context: Context): MovieRepository =
        repo ?: synchronized(this) {
            val dao = AppDatabase.getDatabase(context).movieDao()
            MovieRepository(dao).also { repo = it }
        }
}