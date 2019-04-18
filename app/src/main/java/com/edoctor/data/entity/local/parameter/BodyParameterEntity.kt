package com.edoctor.data.entity.local.parameter

import com.pushtorefresh.storio3.sqlite.annotations.StorIOSQLiteColumn
import com.pushtorefresh.storio3.sqlite.annotations.StorIOSQLiteCreator
import com.pushtorefresh.storio3.sqlite.annotations.StorIOSQLiteType

@StorIOSQLiteType(table = BodyParameterEntityContract.TABLE_NAME)
data class BodyParameterEntity @StorIOSQLiteCreator constructor(

    @StorIOSQLiteColumn(name = BodyParameterEntityContract.COLUMN_UUID, key = true)
    val uuid: String,

    @StorIOSQLiteColumn(name = BodyParameterEntityContract.COLUMN_TYPE)
    val type: Int,

    @StorIOSQLiteColumn(name = BodyParameterEntityContract.COLUMN_UPDATE_TIMESTAMP)
    var updateTimestamp: Long,

    @StorIOSQLiteColumn(name = BodyParameterEntityContract.COLUMN_IS_DELETED)
    var isDeleted: Int,

    @StorIOSQLiteColumn(name = BodyParameterEntityContract.COLUMN_MEASUREMENT_TIMESTAMP)
    var measurementTimestamp: Long,

    @StorIOSQLiteColumn(name = BodyParameterEntityContract.COLUMN_PATIENT_UUID)
    val patientUuid: String,

    @StorIOSQLiteColumn(name = BodyParameterEntityContract.COLUMN_FIRST_VALUE)
    var firstValue: Double,

    @StorIOSQLiteColumn(name = BodyParameterEntityContract.COLUMN_SECOND_VALUE)
    var secondValue: Double? = null,

    @StorIOSQLiteColumn(name = BodyParameterEntityContract.COLUMN_CUSTOM_MODEL_NAME)
    val customModelName: String? = null,

    @StorIOSQLiteColumn(name = BodyParameterEntityContract.COLUMN_CUSTOM_MODEL_UNIT)
    val customModelUnit: String? = null

)

class BodyParameterEntityType(
    val type: Int,
    val customModelName: String? = null,
    val customModelUnit: String? = null
)
