package com.kseniabl.tasksapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kseniabl.tasksapp.databinding.CardsWasBookedItemBinding
import com.kseniabl.tasksapp.models.BookInfoModel
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.view.TagsModel
import javax.inject.Inject

class BookedUsersCardsAdapter @Inject constructor(): RecyclerView.Adapter<BookedUsersCardsAdapter.BookedUsersCardsHolder>(), AllCardsAdapterInterface {

    private var listener: Listener? = null

    var userId: String? = null

    private val diffCallback = object : DiffUtil.ItemCallback<BookInfoModel>() {
        override fun areItemsTheSame(oldItem: BookInfoModel, newItem: BookInfoModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: BookInfoModel, newItem: BookInfoModel): Boolean =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<BookInfoModel>) = differ.submitList(list)

    inner class BookedUsersCardsHolder(val binding: CardsWasBookedItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookedUsersCardsHolder {
        val binding = CardsWasBookedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookedUsersCardsHolder(binding)
    }

    override fun onBindViewHolder(holder: BookedUsersCardsHolder, position: Int) {
        val item = differ.currentList[position]

        holder.binding.apply {
            nameText.text = item.username
            cardTitle.text = item.title
            bookedTags.tags = arrayListOf(TagsModel(item.date))
        }
        holder.itemView.setOnClickListener {
            listener?.onAddItemClick(item)
        }
    }

    fun setOnClickListener(onClickListener: Listener) {
        listener = onClickListener
    }

    override fun getItemCount(): Int = differ.currentList.size

    interface Listener {
        fun onAddItemClick(item: BookInfoModel)
    }
}