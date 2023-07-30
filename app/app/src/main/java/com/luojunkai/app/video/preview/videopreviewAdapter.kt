package com.luojunkai.app.video.preview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.luojunkai.app.R

class videopreviewAdapter(val videopreviewlist: List<videopreview>, private val clickListener: OnItemClickListener) :
    RecyclerView.Adapter<videopreviewAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val previewimage: ImageView = view.findViewById(R.id.previewimage)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.preview_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return videopreviewlist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val videopreview = videopreviewlist[position]
        holder.title.text = videopreview.title
        Glide.with(holder.itemView.context)
            .load(videopreview.imageUrl)
            .into(holder.previewimage)
    }
}