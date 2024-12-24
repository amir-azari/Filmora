package azari.amirhossein.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ItemPreferencesBinding
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import coil3.load
import coil3.request.crossfade
import javax.inject.Inject

class MoviePreferencesAdapter @Inject constructor() : RecyclerView.Adapter<MoviePreferencesAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemPreferencesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseMoviesList.Result) {
            binding.apply {
                    if (item.id == -1){
                        imgPoster.setImageResource(R.drawable.image) // Add a placeholder image
                        txtTitle.text = "Movie name"
                        txtYear.text = "--"
                        txtRating.text = "-.-"
                        imgPoster.scaleType = ImageView.ScaleType.FIT_CENTER
                    }else {
                         val baseUrl = "https://image.tmdb.org/t/p/w500"
                         val fullPosterPath = baseUrl + item.posterPath
                         imgPoster.load(fullPosterPath) {
                             crossfade(true)
                             crossfade(400)
                         }
                        imgPoster.scaleType = ImageView.ScaleType.CENTER_CROP

                        txtTitle.text = item.title
                         txtYear.text = item.releaseDate?.split("-")?.get(0) ?: "N/A"
                         txtRating.text = String.format("%.1f", item.voteAverage)
                    }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPreferencesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 5
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