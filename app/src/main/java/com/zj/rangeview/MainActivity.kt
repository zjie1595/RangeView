package com.zj.rangeview

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ImageUtils
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.setup
import com.zj.rangeview.databinding.ActivityMainBinding
import com.zj.widget.RangeListener
import com.zj.widget.RangeView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max

private const val TAG = "MainActivityTAG"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val frameList = mutableListOf<Bitmap>()

        registerForActivityResult(ActivityResultContracts.GetContent()) { videoUri ->
            lifecycleScope.launch(Dispatchers.IO) {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(this@MainActivity, videoUri)
                val duration =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLong() ?: 0L
                Log.d(TAG, "duration = $duration")
                val frameCount = max(10, duration / 1000)
                val interval = duration / frameCount
                for (i in 0..frameCount) {
                    val timeUs = i * interval * 1000
                    val frame =
                        retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST)
                    frameList += ImageUtils.scale(
                        frame,
                        ConvertUtils.dp2px(56F),
                        ConvertUtils.dp2px(56F)
                    )
                    withContext(Dispatchers.Main) {
                        binding.rvVideoFrame.bindingAdapter.models = frameList
                    }
                }
            }
        }.launch("video/*")

        binding.rvVideoFrame.setup {
            addType<Bitmap>(R.layout.item_video_frame)
        }.models = null

        binding.rangeView.setRangeListener(object : RangeListener {
            override fun onStartTrackingTouch(rangeView: RangeView) {

            }

            override fun onStopTrackingTouch(rangeView: RangeView) {

            }

            override fun onRangeChangeListener(
                rangeView: RangeView,
                startValue: Float,
                endValue: Float,
                changingValue: Float,
                isStartValueChanging: Boolean,
                isFromUser: Boolean
            ) {
                Log.d(TAG, "startValue = $startValue")
                Log.d(TAG, "endValue = $endValue")
                Log.d(TAG, "changingValue = $changingValue")
                Log.d(TAG, "isStartValueChanging = $isStartValueChanging")
                Log.d(TAG, "isFromUser = $isFromUser")
            }
        })
    }
}