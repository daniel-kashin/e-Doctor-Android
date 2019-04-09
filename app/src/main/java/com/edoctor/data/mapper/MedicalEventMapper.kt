package com.edoctor.data.mapper

import com.dropbox.core.android.AuthActivity.result
import com.edoctor.R
import com.edoctor.data.entity.presentation.MedicalEventType
import com.edoctor.data.entity.remote.model.record.*
import com.edoctor.data.entity.remote.model.record.MedicalEventWrapper.Companion.TYPE_ALLERGY
import com.edoctor.data.entity.remote.model.record.MedicalEventWrapper.Companion.TYPE_ANALYSIS
import com.edoctor.data.entity.remote.model.record.MedicalEventWrapper.Companion.TYPE_DOCTOR_VISIT
import com.edoctor.data.entity.remote.model.record.MedicalEventWrapper.Companion.TYPE_NOTE
import com.edoctor.data.entity.remote.model.record.MedicalEventWrapper.Companion.TYPE_PROCEDURE
import com.edoctor.data.entity.remote.model.record.MedicalEventWrapper.Companion.TYPE_SICKNESS
import com.edoctor.data.entity.remote.model.record.MedicalEventWrapper.Companion.TYPE_VACCINATION

object MedicalEventMapper {

    fun toWrapper(medicalEventModel: MedicalEventModel): MedicalEventWrapper = medicalEventModel.run {
        when (this) {
            is Analysis -> {
                MedicalEventWrapper(
                    uuid = uuid,
                    timestamp = timestamp,
                    type = TYPE_ANALYSIS,
                    comment = comment,
                    clinic = clinic,
                    name = name,
                    diagnosis = result
                )
            }
            is Allergy -> {
                MedicalEventWrapper(
                    uuid = uuid,
                    timestamp = timestamp,
                    type = TYPE_ALLERGY,
                    comment = comment,
                    endTimestamp = endTimestamp,
                    name = allergenName,
                    symptoms = symptoms
                )
            }
            is Note -> {
                MedicalEventWrapper(
                    uuid = uuid,
                    timestamp = timestamp,
                    type = TYPE_NOTE,
                    comment = comment
                )
            }
            is Vaccination -> {
                MedicalEventWrapper(
                    uuid = uuid,
                    timestamp = timestamp,
                    type = TYPE_VACCINATION,
                    comment = comment,
                    clinic = clinic,
                    doctorName = doctorName,
                    doctorSpecialization = doctorSpecialization,
                    name = name
                )
            }
            is Procedure -> {
                MedicalEventWrapper(
                    uuid = uuid,
                    timestamp = timestamp,
                    type = TYPE_PROCEDURE,
                    comment = comment,
                    clinic = clinic,
                    doctorName = doctorName,
                    doctorSpecialization = doctorSpecialization,
                    name = name
                )
            }
            is DoctorVisit -> {
                MedicalEventWrapper(
                    uuid = uuid,
                    timestamp = timestamp,
                    type = TYPE_DOCTOR_VISIT,
                    comment = comment,
                    clinic = clinic,
                    doctorName = doctorName,
                    doctorSpecialization = doctorSpecialization,
                    symptoms = complaints,
                    diagnosis = diagnosisAndRecommendations,
                    recipe = recipe
                )
            }
            is Sickness -> {
                MedicalEventWrapper(
                    uuid = uuid,
                    timestamp = timestamp,
                    type = TYPE_SICKNESS,
                    comment = comment,
                    endTimestamp = endTimestamp,
                    symptoms = symptoms,
                    diagnosis = diagnosis
                )
            }
        }
    }

    fun fromWrapper(medicalEventWrapper: MedicalEventWrapper): MedicalEventModel? = medicalEventWrapper.run {
        when (this.type) {
            TYPE_ANALYSIS -> {
                name?.let {
                    Analysis(uuid, timestamp, comment, clinic, name, diagnosis)
                }
            }
            TYPE_ALLERGY -> {
                name?.let {
                    Allergy(uuid, timestamp, comment, endTimestamp, name, symptoms)
                }
            }
            TYPE_NOTE -> {
                Note(uuid, timestamp, comment)
            }
            TYPE_VACCINATION -> {
                name?.let {
                    Vaccination(uuid, timestamp, comment, clinic, doctorName, doctorSpecialization, name)
                }
            }
            TYPE_PROCEDURE -> {
                name?.let {
                    Procedure(uuid, timestamp, comment, clinic, doctorName, doctorSpecialization, name)
                }
            }
            TYPE_DOCTOR_VISIT -> {
                if (symptoms != null && diagnosis != null) {
                    DoctorVisit(uuid, timestamp, comment, clinic, doctorName, doctorSpecialization, symptoms, diagnosis, recipe)
                } else {
                    null
                }
            }
            TYPE_SICKNESS -> {
                diagnosis?.let {
                    Sickness(uuid, timestamp, comment, endTimestamp, symptoms, diagnosis)
                }
            }
            else -> null
        }
    }

    fun toType(medicalEventModel: MedicalEventModel): MedicalEventType {
        return when (medicalEventModel) {
            is Analysis -> MedicalEventType.Analysis
            is Allergy -> MedicalEventType.Allergy
            is DoctorVisit -> MedicalEventType.DoctorVisit
            is Note -> MedicalEventType.Note
            is Procedure -> MedicalEventType.Procedure
            is Sickness -> MedicalEventType.Sickness
            is Vaccination -> MedicalEventType.Vaccination
        }
    }

}