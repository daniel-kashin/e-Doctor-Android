package com.edoctor.data.repository

import com.edoctor.data.entity.remote.model.medicalAccess.MedicalAccessesForDoctorModel
import com.edoctor.data.entity.remote.model.medicalAccess.MedicalAccessesForPatientModel
import com.edoctor.data.remote.rest.MedicalAccessesRestApi
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

class MedicalAccessesRepository(
    private val api: MedicalAccessesRestApi
) {

    fun getMedicalAccessesForDoctor(
        patientUuid: String? = null
    ) : Single<MedicalAccessesForDoctorModel> {
        return api.getMedicalAccessesForDoctor(patientUuid)
    }

    fun getMedicalAccessesForPatient(
        doctorUuid: String? = null
    ) : Single<MedicalAccessesForPatientModel> {
        return api.getMedicalAccessesForPatient(doctorUuid)
    }

    fun postMedicalAccessesForPatient(
        medicalAccesses: MedicalAccessesForPatientModel
    ) : Completable {
        return api.postMedicalAccessesForPatient(medicalAccesses)
    }

}