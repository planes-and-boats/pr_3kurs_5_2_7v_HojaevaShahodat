package com.dignerdranch.anroid.pract_5_2_hojaeva

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private lateinit var movieAdapter: MovieAdapter
    private val favoriteMovies = ArrayList<Movie>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<Button>(R.id.btnBack)
        val rvFavorites = view.findViewById<RecyclerView>(R.id.rvFavorites)
        val sharedPref = requireContext().getSharedPreferences("DATA", Context.MODE_PRIVATE)
        val dao = AppDatabase.getDatabase(requireContext()).movieDao()

        rvFavorites.layoutManager = LinearLayoutManager(requireContext())

        movieAdapter = MovieAdapter(favoriteMovies) { selectedMovie ->
            sharedPref.edit()
                .putString("CLICKED_MOVIE", Gson().toJson(selectedMovie))
                .apply()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragments_parent, MovieDetailFragment())
                .addToBackStack(null)
                .commit()
        }
        rvFavorites.adapter = movieAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            dao.getAllMovies().collect { list ->
                favoriteMovies.clear()
                favoriteMovies.addAll(list)
                movieAdapter.updateData(favoriteMovies)
            }
        }

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}