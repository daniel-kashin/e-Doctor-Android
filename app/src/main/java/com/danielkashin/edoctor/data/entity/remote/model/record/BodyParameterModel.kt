package com.danielkashin.edoctor.data.entity.remote.model.record

import java.io.Serializable

sealed class BodyParameterModel : MedicalRecordModel(), DateSpecific, Serializable {
    abstract val value: Double
}

data class HeightModel(
    override val uuid: String,
    override val timestamp: Long,
    val centimeters: Double
) : BodyParameterModel() {
    override val value = centimeters
}

data class WeightModel(
    override val uuid: String,
    override val timestamp: Long,
    val kilograms: Double
) : BodyParameterModel() {
    override val value = kilograms
}

data class BloodPressureModel(
    override val uuid: String,
    override val timestamp: Long,
    val systolicMmHg: Int,
    val diastolicMmHg: Int
) : BodyParameterModel()     {
    override val value = systolicMmHg.toDouble()
}


data class BloodSugarModel(
    override val uuid: String,
    override val timestamp: Long,
    val mmolPerLiter: Double
) : BodyParameterModel() {
    override val value = mmolPerLiter
}

data class TemperatureModel(
    override val uuid: String,
    override val timestamp: Long,
    val celsiusDegrees: Double
) : BodyParameterModel() {
    override val value = celsiusDegrees
}

data class BloodOxygenModel(
    override val uuid: String,
    override val timestamp: Long,
    val percents: Int
) : BodyParameterModel() {
    override val value = percents.toDouble()
}

data class CustomBodyParameterModel(
    override val uuid: String,
    override val timestamp: Long,
    val name: String,
    val unit: String,
    override val value: Double
) : BodyParameterModel()
