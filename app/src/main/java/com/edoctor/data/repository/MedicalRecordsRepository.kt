package com.edoctor.data.repository

import com.edoctor.data.entity.remote.model.record.*
import com.edoctor.utils.currentUnixTime
import io.reactivex.Single

class MedicalRecordsRepository() {

    fun getLatestBodyParametersOfEachType(): Single<List<BodyParameterModel>> {
        // TODO
        return Single.just(
            listOf(
                HeightModel("111", currentUnixTime(), 178.5),
                WeightModel("222", currentUnixTime() - 1000, 70.0),
                BloodPressureModel("333", currentUnixTime() - 2000, 120, 80),
                CustomBodyParameterModel("444", currentUnixTime() - 3000, "Размер ноги", "см", 26.5)
            )
        )
    }

}