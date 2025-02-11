package azari.amirhossein.filmora.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ItemCelebrityRectangleBinding
import azari.amirhossein.filmora.models.celebtiry.ResponsePeopleType
import azari.amirhossein.filmora.models.celebtiry.ResponsePopularCelebrity
import azari.amirhossein.filmora.models.celebtiry.ResponseTrendingCelebrity
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.loadImageWithShimmer
import javax.inject.Inject

class PeopleAdapter @Inject constructor() :
    PagingDataAdapter<ResponsePeopleType, PeopleAdapter.PeopleViewHolder>(PeopleDiffCallback()) {

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        val binding = ItemCelebrityRectangleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PeopleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class PeopleViewHolder(private val binding: ItemCelebrityRectangleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val originalScaleType: ImageView.ScaleType = binding.ivProfile.scaleType

        fun bind(item: ResponsePeopleType?) {
            if (item == null) return

            when (item) {
                is ResponsePeopleType.Trending -> bindTrending(item.result)
                is ResponsePeopleType.Popular -> bindPopular(item.result)
            }
        }

        private fun bindTrending(item: ResponseTrendingCelebrity.Result) {
            binding.apply {
                val baseUrl = Constants.Network.IMAGE_BASE_URL
                val fullPosterPath = if (item.profilePath.isNullOrEmpty()) {
                    null
                } else {
                    baseUrl + Constants.ImageSize.ORIGINAL + item.profilePath
                }

                ivProfile.loadImageWithShimmer(
                    fullPosterPath,
                    R.drawable.user,
                    R.drawable.user,
                    originalScaleType,
                    false,
                    imgShimmerContainer
                )


                // Click
                root.setOnClickListener {
                    onItemClickListener?.invoke(item.id)
                }

                tvCharacterName.text = item.name
                tvCharacterDescription.text = item.knownForDepartment
            }
        }

        private fun bindPopular(item: ResponsePopularCelebrity.Result) {
            binding.apply {
                val baseUrl = Constants.Network.IMAGE_BASE_URL
                val fullPosterPath = if (item.profilePath.isNullOrEmpty()) {
                    null
                } else {
                    baseUrl + Constants.ImageSize.ORIGINAL + item.profilePath
                }

                ivProfile.loadImageWithShimmer(
                    fullPosterPath,
                    R.drawable.user,
                    R.drawable.user,
                    originalScaleType,
                    false,
                    imgShimmerContainer
                )


                // Click
                root.setOnClickListener {
                    onItemClickListener?.invoke(item.id)
                }

                tvCharacterName.text = item.name
                tvCharacterDescription.text = item.knownForDepartment
            }
        }
    }

    class PeopleDiffCallback : DiffUtil.ItemCallback<ResponsePeopleType>() {
        override fun areItemsTheSame(
            oldItem: ResponsePeopleType,
            newItem: ResponsePeopleType,
        ): Boolean {
            return when {
                oldItem is ResponsePeopleType.Trending && newItem is ResponsePeopleType.Trending ->
                    oldItem.result.id == newItem.result.id

                oldItem is ResponsePeopleType.Popular && newItem is ResponsePeopleType.Popular ->
                    oldItem.result.id == newItem.result.id

                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: ResponsePeopleType,
            newItem: ResponsePeopleType,
        ): Boolean {
            return oldItem == newItem
        }
    }
}

