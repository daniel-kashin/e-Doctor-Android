package com.edoctor.presentation.app.findDoctor

import com.edoctor.data.injection.FindDoctorModule
import dagger.Subcomponent

@Subcomponent(modules = [FindDoctorModule::class])
interface FindDoctorComponent {
    fun inject(findDoctorFragment: FindDoctorFragment)
}