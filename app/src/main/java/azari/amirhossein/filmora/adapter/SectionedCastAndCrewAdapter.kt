package azari.amirhossein.filmora.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ItemCastAndCrewHeaderBinding
import azari.amirhossein.filmora.databinding.ItemCelebrityRectangleBinding
import azari.amirhossein.filmora.databinding.ItemDepartmentHeaderBinding
import azari.amirhossein.filmora.models.detail.CastAndCrewListItem
import azari.amirhossein.filmora.models.detail.ResponseCredit
import azari.amirhossein.filmora.utils.Constants
import azari.amirhossein.filmora.utils.loadImageWithShimmer
import azari.amirhossein.filmora.utils.setClickAnimation
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SectionedCastAndCrewAdapter @Inject constructor(@ApplicationContext private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<CastAndCrewListItem> = emptyList()
    private var onItemClickListener: ((Any) -> Unit)? = null

    fun setOnItemClickListener(listener: (Any) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemCastAndCrewHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                HeaderViewHolder(binding)
            }
            VIEW_TYPE_JOB_HEADER -> {
                val binding = ItemDepartmentHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                JobHeaderViewHolder(binding)
            }
            else -> {
                val binding = ItemCelebrityRectangleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                PersonViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is CastAndCrewListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is CastAndCrewListItem.JobHeader -> (holder as JobHeaderViewHolder).bind(item)
            is CastAndCrewListItem.CastItem -> (holder as PersonViewHolder).bindCast(item.cast)
            is CastAndCrewListItem.CrewItem -> (holder as PersonViewHolder).bindCrew(item.crew)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CastAndCrewListItem.Header -> VIEW_TYPE_HEADER
            is CastAndCrewListItem.JobHeader -> VIEW_TYPE_JOB_HEADER
            is CastAndCrewListItem.CastItem,
            is CastAndCrewListItem.CrewItem -> VIEW_TYPE_PERSON
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(credit: ResponseCredit) {
        val newItems = mutableListOf<CastAndCrewListItem>()

        // Add Cast section
        if (!credit.cast.isNullOrEmpty()) {
            // Add Cast header
            newItems.add(CastAndCrewListItem.Header("Cast"))
            // Add all cast members
            credit.cast.forEach { cast ->
                newItems.add(CastAndCrewListItem.CastItem(cast!!))
            }
        }

        // Add Crew section grouped by department
        if (!credit.crew.isNullOrEmpty()) {
            // Add Crew header
            newItems.add(CastAndCrewListItem.Header("Crew"))

            // Group crew members by department
            val crewByDepartment = credit.crew.groupBy { it.department }

            // Add each department and its members
            crewByDepartment.forEach { (department, crewMembers) ->
                // Add department header
                newItems.add(CastAndCrewListItem.JobHeader(department!!))
                // Add crew members under this department
                crewMembers.forEach { crew ->
                    newItems.add(CastAndCrewListItem.CrewItem(crew))
                }
            }
        }

        val diffCallback = CreditSectionDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    inner class HeaderViewHolder(
        private val binding: ItemCastAndCrewHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CastAndCrewListItem.Header) {
            binding.txtHeader.text = item.title
        }
    }

    inner class JobHeaderViewHolder(
        private val binding: ItemDepartmentHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CastAndCrewListItem.JobHeader) {
            binding.txtDepartment.text = item.job
        }
    }

    inner class PersonViewHolder(
        private val binding: ItemCelebrityRectangleBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val originalScaleType: ImageView.ScaleType = binding.ivProfile.scaleType

        fun bindCast(cast: ResponseCredit.Cast) {
            binding.apply {
                val baseUrl = Constants.Network.IMAGE_BASE_URL
                val fullPosterPath = if (cast.profilePath.isNullOrEmpty()) {
                    null
                } else {
                    baseUrl + Constants.ImageSize.ORIGINAL + cast.profilePath
                }

                ivProfile.loadImageWithShimmer(
                    fullPosterPath,
                    R.drawable.user,
                    R.drawable.user,
                    originalScaleType,
                    false,
                    imgShimmerContainer
                )

                root.setClickAnimation {
                    onItemClickListener?.invoke(cast)
                }

                tvCharacterName.text = cast.name
                tvCharacterDescription.text = cast.knownForDepartment
            }
        }

        fun bindCrew(crew: ResponseCredit.Crew) {
            binding.apply {
                val baseUrl = Constants.Network.IMAGE_BASE_URL
                val fullPosterPath = if (crew.profilePath.isNullOrEmpty()) {
                    null
                } else {
                    baseUrl + Constants.ImageSize.ORIGINAL + crew.profilePath
                }

                ivProfile.loadImageWithShimmer(
                    fullPosterPath,
                    R.drawable.user,
                    R.drawable.user,
                    originalScaleType,
                    false,
                    imgShimmerContainer
                )

                root.setClickAnimation {
                    onItemClickListener?.invoke(crew)
                }

                tvCharacterName.text = crew.name
                tvCharacterDescription.text = crew.knownForDepartment
            }
        }
    }

    private class CreditSectionDiffCallback(
        private val oldList: List<CastAndCrewListItem>,
        private val newList: List<CastAndCrewListItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].getItemId() == newList[newItemPosition].getItemId()
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }


    companion object {
        // Changed from private to public
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_JOB_HEADER = 1
        const val VIEW_TYPE_PERSON = 2
    }
}