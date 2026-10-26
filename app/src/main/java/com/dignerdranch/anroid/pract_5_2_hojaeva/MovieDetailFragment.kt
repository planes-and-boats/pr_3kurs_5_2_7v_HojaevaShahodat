package com.dignerdranch.anroid.pract_5_2_hojaeva

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieDetailFragment : Fragment(R.layout.fragment_movie) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle = view.findViewById<TextView>(R.id.tvDetailTitle)
        val tvRating = view.findViewById<TextView>(R.id.tvDetailRating)
        val tvYearLength = view.findViewById<TextView>(R.id.tvDetailYearLength)
        val tvCountries = view.findViewById<TextView>(R.id.tvDetailCountries)
        val tvGenres = view.findViewById<TextView>(R.id.tvDetailGenres)
        val tvDescription = view.findViewById<TextView>(R.id.tvDetailDescription)
        val etUserNote = view.findViewById<EditText>(R.id.etUserNote)
        val btnSave = view.findViewById<Button>(R.id.btnSaveToDatabase)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
        val btnBack = view.findViewById<Button>(R.id.btnBack)

        val sharedPref = requireContext().getSharedPreferences("DATA", Context.MODE_PRIVATE)
        val dao = AppDatabase.getDatabase(requireContext()).movieDao()

        val movieJson = sharedPref.getString("CLICKED_MOVIE", null) ?: return
        Log.d("API_TEST", movieJson)
        val movie = Gson().fromJson(movieJson, Movie::class.java)

        tvTitle.text = movie.name
        tvDescription.text = movie.description ?: getString(R.string.NoMovieDescription)
        etUserNote.setText(movie.userNote)
        val ratingStr = movie.localRating?.takeIf { it.isNotBlank() } ?: "-"
        tvRating.text = getString(R.string.detailRating, ratingStr)

        val year = movie.year?.toString() ?: "-"
        val length = if (movie.movieLength != null && movie.movieLength > 0) "${movie.movieLength} мин." else null
        if (length != null) {
            tvYearLength.text = getString(R.string.detailYearAndLength, year, length)
        } else {
            tvYearLength.text = getString(R.string.detailYearOnly, year)
        }

        tvCountries.text = getString(R.string.detailCountries, movie.localCountries ?: "-")
        tvGenres.text = getString(R.string.detailGenres, movie.genre ?: "-")

        viewLifecycleOwner.lifecycleScope.launch {
            dao.getAllMovies().collect { list ->
                val found = list.any { it.id == movie.id }
                btnDelete.visibility = if (found) View.VISIBLE else View.GONE
            }
        }

        btnSave.setOnClickListener {
            movie.userNote = etUserNote.text.toString().trim()
            movie.prepareForRoom()

            viewLifecycleOwner.lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    dao.insertMovie(movie)
                }
                Toast.makeText(requireContext(), R.string.errorGoodSave, Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }

        btnDelete.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    dao.deleteMovie(movie)
                }
                Toast.makeText(requireContext(), R.string.errorGoodDelete, Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}