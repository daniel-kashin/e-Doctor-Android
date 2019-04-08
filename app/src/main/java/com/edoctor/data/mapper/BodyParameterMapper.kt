package com.edoctor.data.mapper

import com.edoctor.data.entity.presentation.BodyParameterType
import com.edoctor.data.entity.remote.model.record.*
import com.edoctor.data.entity.presentation.BodyParameterType.*
import com.edoctor.data.entity.remote.model.record.BodyParameterWrapper.Companion.TYPE_BLOOD_OXYGEN
import com.edoctor.data.entity.remote.model.record.BodyParameterWrapper.Companion.TYPE_BLOOD_PRESSURE
import com.edoctor.data.entity.remote.model.record.BodyParameterWrapper.Companion.TYPE_BLOOD_SUGAR
import com.edoctor.data.entity.remote.model.record.BodyParameterWrapper.Companion.TYPE_CUSTOM
import com.edoctor.data.entity.remote.model.record.BodyParameterWrapper.Companion.TYPE_HEIGHT
import com.edoctor.data.entity.remote.model.record.BodyParameterWrapper.Companion.TYPE_TEMPERATURE
import com.edoctor.data.entity.remote.model.record.BodyParameterWrapper.Companion.TYPE_WEIGHT
import com.edoctor.data.entity.remote.request.BodyParameterTypeWrapper

object BodyParameterMapper {

    fun toWrapperModel(bodyParameterModel: BodyParameterModel): BodyParameterWrapper = bodyParameterModel.let {
        when (it) {
            is HeightModel -> {
                BodyParameterWrapper(it.uuid, it.measurementTimestamp, TYPE_HEIGHT, it.centimeters)
            }
            is WeightModel -> {
                BodyParameterWrapper(it.uuid, it.measurementTimestamp, TYPE_WEIGHT, it.kilograms)
            }
            is BloodOxygenModel -> {
                BodyParameterWrapper(it.uuid, it.measurementTimestamp, TYPE_BLOOD_OXYGEN, it.percents.toDouble())
            }
            is BloodSugarModel -> {
                BodyParameterWrapper(it.uuid, it.measurementTimestamp, TYPE_BLOOD_SUGAR, it.mmolPerLiter)
            }
            is TemperatureModel -> {
                BodyParameterWrapper(it.uuid, it.measurementTimestamp, TYPE_TEMPERATURE, it.celsiusDegrees)
            }
            is BloodPressureModel -> {
                BodyParameterWrapper(
                    uuid = it.uuid,
                    measurementTimestamp = it.measurementTimestamp,
                    type = TYPE_BLOOD_PRESSURE,
                    firstValue = it.systolicMmHg.toDouble(),
                    secondValue = it.diastolicMmHg.toDouble()
                )
            }
            is CustomBodyParameterModel -> {
                BodyParameterWrapper(
                    uuid = it.uuid,
                    measurementTimestamp = it.measurementTimestamp,
                    type = TYPE_CUSTOM,
                    firstValue = it.value,
                    customModelName = it.name,
                    customModelUnit = it.unit
                )
            }
        }
    }

    fun fromWrapperModel(bodyParameterWrapper: BodyParameterWrapper): BodyParameterModel? = bodyParameterWrapper.let {
        when (it.type) {
            TYPE_HEIGHT -> {
                HeightModel(it.uuid, it.measurementTimestamp, it.firstValue)
            }
            TYPE_WEIGHT -> {
                WeightModel(it.uuid, it.measurementTimestamp, it.firstValue)
            }
            TYPE_BLOOD_OXYGEN -> {
                BloodOxygenModel(it.uuid, it.measurementTimestamp, it.firstValue.toInt())
            }
            TYPE_BLOOD_SUGAR -> {
                BloodSugarModel(it.uuid, it.measurementTimestamp, it.firstValue)
            }
            TYPE_TEMPERATURE -> {
                TemperatureModel(it.uuid, it.measurementTimestamp, it.firstValue)
            }
            TYPE_BLOOD_PRESSURE -> {
                if (it.secondValue != null) {
                    BloodPressureModel(it.uuid, it.measurementTimestamp, it.firstValue.toInt(), it.secondValue.toInt())
                } else {
                    null
                }
            }
            TYPE_CUSTOM -> {
                if (it.customModelName != null && it.customModelUnit != null) {
                    CustomBodyParameterModel(
                        it.uuid,
                        it.measurementTimestamp,
                        it.customModelName,
                        it.customModelUnit,
                        it.firstValue
                    )
                } else {
                    null
                }
            }
            else -> null
        }
    }

    fun toType(bodyParameterModel: BodyParameterModel): BodyParameterType {
        return when (bodyParameterModel) {
            is HeightModel -> Height
            is WeightModel -> Weight
            is BloodOxygenModel -> BloodOxygen
            is BloodSugarModel -> BloodSugar
            is TemperatureModel -> Temperature
            is BloodPressureModel -> BloodPressure
            is CustomBodyParameterModel -> Custom(bodyParameterModel.name, bodyParameterModel.unit)
        }
    }

    fun toWrapperType(bodyParameterType: BodyParameterType): BodyParameterTypeWrapper {
        return when (bodyParameterType) {
            is BodyParameterType.BloodOxygen -> BodyParameterTypeWrapper(TYPE_BLOOD_OXYGEN)
            is BodyParameterType.BloodPressure -> BodyParameterTypeWrapper(TYPE_BLOOD_PRESSURE)
            is BodyParameterType.BloodSugar -> BodyParameterTypeWrapper(TYPE_BLOOD_SUGAR)
            is BodyParameterType.Custom -> BodyParameterTypeWrapper(TYPE_CUSTOM, bodyParameterType.name, bodyParameterType.unit)
            is BodyParameterType.Height -> BodyParameterTypeWrapper(TYPE_HEIGHT)
            is BodyParameterType.Temperature -> BodyParameterTypeWrapper(TYPE_TEMPERATURE)
            is BodyParameterType.Weight -> BodyParameterTypeWrapper(TYPE_WEIGHT)
        }
    }

}