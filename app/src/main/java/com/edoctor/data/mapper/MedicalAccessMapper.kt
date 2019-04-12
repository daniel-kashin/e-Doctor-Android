package com.edoctor.data.mapper

import com.edoctor.data.entity.presentation.MedicalAccessForDoctor
import com.edoctor.data.entity.presentation.MedicalAccessForPatient
import com.edoctor.data.entity.presentation.MedicalAccessesForDoctor
import com.edoctor.data.entity.presentation.MedicalAccessesForPatient
import com.edoctor.data.entity.remote.model.medicalAccess.MedicalAccessForPatientModel
import com.edoctor.data.entity.remote.model.medicalAccess.MedicalAccessesForDoctorModel
import com.edoctor.data.entity.remote.model.medicalAccess.MedicalAccessesForPatientModel
import com.edoctor.data.mapper.UserMapper.withAbsoluteUrl

object MedicalAccessMapper {

    fun toPresentationForDoctor(
        medicalAccessesForDoctorModel: MedicalAccessesForDoctorModel
    ): MedicalAccessesForDoctor {
        val presentationAccesses = medicalAccessesForDoctorModel.medicalAccesses.map {
            val presentationAvailableTypes = it.availableTypes.mapNotNull {
                MedicalRecordTypeMapper.toPresentation(it)
            }
            val presentationAllTypes = it.allTypes.mapNotNull {
                MedicalRecordTypeMapper.toPresentation(it)
            }

            MedicalAccessForDoctor(withAbsoluteUrl(it.patient), presentationAvailableTypes, presentationAllTypes)
        }

        return MedicalAccessesForDoctor(presentationAccesses)
    }

    fun toPresentationForPatient(
        medicalAccessesForPatientModel: MedicalAccessesForPatientModel
    ): MedicalAccessesForPatient {
        val presentationAllTypes = medicalAccessesForPatientModel.allTypes
            .sortedBy { it.medicalRecordType }
            .mapNotNull { MedicalRecordTypeMapper.toPresentation(it) }

        val presentationAccesses = medicalAccessesForPatientModel.medicalAccesses.map {
            val presentationAvailableTypes = it.availableTypes.mapNotNull {
                MedicalRecordTypeMapper.toPresentation(it)
            }
            MedicalAccessForPatient(withAbsoluteUrl(it.doctor), presentationAvailableTypes)
        }

        return MedicalAccessesForPatient(presentationAccesses, presentationAllTypes)
    }

    fun toModelForPatient(
        medicalAccessesForPatient: MedicalAccessesForPatient
    ): MedicalAccessesForPatientModel {
        val modelAccesses = medicalAccessesForPatient.medicalAccesses.map {
            val presentationAvailableTypes = it.availableTypes.mapNotNull {
                MedicalRecordTypeMapper.toModel(it)
            }
            MedicalAccessForPatientModel(it.doctor, presentationAvailableTypes)
        }

        return MedicalAccessesForPatientModel(modelAccesses, emptyList())
    }


}