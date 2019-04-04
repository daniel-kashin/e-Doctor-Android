package com.edoctor.presentation.app.parameters

import com.edoctor.data.injection.MedicalRecordsModule
import dagger.Subcomponent

@Subcomponent(modules = [MedicalRecordsModule::class])
interface ParametersComponent {
    fun inject(parametersFragment: ParametersFragment)
}