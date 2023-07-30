package com.luojunkai.app.home.key

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.luojunkai.app.R

class keyAdapter(val keylist: List<key>): RecyclerView.Adapter<keyAdapter.ViewHolder>(){

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val keycontent: TextView = view.findViewById(R.id.k_keycontent)
        val tip: TextView = view.findViewById(R.id.k_tip)
        val from: TextView = view.findViewById(R.id.k_from)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.keynews_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return keylist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val key = keylist[position]
        holder.keycontent.text = (key.keycontent)
        holder.tip.text = (key.tip)
        holder.from.text = (key.from)
    }

}