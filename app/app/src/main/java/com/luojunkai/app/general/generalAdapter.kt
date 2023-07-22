package com.luojunkai.app.general

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.luojunkai.app.R

class generalAdapter(
    private val generallist: ArrayList<general>,
    private val generalDao: generalDao
) : RecyclerView.Adapter<generalAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.tv_cover)
        val title: TextView = view.findViewById(R.id.tv_keycontent)
        val content: TextView = view.findViewById(R.id.tv_content)
        val fire: ImageView = view.findViewById(R.id.tv_fire)
        val tip: TextView = view.findViewById(R.id.tv_tip)
        val from: TextView = view.findViewById(R.id.tv_from)
    }

    // 在点击事件中调用这个方法来插入新的 general 对象到数据库中，并更新 generallist
    fun insertGeneral(general: general) {
        generallist.add(0, general) // 添加到列表头部，使新内容在最上方
        notifyDataSetChanged() // 更新适配器

        // 在数据库中插入新的 general 对象
        Thread {
            generalDao.insertGeneral(general)
        }.start()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.generalnews_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return generallist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val general = generallist[position]

        // 使用Glide加载图片并显示在ImageView中
        Glide.with(holder.itemView.context)
            .load(general.imageUrl) // 使用General对象的imageUrl字段作为图片的URL
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.placeholder_image) // 设置占位符图片，防止加载过程中出现空白
                    .error(R.drawable.error_image) // 设置加载失败时显示的错误图片
            )
            .transition(DrawableTransitionOptions.withCrossFade()) // 设置加载图片的过渡效果
            .into(holder.cover) // 将加载的图片显示在cover ImageView中

        holder.title.text = general.title
        holder.content.text = general.content
        holder.fire.setImageResource(general.iconResource)
        holder.tip.text = general.label
        holder.from.text = general.source

        // 点击事件保存 general 到数据库
        holder.itemView.setOnClickListener {
            // 将 General 对象插入到数据库中
            generalDao.insertGeneral(general)

            notifyDataSetChanged() // 更新适配器
        }
    }
}