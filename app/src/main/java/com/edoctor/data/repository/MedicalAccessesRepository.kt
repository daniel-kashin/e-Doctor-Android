package com.edoctor.data.repository

import com.edoctor.data.entity.presentation.MedicalAccessForPatient
import com.edoctor.data.entity.presentation.MedicalAccessesForDoctor
import com.edoctor.data.entity.presentation.MedicalAccessesForPatient
import com.edoctor.data.entity.presentation.MedicalRecordType
import com.edoctor.data.mapper.MedicalAccessMapper
import com.edoctor.data.remote.rest.MedicalAccessesRestApi
import io.reactivex.Completable
import io.reactivex.Single

class MedicalAccessesRepository(
    private val api: MedicalAccessesRestApi
) {

    fun getMedicalAccessesForDoctor(
        patientUuid: String? = null
    ): Single<MedicalAccessesForDoctor> {
        return api.getMedicalAccessesForDoctor(patientUuid)
            .map { MedicalAccessMapper.toPresentationForDoctor(it) }
    }

    fun getMedicalAccessesForPatient(): Single<MedicalAccessesForPatient> {
        return api.getMedicalAccessesForPatient(null)
            .map { MedicalAccessMapper.toPresentationForPatient(it) }
    }

    fun getMedicalAccessForPatient(
        doctorUuid: String? = null
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