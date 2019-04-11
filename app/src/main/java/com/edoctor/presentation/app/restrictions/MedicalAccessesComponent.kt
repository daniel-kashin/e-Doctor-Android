package com.edoctor.presentation.app.restrictions

import com.edoctor.data.injection.MedicalAccessesModule
import com.edoctor.presentation.app.doctor.DoctorActivity
import dagger.Subcomponent

@Subcomponent(modules = [MedicalAccessesModule::class])
interface MedicalAccessesComponent {
    fun inject(medicalAccessesFragment: MedicalAccessesFragment)
    fun inject(doctorActivity: DoctorActivity)
}