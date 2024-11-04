package com.netflixclone.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.netflixclone.app.R
import com.netflixclone.app.models.Category
import kotlinx.android.synthetic.main.item_category.view.*

class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClickListener: OnCategoryClickListener
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(category: Category) {
            itemView.tvCategoryTitle.text = category.title
            Glide.with(itemView.context)
                .load(category.imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(itemView.ivCategoryImage)

            itemView.setOnClickListener {
                onCategoryClickListener.onCategoryClick(category)
            }
        }
    }

    interface OnCategoryClickListener {
        fun onCategoryClick(category: Category)
    }
}