package com.edoctor.presentation.app.events

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.edoctor.R
import com.edoctor.data.entity.remote.model.record.*
import com.edoctor.presentation.app.events.EventsAdapter.ViewHolder
import com.edoctor.utils.dispatchUpdatesTo
import com.edoctor.utils.lazyFind
import com.edoctor.utils.unixTimeToJavaTime
import java.text.SimpleDateFormat

class EventsAdapter : RecyclerView.Adapter<ViewHolder>() {

    var onEventClickListener: ((MedicalEventModel) -> Unit)? = null

    var events: List<MedicalEventModel> = emptyList()
        set(value) {
            val oldItems = field

            field = value

            DiffUtil
                .calculateDiff(
                    object : DiffUtil.Callback() {
                        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                            oldItems[oldItemPosition] == value[newItemPosition]

                        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = true
                        override fun getOldListSize(): Int = oldItems.size
                        override fun getNewListSize(): Int = value.size
                    },
                    true
                )
                .dispatchUpdatesTo(this, "EventsAdapter")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)

        return ViewHolder(view) {
            onEventClickListener?.invoke(it)
        }
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(events[position])
    }

    class ViewHolder(
        private val rootView: View,
        private val onEventClickListener: (MedicalEventModel) -> Unit
    ) : RecyclerView.ViewHolder(rootView) {

        private val name by rootView.lazyFind<TextView>(R.id.name)
        private val date by rootView.lazyFind<TextView>(R.id.date)
        private val comment by rootView.lazyFind<TextView>(R.id.comment)

        @SuppressLint("SimpleDateFormat")
        fun bind(medicalEventModel: MedicalEventModel) = medicalEventModel.let {
            val (nameText: String, typeText: String) = rootView.context.run {
                when (it) {
                    is Analysis -> {
                        it.name to getString(R.string.analysis)
                    }
                    is Allergy -> {
                        it.allergenName to getString(R.string.allergy)
                    }
                    is Note -> {
                        val note = getString(R.string.note)
                        (it.comment ?: note) to note
                    }
                    is Vaccination -> {
                        it.name to getString(R.string.vaccination)
                    }
                    is Procedure -> {
                        it.name to getString(R.string.procedure)
                    }
                    is DoctorVisit -> {
                        val doctorVisit = getString(R.string.doctor_visit)
                        (it.doctorName ?: doctorVisit) to doctorVisit
                    }
                    is Sickness -> {
                        it.diagnosis to getString(R.string.sickness)
                    }
                }
            }

            name.text = nameText
            comment.text = typeText
            date.text = SimpleDateFormat("dd MMM, HH:mm").format(it.timestamp.unixTimeToJavaTime())

            rootView.setOnClickListener {
                onEventClickListener(medicalEventModel)
            }
        }

    }

}