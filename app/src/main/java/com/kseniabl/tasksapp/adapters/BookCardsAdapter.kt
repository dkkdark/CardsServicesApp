package com.kseniabl.tasksapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kseniabl.tasksapp.databinding.CardItemBinding
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.view.TagsModel
import java.util.*
import kotlin.collections.ArrayList

class BookCardsAdapter: RecyclerView.Adapter<BookCardsAdapter.BookCardsHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<CardModel>() {
        override fun areItemsTheSame(oldItem: CardModel, newItem: CardModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CardModel, newItem: CardModel): Boolean =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<CardModel>) = differ.submitList(list)

    inner class BookCardsHolder(val binding: CardItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookCardsHolder {
        val binding = CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookCardsHolder(binding)
    }

    override fun onBindViewHolder(holder: BookCardsHolder, position: Int) {
        val item = differ.currentList[position]

        holder.binding.apply {
            cardText.text = item.title
            cardDescr.text = item.description
            if (item.agreement)
                cardCost.text = "By agreement"
            else
                cardCost.text = "${item.cost} $"

            if (item.tags.isEmpty()) {
                tagView.visibility = View.GONE
                tagText.visibility = View.GONE
            }
            else {
                tagView.visibility = View.VISIBLE
                tagText.visibility = View.VISIBLE
                tagView.tags = item.tags
            }

            dateTitle.visibility = View.VISIBLE
            bookedTags.visibility = View.VISIBLE
            val tagsList = item.bookDates.filter { it.userId.isNotEmpty() }.map { TagsModel(it.date) }
            bookedTags.tags = tagsList as ArrayList

            val currentTime = Calendar.getInstance().time.time
            val distinction = currentTime - item.createTime
            val numOfDays = (distinction / (1000 * 60 * 60 * 24)).toInt()
            val hours = (distinction / (1000 * 60 * 60)).toInt()
            val minutes = (distinction / (1000 * 60)).toInt()
            if (numOfDays == 0 && hours == 0 && minutes == 0)
                cardTime.text = "seconds ago"
            else if (numOfDays == 0 && hours == 0 && minutes != 0)
                cardTime.text = "$minutes minutes ago"
            else if (numOfDays == 0 && hours != 0 && minutes != 0)
                cardTime.text = "$hours hours ago"
            else if (numOfDays != 0 && hours != 0 && minutes != 0)
                cardTime.text = "$numOfDays days ago"
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}