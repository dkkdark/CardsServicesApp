package com.kseniabl.tasksapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kseniabl.tasksapp.R
import com.kseniabl.tasksapp.databinding.CardItemBinding
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.view.TagsModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class AddTasksAdapter @Inject constructor(private val context: Context): RecyclerView.Adapter<AddTasksAdapter.DraftTasksHolder>(), AllCardsAdapterInterface {

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
            if (item.prepayment)
                cardPrepayment.text = context.resources.getString(R.string.with_prepayment)
            else
                cardPrepayment.text = context.resources.getString(R.string.without_prepayment)
            if (item.agreement)
                cardCost.text = context.resources.getString(R.string.by_agreement)
            else
                cardCost.text = context.resources.getString(R.string.cost_num, item.cost)

            if (item.tags.isEmpty()) {
                tagView.visibility = View.GONE
                tagText.visibility = View.GONE
            }
            else {
                tagView.visibility = View.VISIBLE
                tagText.visibility = View.VISIBLE
                tagView.tags = item.tags
            }

            val tagsList =
                item.bookDates.filter { it.userId.isNotEmpty() }.map { TagsModel(it.date) }
            if (tagsList.isNotEmpty()) {
                dateTitle.visibility = View.VISIBLE
                bookedTags.visibility = View.VISIBLE
                bookedTags.tags = tagsList as ArrayList
            }

            val currentTime = Calendar.getInstance().time.time
            val distinction = currentTime - item.createTime
            val numOfDays = (distinction / (1000 * 60 * 60 * 24)).toInt()
            val hours = (distinction / (1000 * 60 * 60)).toInt()
            val minutes = (distinction / (1000 * 60)).toInt()
            if (numOfDays == 0 && hours == 0 && minutes == 0)
                cardTime.text =  context.resources.getString(R.string.seconds_ago)
            else if (numOfDays == 0 && hours == 0 && minutes != 0)
                cardTime.text = context.resources.getString(R.string.minutes_ago, minutes)
            else if (numOfDays == 0 && hours != 0 && minutes != 0)
                cardTime.text = context.resources.getString(R.string.hours_ago, hours)
            else if (numOfDays != 0 && hours != 0 && minutes != 0)
                cardTime.text = context.resources.getString(R.string.days_ago, numOfDays)
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
        fun onAddItemClick(item: CardModel)
    }
}