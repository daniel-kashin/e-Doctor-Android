package com.infostrategic.edoctor.presentation.app.medicalAccessesForDoctor

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.infostrategic.edoctor.R
import com.infostrategic.edoctor.data.entity.presentation.*
import com.infostrategic.edoctor.presentation.app.medicalAccessesForDoctor.MedicalAccessesForDoctorAdapter.ViewHolder
import com.infostrategic.edoctor.utils.dispatchUpdatesTo
import com.infostrategic.edoctor.utils.lazyFind

class MedicalAccessesForDoctorAdapter : RecyclerView.Adapter<ViewHolder>() {

    var onMedicalAccessForDoctorClickListener: ((MedicalAccessForDoctor) -> Unit)? = null

    var medicalAccessesForDoctor: MedicalAccessesForDoctor? = null
        set(value) {
            val oldItems = field?.medicalAccesses.orEmpty()

            field = value

            val newItems = field?.medicalAccesses.orEmpty()

            DiffUtil
                .calculateDiff(
                    object : DiffUtil.Callback() {
                        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                            oldItems[oldItemPosition] == newItems[newItemPosition]
                        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = true
                        override fun getOldListSize(): Int = oldItems.size
                        override fun getNewListSize(): Int = newItems.size
                    },
                    true
                )
                .dispatchUpdatesTo(this, "MedicalAccessesForDoctorAdapter")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_medical_access_for_doctor, parent, false)

        return ViewHolder(view) {
            onMedicalAccessForDoctorClickListener?.invoke(it)
        }
    }

    override fun getItemCount(): Int = medicalAccessesForDoctor?.medicalAccesses?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        medicalAccessesForDoctor?.let {
            holder.bind(it.medicalAccesses[position])
        }
    }

    class ViewHolder(
        private val rootView: View,
        private val onMedicalAccessForDoctorClickListener: ((MedicalAccessForDoctor) -> Unit)
    ) : RecyclerView.ViewHolder(rootView) {

        private val name by rootView.lazyFind<TextView>(R.id.name)
        private val readAccess by rootView.lazyFind<TextView>(R.id.read_access)

        @SuppressLint("SimpleDateFormat")
        fun bind(medicalAccessForDoctor: MedicalAccessForDoctor) {
            name.text = medicalAccessForDoctor.patient.fullName
                    ?: rootView.context.getString(R.string.patient).capitalize()

            readAccess.text = if (medicalAccessForDoctor.availableTypes.isEmpty()) {
                rootView.context.getString(R.string.doctor_has_no_access_to_medcard)
            } else {
                rootView.context.getString(
                    R.string.read_access_types_count_param,
                    medicalAccessForDoctor.availableTypes.size,
                    medicalAccessForDoctor.allTypes.size
                )
            }

            rootView.setOnClickListener {
                onMedicalAccessForDoctorClickListener(medicalAccessForDoctor)
            }
        }
    }

}