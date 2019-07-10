package com.danielkashin.edoctor.data.repository

import com.danielkashin.edoctor.data.entity.presentation.*
import com.danielkashin.edoctor.data.mapper.MedicalAccessMapper
import com.danielkashin.edoctor.data.remote.rest.MedicalAccessesRestApi
import io.reactivex.Completable
import io.reactivex.Single

class MedicalAccessesRepository(
    private val api: MedicalAccessesRestApi
) {

    fun getMedicalAccessesForDoctor(): Single<MedicalAccessesForDoctor> {
        return api.getMedicalAccessesForDoctor(null)
            .map { MedicalAccessMapper.toPresentationForDoctor(it) }
    }

    fun getMedicalAccessForDoctor(
        patientUuid: String
    ): Single<MedicalAccessForDoctor> {
        return api.getMedicalAccessesForDoctor(patientUuid)
            .map { MedicalAccessMapper.toPresentationForDoctor(it) }
            .map { it.medicalAccesses.first { it.patient.uuid == patientUuid } }
    }

    fun getMedicalAccessesForPatient(): Single<MedicalAccessesForPatient> {
        return api.getMedicalAccessesForPatient(null)
            .map { MedicalAccessMapper.toPresentationForPatient(it) }
    }

    fun getMedicalAccessForPatient(
        doctorUuid: String
    ): Single<MedicalAccessInfo> {
        return api.getMedicalAccessesForPatient(doctorUuid)
            .map { MedicalAccessMapper.toPresentationForPatient(it) }
            .map { MedicalAccessInfo(it.medicalAccesses.first { it.doctor.uuid == doctorUuid }, it.allTypes) }
    }

    fun postMedicalAccessesForPatient(
        medicalAccesses: MedicalAccessesForPatient
    ): Completable = Completable.defer {
        api.postMedicalAccessesForPatient(
            MedicalAccessMapper.toModelForPatient(medicalAccesses)
        )
    }

}