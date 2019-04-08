package com.edoctor.presentation.app.medcard

import com.edoctor.data.injection.MedicalRecordsModule
import com.edoctor.presentation.app.events.EventsFragment
import com.edoctor.presentation.app.parameter.ParameterActivity
import com.edoctor.presentation.app.parameters.ParametersFragment
import dagger.Subcomponent

@Subcomponent(modules = [MedicalRecordsModule::class])
interface MedicalRecordsComponent {
    fun inject(parametersFragment: ParametersFragment)
    fun inject(parameterFragment: ParameterActivity)
    fun inject(eventsFragment: EventsFragment)
}