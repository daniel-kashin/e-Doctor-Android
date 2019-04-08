package com.edoctor.data.mapper

import com.edoctor.data.entity.presentation.MedicalEventType
import com.edoctor.data.entity.remote.model.record.*

object MedicalEventMapper {

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