package com.kseniabl.tasksapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kseniabl.tasksapp.databinding.BookDateWithCheckboxItemBinding
import com.kseniabl.tasksapp.models.BookDate
import javax.inject.Inject

class DatesDetailAdapter @Inject constructor() : RecyclerView.Adapter<DatesDetailAdapter.DatesDetailHolder>() {

    var selected = -1

    private val diffCallback = object : DiffUtil.ItemCallback<BookDate>() {
        override fun areItemsTheSame(oldItem: BookDate, newItem: BookDate): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: BookDate, newItem: BookDate): Boolean =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<BookDate>) {
        differ.submitList(list)
    }

    inner class DatesDetailHolder(val binding: BookDateWithCheckboxItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatesDetailHolder {
        val binding = BookDateWithCheckboxItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DatesDetailHolder(binding)
    }

    override fun onBindViewHolder(holder: DatesDetailHolder, position: Int) {
        val item = differ.currentList[position]

        holder.binding.apply {
            dateText.text = item.date

            checkbox.setOnCheckedChangeListener { _, state ->
                if (state)
                    if (selected != -1) {
                        checkbox.isChecked = false
                    }
                    else {
                        checkbox.isChecked = true
                        selected = position
                    }
                else {
                    checkbox.isChecked = false
                    selected = -1
                }
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

}