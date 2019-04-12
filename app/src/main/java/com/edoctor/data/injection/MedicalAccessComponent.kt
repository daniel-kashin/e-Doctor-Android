package com.edoctor.data.injection

import com.edoctor.presentation.app.doctor.DoctorActivity
import com.edoctor.presentation.app.editMedicalAccess.EditMedicalAccessActivity
import com.edoctor.presentation.app.medicalAccessesForPatient.MedicalAccessesForPatientFragment
import dagger.Subcomponent

@Subcomponent(modules = [MedicalAccessModule::class])
interface MedicalAccessComponent {
    fun inject(medicalAccessesForPatientFragment: MedicalAccessesForPatientFragment)
    fun inject(doctorActivity: DoctorActivity)
    fun inject(editMedicalAccessActivity: EditMedicalAccessActivity)
}