package azari.amirhossein.filmora.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.databinding.ItemSearchPreferencesBinding
import azari.amirhossein.filmora.models.prefences.tv.ResponseTvsList
import azari.amirhossein.filmora.utils.Constants
import coil3.load
import coil3.request.crossfade
import javax.inject.Inject

class SearchTvPreferencesAdapter @Inject constructor() :
    RecyclerView.Adapter<SearchTvPreferencesAdapter.ViewHolder>() {

    var onItemClick: ((ResponseTvsList.Result) -> Unit)? = null

    inner class ViewHolder(private val binding: ItemSearchPreferencesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseTvsList.Result) {
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
                txtTitle.text = item.name
                txtYear.text = item.firstAirDate?.split("-")?.get(0) ?: "N/A"
                txtRating.text = String.format("%.1f", item.voteAverage)

                root.setOnClickListener {
                    onItemClick?.invoke(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSearchPreferencesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    private val diffCallback = object : DiffUtil.ItemCallback<ResponseTvsList.Result>() {
        override fun areItemsTheSame(
            oldItem: ResponseTvsList.Result,
            newItem: ResponseTvsList.Result,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ResponseTvsList.Result,
            newItem: ResponseTvsList.Result,
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)
}