package com.danielkashin.edoctor.data.injection

import com.danielkashin.edoctor.data.remote.rest.MedicalAccessesRestApi
import com.danielkashin.edoctor.data.repository.MedicalAccessesRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named

@Module
class MedicalAccessModule {

    @Provides
    internal fun provideMedicalAccessesRestApi(
        @Named(NetworkModule.AUTHORIZED_TAG)
        builder: Retrofit.Builder
    ): MedicalAccessesRestApi = builder.build().create(MedicalAccessesRestApi::class.java)

    @Provides
    fun provideMedicalAccessesRepository(
        medicalAccessesRestApi: MedicalAccessesRestApi
    ) = MedicalAccessesRepository(medicalAccessesRestApi)

}