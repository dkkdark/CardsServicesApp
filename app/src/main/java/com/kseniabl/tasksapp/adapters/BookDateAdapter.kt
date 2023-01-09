package com.kseniabl.tasksapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kseniabl.tasksapp.databinding.BookDateItemBinding
import com.kseniabl.tasksapp.models.BookDate
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BookDateAdapter @Inject constructor() : RecyclerView.Adapter<BookDateAdapter.BookDateHolder>() {

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

    inner class BookDateHolder(val binding: BookDateItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookDateHolder {
        val binding = BookDateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookDateHolder(binding)
    }

    override fun onBindViewHolder(holder: BookDateHolder, position: Int) {
        val item = differ.currentList[position]

        holder.binding.apply {
            dateText.text = item.date
        }

    }

    override fun getItemCount(): Int = differ.currentList.size

}