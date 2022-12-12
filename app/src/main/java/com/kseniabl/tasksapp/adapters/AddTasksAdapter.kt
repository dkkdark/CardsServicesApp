package com.kseniabl.tasksapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kseniabl.tasksapp.databinding.CardItemBinding
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.view.TagsModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class AddTasksAdapter: RecyclerView.Adapter<AddTasksAdapter.DraftTasksHolder>(), AllCardsAdapterInterface {

    private var listener: Listener? = null

    private val diffCallback = object : DiffUtil.ItemCallback<CardModel>() {
        override fun areItemsTheSame(oldItem: CardModel, newItem: CardModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CardModel, newItem: CardModel): Boolean =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<CardModel>) = differ.submitList(list)

    inner class DraftTasksHolder(val binding: CardItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraftTasksHolder {
        val binding = CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DraftTasksHolder(binding)
    }

    override fun onBindViewHolder(holder: DraftTasksHolder, position: Int) {
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
        holder.itemView.setOnClickListener {
            listener?.onAddItemClick(item)
        }
    }

    fun setOnClickListener(onClickListener: Listener) {
       listener = onClickListener;
    }

    override fun getItemCount(): Int = differ.currentList.size

    interface Listener {
        fun onAddItemClick(item: CardModel)
    }
}