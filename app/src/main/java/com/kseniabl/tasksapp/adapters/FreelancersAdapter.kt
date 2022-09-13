package com.kseniabl.tasksapp.adapters

import android.content.Context
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kseniabl.tasksapp.R
import com.kseniabl.tasksapp.databinding.FreelancerItemBinding
import com.kseniabl.tasksapp.models.FreelancerModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

class FreelancersAdapter @Inject constructor() : RecyclerView.Adapter<FreelancersAdapter.FreelancersHolder>(), AllCardsAdapterInterface {

    @Inject
    @ApplicationContext
    lateinit var context: Context

    private val diffCallback = object : DiffUtil.ItemCallback<FreelancerModel>() {
        override fun areItemsTheSame(oldItem: FreelancerModel, newItem: FreelancerModel): Boolean =
            oldItem.id == newItem.id

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
            itemExeName.text = item.username
            itemExeDescr.text = item.profession?.description
            //val bytes = Base64.decode(item.img?.img, Base64.DEFAULT)
            //Glide.with(context).load(bytes).placeholder(R.drawable.user).into(imageViewItemExe)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}