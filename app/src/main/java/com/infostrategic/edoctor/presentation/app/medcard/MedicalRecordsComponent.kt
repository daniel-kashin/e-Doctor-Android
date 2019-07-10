package com.infostrategic.edoctor.presentation.app.medcard

import com.infostrategic.edoctor.data.injection.MedicalRecordsModule
import com.infostrategic.edoctor.presentation.app.events.EventsFragment
import com.infostrategic.edoctor.presentation.app.parameter.ParameterActivity
import com.infostrategic.edoctor.presentation.app.parameters.ParametersFragment
import dagger.Subcomponent

@Subcomponent(modules = [MedicalRecordsModule::class])
interface MedicalRecordsComponent {
    fun inject(parametersFragment: ParametersFragment)
    fun inject(parameterFragment: ParameterActivity)
    fun inject(eventsFragment: EventsFragment)
}