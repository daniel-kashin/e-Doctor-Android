package com.danielkashin.edoctor.presentation.app.findDoctor

import com.danielkashin.edoctor.data.injection.FindDoctorModule
import dagger.Subcomponent

@Subcomponent(modules = [FindDoctorModule::class])
interface FindDoctorComponent {
    fun inject(findDoctorFragment: FindDoctorFragment)
}