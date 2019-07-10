package com.infostrategic.edoctor.presentation.app.medicalAccessesForPatient

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.infostrategic.edoctor.R
import com.infostrategic.edoctor.data.entity.presentation.MedicalAccessForPatient
import com.infostrategic.edoctor.data.entity.presentation.MedicalAccessesForPatient
import com.infostrategic.edoctor.data.entity.presentation.MedicalRecordType
import com.infostrategic.edoctor.presentation.app.medicalAccessesForPatient.MedicalAccessesForPatientAdapter.ViewHolder
import com.infostrategic.edoctor.utils.dispatchUpdatesTo
import com.infostrategic.edoctor.utils.lazyFind

class MedicalAccessesForPatientAdapter : RecyclerView.Adapter<ViewHolder>() {

    var onPatientMedicalAccessClickListener: ((MedicalAccessForPatient) -> Unit)? = null
    var onDeletePatientMedicalAccessClickListener: ((MedicalAccessForPatient) -> Unit)? = null

    var medicalAccessesForPatient: MedicalAccessesForPatient? = null
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
                .dispatchUpdatesTo(this, "MedicalAccessesForPatientAdapter")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_medical_access_for_patient, parent, false)

        return ViewHolder(
            view,
            { onPatientMedicalAccessClickListener?.invoke(it) },
            { onDeletePatientMedicalAccessClickListener?.invoke(it) }
        )
    }

    override fun getItemCount(): Int = medicalAccessesForPatient?.medicalAccesses?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        medicalAccessesForPatient?.let {
            holder.bind(it.medicalAccesses[position], it.allTypes)
        }
    }

    class ViewHolder(
        private val rootView: View,
        private val onPatientMedicalAccessClickListener: ((MedicalAccessForPatient) -> Unit),
        private val onDeletePatientMedicalAccessClickListener: ((MedicalAccessForPatient) -> Unit)
    ) : RecyclerView.ViewHolder(rootView) {

        private val name by rootView.lazyFind<TextView>(R.id.name)
        private val iconDelete by rootView.lazyFind<ImageView>(R.id.icon_delete)
        private val readAccess by rootView.lazyFind<TextView>(R.id.read_access)

        @SuppressLint("SimpleDateFormat")
        fun bind(
            medicalAccessForPatient: MedicalAccessForPatient,
            allTypes: List<MedicalRecordType>
        ) {
            name.text = medicalAccessForPatient.doctor.fullName
                    ?: rootView.context.getString(R.string.doctor).capitalize()

            readAccess.text = if (medicalAccessForPatient.availableTypes.isEmpty()) {
                rootView.context.getString(R.string.doctor_has_no_access_to_medcard)
            } else {
                rootView.context.getString(
                    R.string.read_access_types_count_param,
                    medicalAccessForPatient.availableTypes.size,
                    allTypes.size
                )
            }

            rootView.setOnClickListener {
                onPatientMedicalAccessClickListener(medicalAccessForPatient)
            }
            iconDelete.setOnClickListener {
                onDeletePatientMedicalAccessClickListener(medicalAccessForPatient)
            }
        }

    }

}