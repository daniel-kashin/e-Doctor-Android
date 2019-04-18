package com.edoctor.data.mapper

import com.edoctor.data.entity.local.parameter.BodyParameterEntity
import com.edoctor.data.entity.local.parameter.BodyParameterEntityType
import com.edoctor.data.entity.presentation.BodyParameterType
import com.edoctor.data.entity.remote.model.record.*
import com.edoctor.data.entity.presentation.BodyParameterType.*
import com.edoctor.data.entity.remote.model.user.PatientModel

import com.edoctor.data.entity.remote.request.BodyParameterTypeWrapper
import com.edoctor.data.mapper.MedicalRecordTypeMapper.BODY_PARAMETER_TYPE_BLOOD_OXYGEN
import com.edoctor.data.mapper.MedicalRecordTypeMapper.BODY_PARAMETER_TYPE_BLOOD_PRESSURE
import com.edoctor.data.mapper.MedicalRecordTypeMapper.BODY_PARAMETER_TYPE_BLOOD_SUGAR
import com.edoctor.data.mapper.MedicalRecordTypeMapper.BODY_PARAMETER_TYPE_CUSTOM
import com.edoctor.data.mapper.MedicalRecordTypeMapper.BODY_PARAMETER_TYPE_HEIGHT
import com.edoctor.data.mapper.MedicalRecordTypeMapper.BODY_PARAMETER_TYPE_TEMPERATURE
import com.edoctor.data.mapper.MedicalRecordTypeMapper.BODY_PARAMETER_TYPE_WEIGHT

object BodyParameterMapper {

    fun toWrapperFromLocal(bodyParameterEntity: BodyParameterEntity): BodyParameterWrapper = bodyParameterEntity.run {
        BodyParameterWrapper(
            uuid = uuid,
            measurementTimestamp = measurementTimestamp,
            isDeleted = isDeleted != 0,
            type = type,
            firstValue = firstValue,
            secondValue = secondValue,
            customModelName = customModelName,
            customModelUnit = customModelUnit
        )
    }

    fun toLocalFromWrapper(bodyParameterWrapper: BodyParameterWrapper, patientUuid: String, isChangedLocally: Boolean): BodyParameterEntity =
        bodyParameterWrapper.run {
            BodyParameterEntity(
                uuid = uuid,
                measurementTimestamp = measurementTimestamp,
                isChangedLocally = if (isChangedLocally) 1 else 0,
                isDeleted = if (isDeleted) 1 else 0,
                type = type,
                patientUuid = patientUuid,
                firstValue = firstValue,
                secondValue = secondValue,
                customModelName = customModelName,
                customModelUnit = customModelUnit
            )
        }

    fun toWrapperFromModel(bodyParameterModel: BodyParameterModel): BodyParameterWrapper = bodyParameterModel.let {
        when (it) {
            is HeightModel -> {
                BodyParameterWrapper(it.uuid, it.timestamp, false, BODY_PARAMETER_TYPE_HEIGHT, it.centimeters)
            }
            is WeightModel -> {
                BodyParameterWrapper(it.uuid, it.timestamp, false, BODY_PARAMETER_TYPE_WEIGHT, it.kilograms)
            }
            is BloodOxygenModel -> {
                BodyParameterWrapper(it.uuid, it.timestamp, false, BODY_PARAMETER_TYPE_BLOOD_OXYGEN, it.percents.toDouble())
            }
            is BloodSugarModel -> {
                BodyParameterWrapper(it.uuid, it.timestamp, false, BODY_PARAMETER_TYPE_BLOOD_SUGAR, it.mmolPerLiter)
            }
            is TemperatureModel -> {
                BodyParameterWrapper(it.uuid, it.timestamp, false, BODY_PARAMETER_TYPE_TEMPERATURE, it.celsiusDegrees)
            }
            is BloodPressureModel -> {
                BodyParameterWrapper(
                    uuid = it.uuid,
                    measurementTimestamp = it.timestamp,
                    isDeleted = false,
                    type = BODY_PARAMETER_TYPE_BLOOD_PRESSURE,
                    firstValue = it.systolicMmHg.toDouble(),
                    secondValue = it.diastolicMmHg.toDouble()
                )
            }
            is CustomBodyParameterModel -> {
                BodyParameterWrapper(
                    uuid = it.uuid,
                    measurementTimestamp = it.timestamp,
                    isDeleted = false,
                    type = BODY_PARAMETER_TYPE_CUSTOM,
                    firstValue = it.value,
                    customModelName = it.name,
                    customModelUnit = it.unit
                )
            }
        }
    }

    fun toModelFromWrapper(bodyParameterWrapper: BodyParameterWrapper): BodyParameterModel? = bodyParameterWrapper.let {
        when (it.type) {
            BODY_PARAMETER_TYPE_HEIGHT -> {
                HeightModel(it.uuid, it.measurementTimestamp, it.firstValue)
            }
            BODY_PARAMETER_TYPE_WEIGHT -> {
                WeightModel(it.uuid, it.measurementTimestamp, it.firstValue)
            }
            BODY_PARAMETER_TYPE_BLOOD_OXYGEN -> {
                BloodOxygenModel(it.uuid, it.measurementTimestamp, it.firstValue.toInt())
            }
            BODY_PARAMETER_TYPE_BLOOD_SUGAR -> {
                BloodSugarModel(it.uuid, it.measurementTimestamp, it.firstValue)
            }
            BODY_PARAMETER_TYPE_TEMPERATURE -> {
                TemperatureModel(it.uuid, it.measurementTimestamp, it.firstValue)
            }
            BODY_PARAMETER_TYPE_BLOOD_PRESSURE -> {
                if (it.secondValue != null) {
                    BloodPressureModel(it.uuid, it.measurementTimestamp, it.firstValue.toInt(), it.secondValue.toInt())
                } else {
                    null
                }
            }
            BODY_PARAMETER_TYPE_CUSTOM -> {
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
            is HeightModel -> Height()
            is WeightModel -> Weight()
            is BloodOxygenModel -> BloodOxygen()
            is BloodSugarModel -> BloodSugar()
            is TemperatureModel -> Temperature()
            is BloodPressureModel -> BloodPressure()
            is CustomBodyParameterModel -> Custom(bodyParameterModel.name, bodyParameterModel.unit)
        }
    }

    fun toWrapperType(bodyParameterType: BodyParameterType): BodyParameterTypeWrapper {
        return when (bodyParameterType) {
            is BodyParameterType.BloodOxygen -> BodyParameterTypeWrapper(BODY_PARAMETER_TYPE_BLOOD_OXYGEN)
            is BodyParameterType.BloodPressure -> BodyParameterTypeWrapper(BODY_PARAMETER_TYPE_BLOOD_PRESSURE)
            is BodyParameterType.BloodSugar -> BodyParameterTypeWrapper(BODY_PARAMETER_TYPE_BLOOD_SUGAR)
            is BodyParameterType.Custom -> BodyParameterTypeWrapper(
                BODY_PARAMETER_TYPE_CUSTOM,
                bodyParameterType.name,
                bodyParameterType.unit
            )
            is BodyParameterType.Height -> BodyParameterTypeWrapper(BODY_PARAMETER_TYPE_HEIGHT)
            is BodyParameterType.Temperature -> BodyParameterTypeWrapper(BODY_PARAMETER_TYPE_TEMPERATURE)
            is BodyParameterType.Weight -> BodyParameterTypeWrapper(BODY_PARAMETER_TYPE_WEIGHT)
        }
    }

    fun toEntityType(bodyParameterType: BodyParameterType): BodyParameterEntityType {
        return when (bodyParameterType) {
            is BodyParameterType.BloodOxygen -> BodyParameterEntityType(BODY_PARAMETER_TYPE_BLOOD_OXYGEN)
            is BodyParameterType.BloodPressure -> BodyParameterEntityType(BODY_PARAMETER_TYPE_BLOOD_PRESSURE)
            is BodyParameterType.BloodSugar -> BodyParameterEntityType(BODY_PARAMETER_TYPE_BLOOD_SUGAR)
            is BodyParameterType.Custom -> BodyParameterEntityType(
                BODY_PARAMETER_TYPE_CUSTOM,
                bodyParameterType.name,
                bodyParameterType.unit
            )
            is BodyParameterType.Height -> BodyParameterEntityType(BODY_PARAMETER_TYPE_HEIGHT)
            is BodyParameterType.Temperature -> BodyParameterEntityType(BODY_PARAMETER_TYPE_TEMPERATURE)
            is BodyParameterType.Weight -> BodyParameterEntityType(BODY_PARAMETER_TYPE_WEIGHT)
        }
    }

}