package azari.amirhossein.filmora.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ItemMediaBinding
import azari.amirhossein.filmora.models.prefences.ResponseGenresList
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.loadImageWithShimmer
import javax.inject.Inject

class MayLikeMovieAdapter @Inject constructor() :
    RecyclerView.Adapter<MayLikeMovieAdapter.ViewHolder>() {

    private var allGenres: List<ResponseGenresList.Genre> = emptyList()

    private var onItemClickListener: ((ResponseMoviesList.Result) -> Unit)? = null
    fun setOnItemClickListener(listener: (ResponseMoviesList.Result) -> Unit) {
        onItemClickListener = listener
    }


    inner class ViewHolder(private val binding: ItemMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val originalScaleType: ImageView.ScaleType = binding.imgPoster.scaleType

        fun bind(item: ResponseMoviesList.Result) {
            binding.apply {
                val baseUrl = Constants.Network.IMAGE_BASE_URL
                val fullPosterPath = if (item.posterPath.isNullOrEmpty()) {
                    null
                } else {
                    baseUrl + Constants.ImageSize.ORIGINAL + item.posterPath
                }

                imgPoster.loadImageWithShimmer(
                    fullPosterPath,
                    R.drawable.image_slash_medium,
                    R.drawable.image_medium,
                    originalScaleType,
                    true,
                    imgShimmerContainer
                )


                txtTitle.text = item.title
                txtYear.text = item.releaseDate?.split("-")?.get(0) ?: "N/A"
                txtRating.text = String.format("%.1f", item.voteAverage)

                val genreAdapter = GenresAdapter()
                rvGenres.adapter = genreAdapter
                rvGenres.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

                val genres = getGenresByIds(item.genreIds)
                genreAdapter.differ.submitList(genres)
            }
            // Click
            binding.root.setOnClickListener {
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