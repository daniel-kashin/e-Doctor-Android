package com.edoctor.data.entity.remote.model.record

data class MedicalEventWrapper(
    val analysis: Analysis?,
    val allergy: Allergy?,
    val note: Note?,
    val vaccination: Vaccination?,
    val procedure: Procedure?,
    val doctorVisit: DoctorVisit?,
    val sickness: Sickness?
)