package com.infostrategic.edoctor.presentation.app.findDoctor

import com.infostrategic.edoctor.data.injection.FindDoctorModule
import dagger.Subcomponent

@Subcomponent(modules = [FindDoctorModule::class])
interface FindDoctorComponent {
    fun inject(findDoctorFragment: FindDoctorFragment)
}