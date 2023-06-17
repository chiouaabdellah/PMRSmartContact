package com.example.smartcontact

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(private val dataset: MutableList<String>, private val onItemClick: (String) -> Unit) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(private val view: View, private val onItemClick: (String) -> Unit) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.summaryNameTextView)

        init {
            view.setOnClickListener {
                onItemClick(textView.text.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.summary_item, parent, false)
        return ItemViewHolder(view) { summary ->
            onItemClick(summary)
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.textView.text = dataset[position]
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}
