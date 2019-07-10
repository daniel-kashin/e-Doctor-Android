package com.danielkashin.edoctor.data.injection

import com.danielkashin.edoctor.data.local.event.MedicalEventLocalStore
import com.danielkashin.edoctor.data.local.parameter.BodyParameterLocalStore
import com.danielkashin.edoctor.data.remote.rest.MedicalEventsRestApi
import com.danielkashin.edoctor.data.remote.rest.ParametersRestApi
import com.danielkashin.edoctor.data.remote.rest.RequestedEventsRestApi
import com.danielkashin.edoctor.data.repository.MedicalRecordsRepository
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
    internal fun provideRequestedEventsRestApi(
        @Named(NetworkModule.AUTHORIZED_TAG)
        builder: Retrofit.Builder
    ): RequestedEventsRestApi = builder.build().create(RequestedEventsRestApi::class.java)

    @Provides
    fun provideMedicalRecordsRepository(
        parametersRestApi: ParametersRestApi,
        medicalEventsRestApi: MedicalEventsRestApi,
        requestedEventsRestApi: RequestedEventsRestApi,
        bodyParameterLocalStore: BodyParameterLocalStore,
        medicalEventLocalStore: MedicalEventLocalStore
    ) = MedicalRecordsRepository(
        parametersRestApi,
        medicalEventsRestApi,
        requestedEventsRestApi,
        bodyParameterLocalStore,
        medicalEventLocalStore
    )

}