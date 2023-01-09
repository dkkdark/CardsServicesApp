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
import com.kseniabl.tasksapp.models.CardModel
import com.kseniabl.tasksapp.models.FreelancerModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

class FreelancersAdapter @Inject constructor() : RecyclerView.Adapter<FreelancersAdapter.FreelancersHolder>(), AllCardsAdapterInterface {

    private var listener: Listener? = null

    @Inject
    @ApplicationContext
    lateinit var context: Context

    private val diffCallback = object : DiffUtil.ItemCallback<FreelancerModel>() {
        override fun areItemsTheSame(oldItem: FreelancerModel, newItem: FreelancerModel): Boolean =
            oldItem.userInfo?.id == newItem.userInfo?.id

        override fun areContentsTheSame(oldItem: FreelancerModel, newItem: FreelancerModel): Boolean =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<FreelancerModel>) = differ.submitList(list)

    inner class FreelancersHolder(val binding: FreelancerItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FreelancersHolder {
        val binding = FreelancerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FreelancersHolder(binding)
    }

    override fun onBindViewHolder(holder: FreelancersHolder, position: Int) {
        val item = differ.currentList[position]

        holder.binding.apply {
            itemExeName.text = item.userInfo?.username
            itemExeDescr.text = item.specialization?.specialization
            //val bytes = Base64.decode(item.img?.img, Base64.DEFAULT)
            //Glide.with(context).load(bytes).placeholder(R.drawable.user).into(imageViewItemExe)
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
        fun onAddItemClick(item: FreelancerModel)
    }
}