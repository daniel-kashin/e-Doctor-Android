package com.edoctor.data.repository

import com.edoctor.data.entity.presentation.*
import com.edoctor.data.mapper.MedicalAccessMapper
import com.edoctor.data.remote.rest.MedicalAccessesRestApi
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
    ): Single<Pair<List<MedicalRecordType>, MedicalAccessForPatient>> {
        return api.getMedicalAccessesForPatient(doctorUuid)
            .map { MedicalAccessMapper.toPresentationForPatient(it) }
            .map { it.allTypes to it.medicalAccesses.first { it.doctor.uuid == doctorUuid } }
    }

    fun postMedicalAccessesForPatient(
        medicalAccesses: MedicalAccessesForPatient
    ): Completable = Completable.defer {
        api.postMedicalAccessesForPatient(
            MedicalAccessMapper.toModelForPatient(medicalAccesses)
        )
    }

}