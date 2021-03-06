package com.infostrategic.edoctor.data.injection

import com.infostrategic.edoctor.data.remote.rest.FindDoctorRestApi
import com.infostrategic.edoctor.data.repository.FindDoctorRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named

@Module
class FindDoctorModule {

    @Provides
    internal fun provideFindDoctorRestApi(
        @Named(NetworkModule.AUTHORIZED_TAG)
        builder: Retrofit.Builder
    ): FindDoctorRestApi = builder.build().create(FindDoctorRestApi::class.java)

    @Provides
    internal fun provideFindDoctorRepository(
        api: FindDoctorRestApi
    ): FindDoctorRepository = FindDoctorRepository(api)

}