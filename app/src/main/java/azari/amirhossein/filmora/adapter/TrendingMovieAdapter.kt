package azari.amirhossein.filmora.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ItemMediaLargeBinding
import azari.amirhossein.filmora.models.movie.ResponseTrendingMovie
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.loadImageWithShimmer
import azari.amirhossein.filmora.utils.setClickAnimation
import javax.inject.Inject

class TrendingMovieAdapter @Inject constructor() : RecyclerView.Adapter<TrendingMovieAdapter.ViewHolder>(){
    private var allGenres: List<ResponseGenresList.Genre> = emptyList()

    private var onItemClickListener: ((ResponseTrendingMovie.Result) -> Unit)? = null
    fun setOnItemClickListener(listener: (ResponseTrendingMovie.Result) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemMediaLargeBinding) : RecyclerView.ViewHolder(binding.root){
        private val originalScaleType: ImageView.ScaleType = binding.imgPoster.scaleType

        fun bind (item : ResponseTrendingMovie.Result){

            binding.apply {
                val baseUrl = Constants.Network.IMAGE_BASE_URL
                val fullPosterPath = if (item.posterPath.isNullOrEmpty()) {
                    null
                } else {
                    baseUrl + Constants.ImageSize.ORIGINAL + item.backdropPath
                }

                imgPoster.loadImageWithShimmer(
                    fullPosterPath,
                    R.drawable.image_slash_large,
                    R.drawable.image_large,
                    originalScaleType,
                    true,
                    imgShimmerContainer
                )

                txtTitle.text = item.title ?: "N/A"
                val year = item.releaseDate?.split("-")?.get(0) ?: "N/A"
                val voteAverage = item.voteAverage?.let { String.format("%.1f", it) } ?: "N/A"
                txtRating.text = voteAverage
                txtYear.text = year

                val genreAdapter = GenresAdapter()
                rvGenres.adapter = genreAdapter
                rvGenres.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

                val genres = getGenresByIds(item.genreIds)
                genreAdapter.differ.submitList(genres)
            }
            binding.root.setClickAnimation {
                onItemClickListener?.let { it(item) }
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
        val binding = ItemMediaLargeBinding.inflate(LayoutInflater.from(parent.context) ,parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    private val diffCallback = object : DiffUtil.ItemCallback<ResponseTrendingMovie.Result>(){
        override fun areItemsTheSame(
            oldItem: ResponseTrendingMovie.Result,
            newItem: ResponseTrendingMovie.Result,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ResponseTrendingMovie.Result,
            newItem: ResponseTrendingMovie.Result,
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this , diffCallback)
}