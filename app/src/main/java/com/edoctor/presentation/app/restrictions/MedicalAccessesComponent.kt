package com.edoctor.presentation.app.restrictions

import com.edoctor.data.injection.MedicalAccessesModule
import dagger.Subcomponent

@Subcomponent(modules = [MedicalAccessesModule::class])
interface MedicalAccessesComponent {
    fun inject(medicalAccessesFragment: MedicalAccessesFragment)
}