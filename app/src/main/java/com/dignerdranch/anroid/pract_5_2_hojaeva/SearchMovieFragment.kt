package com.dignerdranch.anroid.pract_5_2_hojaeva

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.URLEncoder

class SearchMovieFragment : Fragment(R.layout.fragment_search_movie) {

    private val apiKey = "5FNX25A-R5Z45ZW-PGCPZAM-WFB6BAZ"

    private lateinit var movieAdapter: MovieAdapter
    private val movieLines = ArrayList<Movie>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etSearchQuery = view.findViewById<EditText>(R.id.etSearchQuery)
        val btnSearch = view.findViewById<Button>(R.id.btnSearch)
        val rvMovies = view.findViewById<RecyclerView>(R.id.rvMovies)
        val btnGoToFavorites = view.findViewById<Button>(R.id.btnGoToFavorites)

        val sharedPref = requireContext().getSharedPreferences("DATA", Context.MODE_PRIVATE)

        rvMovies.layoutManager = LinearLayoutManager(requireContext())

        movieAdapter = MovieAdapter(movieLines) { selectedMovie ->
            sharedPref.edit()
                .putString("CLICKED_MOVIE", Gson().toJson(selectedMovie))
                .apply()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragments_parent, MovieDetailFragment())
                .addToBackStack(null)
                .commit()
        }
        rvMovies.adapter = movieAdapter

        btnSearch.setOnClickListener {
            val query = etSearchQuery.text.toString().trim()

            if (query.isEmpty()) {
                Toast.makeText(requireContext(), R.string.errorEmptySearch, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val encoded = URLEncoder.encode(query, "UTF-8")
            val url = "https://api.poiskkino.dev/v1.5/movie/search?query=$encoded"

            val queue = Volley.newRequestQueue(requireContext())

            val request = object : JsonObjectRequest(
                Request.Method.GET, url, null,
                { response ->
                    try {
                        val docs = response.getJSONArray("docs")
                        movieLines.clear()

                        if (docs.length() > 0) {
                            val jsonString = docs.toString()
                            Log.d("API_TEST", jsonString)
                            sharedPref.edit().putString("LAST_MOVIE", jsonString).apply()

                            val type = object : TypeToken<ArrayList<Movie>>() {}.type
                            val parsed: ArrayList<Movie> = Gson().fromJson(jsonString, type)

                            parsed.forEach { it.prepareForRoom() }
                            movieLines.addAll(parsed)
                            movieAdapter.updateData(movieLines)
                        } else {
                            movieAdapter.updateData(movieLines)
                            Toast.makeText(requireContext(), R.string.errorEmptyResult, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), R.string.errorNetwork, Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    error.printStackTrace()
                    Toast.makeText(requireContext(), R.string.errorNetwork, Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getHeaders(): MutableMap<String, String> = hashMapOf("X-API-KEY" to apiKey)
            }

            queue.add(request)
        }

        btnGoToFavorites.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragments_parent, FavoritesFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}