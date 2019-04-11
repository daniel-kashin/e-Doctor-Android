package com.edoctor.data.repository

import com.edoctor.data.entity.presentation.MedicalAccessesForPatient
import com.edoctor.data.entity.remote.model.medicalAccess.MedicalAccessForPatientModel
import com.edoctor.data.entity.remote.model.medicalAccess.MedicalAccessesForDoctorModel
import com.edoctor.data.entity.remote.model.medicalAccess.MedicalAccessesForPatientModel
import com.edoctor.data.mapper.MedicalAccessMapper
import com.edoctor.data.remote.rest.MedicalAccessesRestApi
import io.reactivex.Completable
import io.reactivex.Single

class MedicalAccessesRepository(
    private val api: MedicalAccessesRestApi
) {

    fun getMedicalAccessesForDoctor(
        patientUuid: String? = null
    ): Single<MedicalAccessesForDoctorModel> {
        return api.getMedicalAccessesForDoctor(patientUuid)
    }

    fun getMedicalAccessesForPatient(): Single<MedicalAccessesForPatient> {
        return api.getMedicalAccessesForPatient(null)
            .map { MedicalAccessMapper.toPresentationForPatient(it) }
    }

    fun getMedicalAccessForPatient(
        doctorUuid: String? = null
    ): Single<MedicalAccessForPatientModel> {
        return api.getMedicalAccessesForPatient(doctorUuid)
            .map { it.medicalAccesses.first { it.doctor.uuid == doctorUuid } }
    }

    fun postMedicalAccessesForPatient(
        medicalAccesses: MedicalAccessesForPatientModel
    ): Completable {
        return api.postMedicalAccessesForPatient(medicalAccesses)
    }

}