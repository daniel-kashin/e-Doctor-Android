package com.edoctor.data.injection

import com.edoctor.data.repository.MedicalRecordsRepository
import dagger.Module
import dagger.Provides

@Module
class MedicalRecordsModule() {

    @Provides
    fun provideMedicalRecordsRepository() = MedicalRecordsRepository()

}