package com.luojunkai.app

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luojunkai.app.preview.videopreview
import com.luojunkai.app.preview.videopreviewAdapter

class VideoFragment : Fragment(), videopreviewAdapter.OnItemClickListener {
    private val videopreviewlist = ArrayList<videopreview>()
    private lateinit var videopreviewrecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video, container, false)

        initkey()
        val videopreviewlayoutManager = LinearLayoutManager(requireContext())
        videopreviewrecyclerView = view.findViewById(R.id.preview)
        videopreviewrecyclerView.layoutManager = videopreviewlayoutManager
        videopreviewrecyclerView.adapter = videopreviewAdapter(videopreviewlist, this)

        // 添加间距装饰器
        val itemSpacingDecoration = ItemSpacingDecoration(requireContext())
        videopreviewrecyclerView.addItemDecoration(itemSpacingDecoration)

        return view
    }

    private fun initkey() {
        repeat(1) {
            videopreviewlist.add(
                videopreview(
                    "Mountain",
                    "https://lmg.jj20.com/up/allimg/1113/051220112022/200512112022-1-1200.jpg",
                    "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
                )
            )
            videopreviewlist.add(
                videopreview(
                    "Natural",
                    "https://lmg.jj20.com/up/allimg/1115/092621094155/210926094155-7-1200.jpg",
                    "https://media.w3.org/2010/05/sintel/trailer.mp4"
                )
            )
            videopreviewlist.add(
                videopreview(
                    "Dragon",
                    "https://lmg.jj20.com/up/allimg/4k/s/02/210925000GaM1-0-lp.jpg",
                    "http://vjs.zencdn.net/v/oceans.mp4"

                )
            )
        }
    }


    class ItemSpacingDecoration(context: Context) : RecyclerView.ItemDecoration() {
        private val itemSpacingPx: Int = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            10f,
            context.resources.displayMetrics
        ).toInt()

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.top = itemSpacingPx
            outRect.bottom = itemSpacingPx
        }
    }

    override fun onItemClick(position: Int) {
        val videoPreview = videopreviewlist[position]
        // 创建Intent并传递视频URL和封面URL到VideoPlayerActivity
        val intent = Intent(requireContext(), VideoPlayerActivity::class.java)
        intent.putExtra("videoUrl", videoPreview.videoUrl)
        intent.putExtra("imageUrl", videoPreview.imageUrl)
        startActivity(intent)
    }
}