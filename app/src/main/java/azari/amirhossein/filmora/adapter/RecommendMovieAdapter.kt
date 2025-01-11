package azari.amirhossein.filmora.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.databinding.ItemMediaBinding
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.utils.Constants
import coil3.load
import coil3.request.crossfade
import javax.inject.Inject

class RecommendMovieAdapter @Inject constructor() :
    RecyclerView.Adapter<RecommendMovieAdapter.ViewHolder>() {

    private var allGenres: List<ResponseGenresList.Genre> = emptyList()

    inner class ViewHolder(private val binding: ItemMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseMoviesList.Result) {
            binding.apply {
                val baseUrl = Constants.Network.IMAGE_BASE_URL
                val fullPosterPath = baseUrl + Constants.ImageSize.W500 + item.posterPath

                imgShimmerContainer.visibility = View.VISIBLE
                imgPoster.visibility = View.INVISIBLE
                imgPoster.load(fullPosterPath) {
                    crossfade(true)
                    crossfade(400)
                    listener(
                        onSuccess = { _, _ ->
                            imgShimmerContainer.stopShimmer()
                            imgShimmerContainer.visibility = View.GONE
                            imgPoster.visibility = View.VISIBLE
                        },
                        onError = { _, _ ->
                            imgShimmerContainer.stopShimmer()
                            imgShimmerContainer.visibility = View.GONE
                            imgPoster.visibility = View.VISIBLE
                        }
                    )
                }

                txtTitle.text = item.title
                txtYear.text = item.releaseDate?.split("-")?.get(0) ?: "N/A"
                txtRating.text = String.format("%.1f", item.voteAverage)

                val genreAdapter = GenresAdapter()
                rvGenres.adapter = genreAdapter
                rvGenres.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

                val genres = getGenresByIds(item.genreIds)
                genreAdapter.differ.submitList(genres)
            }
        }
    }
    fun submitGenres(genres: List<ResponseGenresList.Genre>) {
        this.allGenres = genres
    }
    private fun getGenresByIds(genreIds: List<Int?>?): List<ResponseGenresList.Genre> {
        if (genreIds.isNullOrEmpty()) {
            return emptyList()
        }
        return allGenres.filter { genre -> genreIds.contains(genre.id) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in differ.currentList.indices) {
            holder.bind(differ.currentList[position])
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<ResponseMoviesList.Result>() {
        override fun areItemsTheSame(oldItem: ResponseMoviesList.Result, newItem: ResponseMoviesList.Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ResponseMoviesList.Result, newItem: ResponseMoviesList.Result): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
}