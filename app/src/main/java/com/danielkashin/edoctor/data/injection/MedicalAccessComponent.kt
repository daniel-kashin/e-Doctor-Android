package com.danielkashin.edoctor.data.injection

import com.danielkashin.edoctor.presentation.app.doctor.DoctorActivity
import com.danielkashin.edoctor.presentation.app.editMedicalAccess.EditMedicalAccessActivity
import com.danielkashin.edoctor.presentation.app.medicalAccessesForDoctor.MedicalAccessesForDoctorFragment
import com.danielkashin.edoctor.presentation.app.medicalAccessesForPatient.MedicalAccessesForPatientFragment
import com.danielkashin.edoctor.presentation.app.patient.PatientActivity
import dagger.Subcomponent

@Subcomponent(modules = [MedicalAccessModule::class])
interface MedicalAccessComponent {
    fun inject(medicalAccessesForPatientFragment: MedicalAccessesForPatientFragment)
    fun inject(medicalAccessesForDoctorFragment: MedicalAccessesForDoctorFragment)
    fun inject(doctorActivity: DoctorActivity)
    fun inject(patientActivity: PatientActivity)
    fun inject(editMedicalAccessActivity: EditMedicalAccessActivity)
}