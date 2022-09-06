package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.ItemBinding


@BindingAdapter("listData")
fun submitList(recyclerView: RecyclerView, data: List<Asteroid>) {
    val adapter = recyclerView.adapter as AsteroidListAdapter
    adapter.submitList(data)
}

class AsteroidListAdapter() :
    ListAdapter<Asteroid, AsteroidListAdapter.AstroidViewHolder>(DiffCallBack) {

    private lateinit var asteroidListen :AsteroidListener

    fun setOnClickListener(onListener : AsteroidListener){
        asteroidListen = onListener
    }

    interface AsteroidListener {
        fun listen (asteroidPos: Int)
    }


    companion object DiffCallBack : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AstroidViewHolder {
        return AstroidViewHolder(ItemBinding.inflate(LayoutInflater.from(parent.context)),asteroidListen)
    }


    override fun onBindViewHolder(holder: AstroidViewHolder, position: Int) {
        val posItem = getItem(position)
        holder.also {

            it.bind(posItem)
        }
}
    class AstroidViewHolder(private val binding: ItemBinding,listener: AsteroidListener) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(asteroid: Asteroid) {
            binding.asteroid = asteroid
            binding.executePendingBindings()
        }
        init {
            binding.root.setOnClickListener{
                listener.listen(adapterPosition)
            }
        }
    }
    }