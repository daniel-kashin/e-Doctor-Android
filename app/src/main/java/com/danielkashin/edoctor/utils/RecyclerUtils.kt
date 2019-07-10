package com.danielkashin.edoctor.utils

import android.util.Log.d
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView

fun DiffUtil.DiffResult.dispatchUpdatesTo(adapter: RecyclerView.Adapter<*>, logTag: String? = null) {
    dispatchUpdatesTo(object : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {
            d(logTag, "onChanged(): position = $position, count = $count")
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            d(logTag, "onMoved(): fromPosition = $fromPosition, toPosition = $toPosition")
            adapter.notifyItemMoved(fromPosition, toPosition)
        }

        override fun onInserted(position: Int, count: Int) {
            d(logTag, "onInserted(): position = $position, count = $count")
            adapter.notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            d(logTag, "onRemoved(): position = $position, count = $count")
            adapter.notifyItemRangeRemoved(position, count)
        }
    })
}