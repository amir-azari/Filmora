package azari.amirhossein.filmora.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.databinding.ItemTrendingAllBinding
import azari.amirhossein.filmora.models.home.ResponseTrendingList
import azari.amirhossein.filmora.models.prefences.movie.ResponseMoviesList
import azari.amirhossein.filmora.utils.Constants
import coil3.load
import coil3.request.crossfade
import javax.inject.Inject

class TrendingAllAdapter @Inject constructor() : RecyclerView.Adapter<TrendingAllAdapter.ViewHolder>(){

    private var onItemClickListener: ((ResponseTrendingList.Result) -> Unit)? = null
    fun setOnItemClickListener(listener: (ResponseTrendingList.Result) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemTrendingAllBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind (item : ResponseTrendingList.Result){

            binding.apply {
                val baseUrl = Constants.Network.IMAGE_BASE_URL
                val fullPosterPath = baseUrl + Constants.ImageSize.ORIGINAL + item.backdropPath
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

                txtName.text = when {
                    item.mediaType == "tv" -> item.name
                    else -> item.title
                } ?: "N/A"
                val year = when {
                    item.mediaType == "tv" -> item.firstAirDate?.split("-")?.get(0)
                    else -> item.releaseDate?.split("-")?.get(0)
                } ?: "N/A"
                val voteAverage = item.voteAverage?.let { String.format("%.1f", it) } ?: "N/A"
                txtRating.text = voteAverage
                txtYear.text = year
            }
            binding.root.setOnClickListener {
                onItemClickListener?.let { it(item) }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTrendingAllBinding.inflate(LayoutInflater.from(parent.context) ,parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    private val diffCallback = object : DiffUtil.ItemCallback<ResponseTrendingList.Result>(){
        override fun areItemsTheSame(
            oldItem: ResponseTrendingList.Result,
            newItem: ResponseTrendingList.Result,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ResponseTrendingList.Result,
            newItem: ResponseTrendingList.Result,
        ): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this , diffCallback)
}