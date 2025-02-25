package azari.amirhossein.filmora.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import azari.amirhossein.filmora.R
import azari.amirhossein.filmora.databinding.ItemCreditBinding
import azari.amirhossein.filmora.databinding.ItemDepartmentHeaderBinding
import azari.amirhossein.filmora.databinding.ItemYearHeaderBinding
import azari.amirhossein.filmora.models.detail.people.CreditItem
import azari.amirhossein.filmora.models.detail.people.CreditListItem
import azari.amirhossein.filmora.models.detail.people.DepartmentSection
import azari.amirhossein.filmora.utils.setClickAnimation
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SectionedCreditsAdapter @Inject constructor(@ApplicationContext private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<CreditListItem> = emptyList()
    private var onItemClickListener: ((CreditItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (CreditItem) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DEPARTMENT -> {
                val binding = ItemDepartmentHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                DepartmentViewHolder(binding)
            }
            VIEW_TYPE_YEAR -> {
                val binding = ItemYearHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                YearViewHolder(binding)
            }
            else -> {
                val binding = ItemCreditBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                CreditViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is CreditListItem.DepartmentHeader -> (holder as DepartmentViewHolder).bind(item)
            is CreditListItem.YearHeader -> (holder as YearViewHolder).bind(item)
            is CreditListItem.CreditEntry -> (holder as CreditViewHolder).bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CreditListItem.DepartmentHeader -> VIEW_TYPE_DEPARTMENT
            is CreditListItem.YearHeader -> VIEW_TYPE_YEAR
            is CreditListItem.CreditEntry -> VIEW_TYPE_CREDIT
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(sections: List<DepartmentSection>) {
        val newItems = sections.flatMap { department ->
            listOf<CreditListItem>(CreditListItem.DepartmentHeader(department.name)) +
                    department.yearSections.flatMap { yearSection ->
                        listOf<CreditListItem>(CreditListItem.YearHeader(yearSection.year)) +
                                yearSection.items.map { CreditListItem.CreditEntry(it) }
                    }
        }

        val diffCallback = CreditsDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    inner class DepartmentViewHolder(
        private val binding: ItemDepartmentHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CreditListItem.DepartmentHeader) {
            binding.txtDepartment.text = item.name
        }
    }

    inner class YearViewHolder(
        private val binding: ItemYearHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CreditListItem.YearHeader) {
            if (item.year.isEmpty()){
                binding.txtYear.text = "Unknown Date"
            }else{
                binding.txtYear.text = item.year

            }
        }
    }

    inner class CreditViewHolder(
        private val binding: ItemCreditBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CreditListItem.CreditEntry) {

            binding.apply {
                if (item.credit.role.isEmpty()){
                    txtRole.visibility = View.INVISIBLE
                }

                txtTitle.text = item.credit.title
                txtTitle.paintFlags = txtTitle.paintFlags or Paint.UNDERLINE_TEXT_FLAG

                txtRole.text = context.getString(R.string.role, item.credit.role)

                root.setClickAnimation {
                    onItemClickListener?.let { click -> click(item.credit) }
                }

            }
        }
    }

    private class CreditsDiffCallback(
        private val oldList: List<CreditListItem>,
        private val newList: List<CreditListItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.getItemId() == newItem.getItemId()
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem == newItem
        }
    }

    companion object {
        private const val VIEW_TYPE_DEPARTMENT = 0
        private const val VIEW_TYPE_YEAR = 1
        private const val VIEW_TYPE_CREDIT = 2
    }
}