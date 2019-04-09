package com.edoctor.data.injection

import com.edoctor.data.remote.rest.MedicalEventsRestApi
import com.edoctor.data.remote.rest.ParametersRestApi
import com.edoctor.data.repository.MedicalRecordsRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named

@Module
class MedicalRecordsModule {

    @Provides
    internal fun provideParametersRestApi(
        @Named(NetworkModule.AUTHORIZED_TAG)
        builder: Retrofit.Builder
    ): ParametersRestApi = builder.build().create(ParametersRestApi::class.java)

    @Provides
    internal fun provideMedicalEventsRestApi(
        @Named(NetworkModule.AUTHORIZED_TAG)
        builder: Retrofit.Builder
    ): MedicalEventsRestApi = builder.build().create(MedicalEventsRestApi::class.java)

    @Provides
    fun provideMedicalRecordsRepository(
        parametersRestApi: ParametersRestApi,
        medicalEventsRestApi: MedicalEventsRestApi
    ) = MedicalRecordsRepository(parametersRestApi, medicalEventsRestApi)

}