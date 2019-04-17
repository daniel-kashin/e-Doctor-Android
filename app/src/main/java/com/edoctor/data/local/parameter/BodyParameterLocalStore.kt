package com.edoctor.data.local.parameter

import com.edoctor.data.entity.local.message.MessageEntityContract
import com.edoctor.data.entity.local.parameter.BodyParameterEntity
import com.edoctor.data.entity.local.parameter.BodyParameterEntityContract
import com.edoctor.data.entity.local.parameter.BodyParameterEntityContract.COLUMN_TYPE
import com.edoctor.data.entity.local.parameter.BodyParameterEntityContract.TABLE_NAME
import com.edoctor.data.entity.local.parameter.BodyParameterEntityContract.COLUMN_UUID
import com.edoctor.data.entity.local.parameter.BodyParameterEntityContract.COLUMN_CUSTOM_MODEL_UNIT
import com.edoctor.data.entity.local.parameter.BodyParameterEntityContract.COLUMN_CUSTOM_MODEL_NAME
import com.edoctor.data.entity.local.parameter.BodyParameterEntityContract.COLUMN_PATIENT_UUID
import com.edoctor.data.entity.local.parameter.BodyParameterEntityType
import com.edoctor.data.local.base.BaseLocalStore
import com.pushtorefresh.storio3.sqlite.StorIOSQLite
import com.pushtorefresh.storio3.sqlite.queries.Query
import io.reactivex.Single

class BodyParameterLocalStore(storIOSQLite: StorIOSQLite) : BaseLocalStore<BodyParameterEntity>(storIOSQLite) {

    override val objectClass = BodyParameterEntity::class.java
    override val tableName = TABLE_NAME
    override val idColumnName = COLUMN_UUID

    fun getParametersForPatientQuery(patientUuid: String, type: BodyParameterEntityType) =
        if (type.customModelUnit != null && type.customModelName != null) {
            Query.builder()
                .table(tableName)
                .where(
                    "$COLUMN_PATIENT_UUID = ? AND $COLUMN_TYPE = ? AND $COLUMN_CUSTOM_MODEL_UNIT = ? AND $COLUMN_CUSTOM_MODEL_NAME = ?"
                )
                .whereArgs(patientUuid, type.type, type.customModelUnit, type.customModelName)
                .build()
        } else {
            Query.builder()
                .table(tableName)
                .where("$COLUMN_PATIENT_UUID = ? AND $COLUMN_TYPE = ?")
                .whereArgs(patientUuid, type.type)
                .build()
        }

    fun getParametersForPatient(
        patientUuid: String,
        type: BodyParameterEntityType
    ): Single<List<BodyParameterEntity>> = getAllByQuery(getParametersForPatientQuery(patientUuid, type))

    fun getParametersForPatientBlocking(
        patientUuid: String,
        type: BodyParameterEntityType
    ): List<BodyParameterEntity> = getAllByQueryBlocking(getParametersForPatientQuery(patientUuid, type))

    fun getLatestParametersOfEachTypeForPatient(patientUuid: String): Single<List<BodyParameterEntity>> =
        getAllByQuery(
            Query.builder()
                .table(tableName)
                .groupBy("$COLUMN_TYPE, $COLUMN_CUSTOM_MODEL_UNIT, $COLUMN_CUSTOM_MODEL_NAME")
                .where("$COLUMN_PATIENT_UUID = ?")
                .whereArgs(patientUuid)
                .build()
        ).map { types ->
            types.mapNotNull { type ->
                getParametersForPatientBlocking(
                    patientUuid,
                    BodyParameterEntityType(type.type, type.customModelName, type.customModelUnit)
                ).maxBy { it.measurementTimestamp }
            }
        }


}