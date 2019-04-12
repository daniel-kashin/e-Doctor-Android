package com.edoctor.presentation.app.editMedicalAccess

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.edoctor.R
import com.edoctor.data.entity.presentation.BodyParameterType
import com.edoctor.data.entity.presentation.MedicalEventType
import com.edoctor.data.entity.presentation.MedicalRecordType
import com.edoctor.presentation.app.editMedicalAccess.EditMedicalAccessAdapter.ViewHolder
import com.edoctor.utils.dispatchUpdatesTo
import com.edoctor.utils.lazyFind

class EditMedicalAccessAdapter : RecyclerView.Adapter<ViewHolder>() {

    var medicalRecordTypes: MutableList<Pair<MedicalRecordType, Boolean>> = mutableListOf()
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
                .dispatchUpdatesTo(this, "EditMedicalAccessAdapter")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_edit_medical_access, parent, false)

        return ViewHolder(view) { medicalRecordType, isChecked ->
            val index = medicalRecordTypes.indexOfFirst { it.first == medicalRecordType }
            if (index != -1) {
                medicalRecordTypes[index] = Pair(medicalRecordType, isChecked)
            }
        }
    }

    override fun getItemCount(): Int = medicalRecordTypes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(medicalRecordTypes[position])
    }

    class ViewHolder(
        private val rootView: View,
        private val onMedicalRecordClickListener: (MedicalRecordType, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(rootView) {

        private val name by rootView.lazyFind<TextView>(R.id.name)
        private val checkbox by rootView.lazyFind<CheckBox>(R.id.checkbox)

        @SuppressLint("SimpleDateFormat")
        fun bind(medicalRecordInfo: Pair<MedicalRecordType, Boolean>) = medicalRecordInfo.let {
            checkbox.isFocusable = false
            checkbox.isClickable = false

            val medicalRecordType = medicalRecordInfo.first

            checkbox.isChecked = medicalRecordInfo.second
            name.text = when (medicalRecordType) {
                is BodyParameterType -> rootView.context.run {
                    val parameter = getString(R.string.parameter)
                    val name = when (medicalRecordType) {
                        is BodyParameterType.Height -> getString(R.string.height)
                        is BodyParameterType.Weight -> getString(R.string.weight)
                        is BodyParameterType.BloodPressure -> getString(R.string.blood_pressure)
                        is BodyParameterType.BloodSugar -> getString(R.string.blood_sugar)
                        is BodyParameterType.Temperature -> getString(R.string.temperature)
                        is BodyParameterType.BloodOxygen -> getString(R.string.blood_oxygen)
                        is BodyParameterType.Custom -> {
                                "${medicalRecordType.name} (${medicalRecordType.unit})"
                        }
                    }
                    "$parameter: $name"
                }
                is MedicalEventType -> rootView.context.run {
                    val medicalEvent = getString(R.string.event)
                    val name = when (medicalRecordType) {
                        is MedicalEventType.Analysis -> getString(R.string.analysis)
                        is MedicalEventType.Allergy -> getString(R.string.allergy)
                        is MedicalEventType.Note -> getString(R.string.note)
                        is MedicalEventType.Vaccination -> getString(R.string.vaccination)
                        is MedicalEventType.Procedure -> getString(R.string.procedure)
                        is MedicalEventType.DoctorVisit -> getString(R.string.doctor_visit)
                        is MedicalEventType.Sickness -> getString(R.string.sickness)
                    }
                    "$medicalEvent: $name"
                }
            }

            rootView.setOnClickListener {
                checkbox.isChecked = !checkbox.isChecked
                onMedicalRecordClickListener(medicalRecordInfo.first, checkbox.isChecked)
            }
        }

    }

}