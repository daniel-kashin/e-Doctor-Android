package com.edoctor.data.mapper

import com.edoctor.data.entity.local.event.MedicalEventEntity
import com.edoctor.data.entity.presentation.MedicalEventType
import com.edoctor.data.entity.remote.model.record.*
import com.edoctor.data.mapper.MedicalRecordTypeMapper.MEDICAL_EVENT_TYPE_ALLERGY
import com.edoctor.data.mapper.MedicalRecordTypeMapper.MEDICAL_EVENT_TYPE_ANALYSIS
import com.edoctor.data.mapper.MedicalRecordTypeMapper.MEDICAL_EVENT_TYPE_DOCTOR_VISIT
import com.edoctor.data.mapper.MedicalRecordTypeMapper.MEDICAL_EVENT_TYPE_NOTE
import com.edoctor.data.mapper.MedicalRecordTypeMapper.MEDICAL_EVENT_TYPE_PROCEDURE
import com.edoctor.data.mapper.MedicalRecordTypeMapper.MEDICAL_EVENT_TYPE_SICKNESS
import com.edoctor.data.mapper.MedicalRecordTypeMapper.MEDICAL_EVENT_TYPE_VACCINATION

object MedicalEventMapper {

    fun toWrapperFromLocal(medicalEventEntity: MedicalEventEntity): MedicalEventWrapper = medicalEventEntity.run {
        MedicalEventWrapper(
            uuid = uuid,
            timestamp = timestamp,
            type = type,
            isDeleted = isDeleted != 0,
            doctorCreatorUuid = doctorCreatorUuid,
            isAddedFromDoctor = isAddedFromDoctor != 0,
            endTimestamp = endTimestamp,
            name = name,
            clinic = clinic,
            doctorName = doctorName,
            doctorSpecialization = doctorSpecialization,
            symptoms = symptoms,
            diagnosis = diagnosis,
            recipe = recipe,
            comment = comment
        )
    }

    fun toLocalFromWrapper(
        medicalEventWrapper: MedicalEventWrapper,
        patientUuid: String,
        isChangedLocally: Boolean
    ): MedicalEventEntity = medicalEventWrapper.run {
        MedicalEventEntity(
            uuid = uuid,
            timestamp = timestamp,
            type = type,
            isChangedLocally = if (isChangedLocally) 1 else 0,
            isDeleted = if (isDeleted) 1 else 0,
            doctorCreatorUuid = doctorCreatorUuid,
            isAddedFromDoctor = if (isAddedFromDoctor) 1 else 0,
            endTimestamp = endTimestamp,
            name = name,
            clinic = clinic,
            doctorName = doctorName,
            doctorSpecialization = doctorSpecialization,
            patientUuid = patientUuid,
            symptoms = symptoms,
            diagnosis = diagnosis,
            recipe = recipe,
            comment = comment
        )
    }

    fun toWrapperFromModel(medicalEventModel: MedicalEventModel): MedicalEventWrapper = medicalEventModel.run {
        when (this) {
            is Analysis -> {
                MedicalEventWrapper(
                    uuid = uuid,
                    doctorCreatorUuid = doctorCreatorUuid,
                    isDeleted = false,
                    isAddedFromDoctor = isAddedFromDoctor,
                    timestamp = timestamp,
                    type = MEDICAL_EVENT_TYPE_ANALYSIS,
                    comment = comment,
                    clinic = clinic,
                    name = name,
                    diagnosis = result
                )
            }
            is Allergy -> {
                MedicalEventWrapper(
                    uuid = uuid,
                    doctorCreatorUuid = doctorCreatorUuid,
                    isDeleted = false,
                    isAddedFromDoctor = isAddedFromDoctor,
                    timestamp = timestamp,
                    type = MEDICAL_EVENT_TYPE_ALLERGY,
                    comment = comment,
                    endTimestamp = endTimestamp,
                    name = allergenName,
                    symptoms = symptoms
                )
            }
            is Note -> {
                MedicalEventWrapper(
                    uuid = uuid,
                    doctorCreatorUuid = doctorCreatorUuid,
                    isDeleted = false,
                    isAddedFromDoctor = isAddedFromDoctor,
                    timestamp = timestamp,
                    type = MEDICAL_EVENT_TYPE_NOTE,
                    comment = comment
                )
            }
            is Vaccination -> {
                MedicalEventWrapper(
                    uuid = uuid,
                    doctorCreatorUuid = doctorCreatorUuid,
                    isDeleted = false,
                    isAddedFromDoctor = isAddedFromDoctor,
                    timestamp = timestamp,
                    type = MEDICAL_EVENT_TYPE_VACCINATION,
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
                    doctorCreatorUuid = doctorCreatorUuid,
                    isDeleted = false,
                    isAddedFromDoctor = isAddedFromDoctor,
                    timestamp = timestamp,
                    type = MEDICAL_EVENT_TYPE_PROCEDURE,
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
                    doctorCreatorUuid = doctorCreatorUuid,
                    isDeleted = false,
                    isAddedFromDoctor = isAddedFromDoctor,
                    timestamp = timestamp,
                    type = MEDICAL_EVENT_TYPE_DOCTOR_VISIT,
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
                    doctorCreatorUuid = doctorCreatorUuid,
                    isDeleted = false,
                    isAddedFromDoctor = isAddedFromDoctor,
                    timestamp = timestamp,
                    type = MEDICAL_EVENT_TYPE_SICKNESS,
                    comment = comment,
                    endTimestamp = endTimestamp,
                    symptoms = symptoms,
                    diagnosis = diagnosis
                )
            }
        }
    }

    fun toModelFromWrapper(medicalEventWrapper: MedicalEventWrapper): MedicalEventModel? = medicalEventWrapper.run {
        when (this.type) {
            MEDICAL_EVENT_TYPE_ANALYSIS -> {
                name?.let {
                    Analysis(uuid, isDeleted, doctorCreatorUuid, isAddedFromDoctor, timestamp, comment, clinic, name, diagnosis)
                }
            }
            MEDICAL_EVENT_TYPE_ALLERGY -> {
                name?.let {
                    Allergy(
                        uuid,
                        isDeleted,
                        doctorCreatorUuid,
                        isAddedFromDoctor,
                        timestamp,
                        comment,
                        endTimestamp,
                        name,
                        symptoms
                    )
                }
            }
            MEDICAL_EVENT_TYPE_NOTE -> {
                Note(uuid, isDeleted, doctorCreatorUuid, isAddedFromDoctor, timestamp, comment)
            }
            MEDICAL_EVENT_TYPE_VACCINATION -> {
                name?.let {
                    Vaccination(
                        uuid,
                        isDeleted,
                        doctorCreatorUuid,
                        isAddedFromDoctor,
                        timestamp,
                        comment,
                        clinic,
                        doctorName,
                        doctorSpecialization,
                        name
                    )
                }
            }
            MEDICAL_EVENT_TYPE_PROCEDURE -> {
                name?.let {
                    Procedure(
                        uuid,
                        isDeleted,
                        doctorCreatorUuid,
                        isAddedFromDoctor,
                        timestamp,
                        comment,
                        clinic,
                        doctorName,
                        doctorSpecialization,
                        name
                    )
                }
            }
            MEDICAL_EVENT_TYPE_DOCTOR_VISIT -> {
                if (symptoms != null && diagnosis != null) {
                    DoctorVisit(
                        uuid,
                        isDeleted,
                        doctorCreatorUuid,
                        isAddedFromDoctor,
                        timestamp,
                        comment,
                        clinic,
                        doctorName,
                        doctorSpecialization,
                        symptoms,
                        diagnosis,
                        recipe
                    )
                } else {
                    null
                }
            }
            MEDICAL_EVENT_TYPE_SICKNESS -> {
                diagnosis?.let {
                    Sickness(
                        uuid,
                        isDeleted,
                        doctorCreatorUuid,
                        isAddedFromDoctor,
                        timestamp,
                        comment,
                        endTimestamp,
                        symptoms,
                        diagnosis
                    )
                }
            }
            else -> null
        }
    }

    fun toType(medicalEventModel: MedicalEventModel): MedicalEventType {
        return when (medicalEventModel) {
            is Analysis -> MedicalEventType.Analysis()
            is Allergy -> MedicalEventType.Allergy()
            is DoctorVisit -> MedicalEventType.DoctorVisit()
            is Note -> MedicalEventType.Note()
            is Procedure -> MedicalEventType.Procedure()
            is Sickness -> MedicalEventType.Sickness()
            is Vaccination -> MedicalEventType.Vaccination()
        }
    }

}