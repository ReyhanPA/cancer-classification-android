package com.dicoding.asclepius.helper

import androidx.recyclerview.widget.DiffUtil
import com.dicoding.asclepius.data.remote.response.ArticlesItem

object DiffUtilCallback : DiffUtil.ItemCallback<ArticlesItem>() {
    override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
        return oldItem == newItem
    }
}