package com.example.loginsignupsql

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OverviewAdapter(private val overviewItems: List<String>) : RecyclerView.Adapter<OverviewAdapter.OverviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OverviewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        return OverviewViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OverviewViewHolder, position: Int) {
        holder.bind(overviewItems[position])
    }

    override fun getItemCount(): Int = overviewItems.size

    class OverviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(text: String) {
            textView.text = text
        }
    }
}
