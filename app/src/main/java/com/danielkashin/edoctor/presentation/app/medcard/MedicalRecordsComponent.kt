package com.danielkashin.edoctor.presentation.app.medcard

import com.danielkashin.edoctor.data.injection.MedicalRecordsModule
import com.danielkashin.edoctor.presentation.app.events.EventsFragment
import com.danielkashin.edoctor.presentation.app.parameter.ParameterActivity
import com.danielkashin.edoctor.presentation.app.parameters.ParametersFragment
import dagger.Subcomponent

@Subcomponent(modules = [MedicalRecordsModule::class])
interface MedicalRecordsComponent {
    fun inject(parametersFragment: ParametersFragment)
    fun inject(parameterFragment: ParameterActivity)
    fun inject(eventsFragment: EventsFragment)
}