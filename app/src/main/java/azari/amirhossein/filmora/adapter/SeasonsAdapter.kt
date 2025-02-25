package azari.amirhossein.filmora.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ItemSeasonBinding
import azari.amirhossein.filmora.models.detail.tv.ResponseTvDetails.Season
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.loadImageWithShimmer
import azari.amirhossein.filmora.utils.setClickAnimation
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class SeasonsAdapter @Inject constructor() :
    RecyclerView.Adapter<SeasonsAdapter.ViewHolder>() {

    private var onItemClickListener: ((Season) -> Unit)? = null
    fun setOnItemClickListener(listener: (Season) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemSeasonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val originalScaleType: ImageView.ScaleType = binding.imgPoster.scaleType

        fun bind(item: Season) {
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

                // Click
                binding.root.setClickAnimation {
                    onItemClickListener?.let { it(item) }

                }
                if (item.airDate == null) {
                    imgCalendar.visibility = View.GONE
                    txtYear.visibility = View.GONE
                }
                txtYear.text = item.airDate?.let { formatDate(it) }
                txtRating.text = String.format(Locale.getDefault(), "%.1f", item.voteAverage ?: Constants.Defaults.VOTE_AVERAGE)
                txtTitle.text = item.name

            }
        }
    }

    fun formatDate(inputDate: String?): String {
        return try {
            if (inputDate.isNullOrEmpty()) {
                Constants.Message.INVALID_DATE
            } else {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                val date = inputFormat.parse(inputDate)
                if (date != null) {
                    outputFormat.format(date)
                } else {
                    Constants.Message.INVALID_DATE
                }
            }
        } catch (e: Exception) {
            Constants.Message.INVALID_DATE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSeasonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    private val diffCallback = object : DiffUtil.ItemCallback<Season>() {
        override fun areItemsTheSame(oldItem: Season, newItem: Season): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Season, newItem: Season): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
}