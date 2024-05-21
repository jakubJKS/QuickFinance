package com.example.loginsignupsql

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OverviewAdapter(private val overviewItems: List<String>) : RecyclerView.Adapter<OverviewAdapter.OverviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OverviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return OverviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: OverviewViewHolder, position: Int) {
        holder.textView.text = overviewItems[position]
    }

    override fun getItemCount(): Int {
        return overviewItems.size
    }

    class OverviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(android.R.id.text1)
    }
}
