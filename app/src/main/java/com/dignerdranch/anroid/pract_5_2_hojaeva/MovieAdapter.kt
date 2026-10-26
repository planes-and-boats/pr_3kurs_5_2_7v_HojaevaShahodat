package com.dignerdranch.anroid.pract_5_2_hojaeva

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovieAdapter(
    private var movies: List<Movie>,
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>(){

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvItemTitle)
        val tvYear: TextView = view.findViewById(R.id.tvItemYear)
        val tvGenre: TextView = view.findViewById(R.id.tvItemGenre)
        val tvLength: TextView = view.findViewById(R.id.tvItemLength)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]

        holder.tvTitle.text = movie.name
        holder.tvYear.text = movie.year?.toString() ?: "-"
        holder.tvGenre.text = holder.itemView.context.getString(R.string.detailGenres,
            movie.genre?: R.string.NoMovieGenre)

        if (movie.movieLength != null && movie.movieLength > 0) {
            holder.tvLength.text = holder.itemView.context.getString(R.string.itemLength, movie.movieLength.toString())
            holder.tvLength.visibility = View.VISIBLE
        } else {
            holder.tvLength.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onItemClick(movie)
        }
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    fun updateData(newMovies: List<Movie>) {
        this.movies = newMovies
        notifyDataSetChanged()
    }
}