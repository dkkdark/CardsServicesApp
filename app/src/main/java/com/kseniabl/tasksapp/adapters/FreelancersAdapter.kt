package com.kseniabl.tasksapp.adapters

import android.content.Context
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kseniabl.tasksapp.R
import com.kseniabl.tasksapp.databinding.FreelancerItemBinding
import com.kseniabl.tasksapp.models.UserModel
import com.kseniabl.tasksapp.utils.HelperFunctions
import javax.inject.Inject

class FreelancersAdapter @Inject constructor(private val context: Context) : RecyclerView.Adapter<FreelancersAdapter.FreelancersHolder>(), AllCardsAdapterInterface {

    private var listener: Listener? = null

    private var oldList = listOf<UserModel>()

    private val diffCallback = object : DiffUtil.ItemCallback<UserModel>() {
        override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<UserModel>) {
        if (oldList != list) {
            differ.submitList(list)
            oldList = list
        }
    }

    inner class FreelancersHolder(val binding: FreelancerItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreelancersHolder {
        val binding = FreelancerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FreelancersHolder(binding)
    }

    override fun onBindViewHolder(holder: FreelancersHolder, position: Int) {
        val item = differ.currentList[position]

        holder.binding.apply {
            itemExeName.text = item.username
            itemExeDescr.text = item.specialization?.specialization

            val bitmap = item.img?.content?.let { HelperFunctions.getImageBitmap(it) }
            bitmap?.let {
                Glide.with(context).asBitmap()
                    .load(bitmap).placeholder(R.drawable.user).into(imageViewItemExe)
            }
        }

        holder.itemView.setOnClickListener {
            listener?.onAddItemClick(item)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun setOnClickListener(onClickListener: Listener) {
        listener = onClickListener
    }

    interface Listener {
        fun onAddItemClick(user: UserModel)
    }
}