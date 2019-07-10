package com.danielkashin.edoctor.data.local.parameter

import com.danielkashin.edoctor.data.entity.local.parameter.BodyParameterEntity
import com.danielkashin.edoctor.data.entity.local.parameter.BodyParameterEntityContract.COLUMN_TYPE
import com.danielkashin.edoctor.data.entity.local.parameter.BodyParameterEntityContract.TABLE_NAME
import com.danielkashin.edoctor.data.entity.local.parameter.BodyParameterEntityContract.COLUMN_UUID
import com.danielkashin.edoctor.data.entity.local.parameter.BodyParameterEntityContract.COLUMN_CUSTOM_MODEL_UNIT
import com.danielkashin.edoctor.data.entity.local.parameter.BodyParameterEntityContract.COLUMN_CUSTOM_MODEL_NAME
import com.danielkashin.edoctor.data.entity.local.parameter.BodyParameterEntityContract.COLUMN_IS_CHANGED_LOCALLY
import com.danielkashin.edoctor.data.entity.local.parameter.BodyParameterEntityContract.COLUMN_IS_DELETED
import com.danielkashin.edoctor.data.entity.local.parameter.BodyParameterEntityContract.COLUMN_PATIENT_UUID
import com.danielkashin.edoctor.data.entity.local.parameter.BodyParameterEntityType
import com.danielkashin.edoctor.data.local.base.BaseLocalStore
import com.pushtorefresh.storio3.sqlite.StorIOSQLite
import com.pushtorefresh.storio3.sqlite.queries.Query
import io.reactivex.Completable
import io.reactivex.Single

class BodyParameterLocalStore(storIOSQLite: StorIOSQLite) : BaseLocalStore<BodyParameterEntity>(storIOSQLite) {

    override val objectClass = BodyParameterEntity::class.java
    override val tableName = TABLE_NAME
    override val idColumnName = COLUMN_UUID

    private fun getParametersForPatientQuery(patientUuid: String, type: BodyParameterEntityType) =
        if (type.customModelUnit != null && type.customModelName != null) {
            Query.builder()
                .table(tableName)
                .where(
                    "$COLUMN_PATIENT_UUID = ? AND " +
                            "$COLUMN_TYPE = ? AND $COLUMN_CUSTOM_MODEL_UNIT = ? AND $COLUMN_CUSTOM_MODEL_NAME = ? AND " +
                            "$COLUMN_IS_DELETED = 0"
                )
                .whereArgs(patientUuid, type.type, type.customModelUnit, type.customModelName)
                .build()
        } else {
            Query.builder()
                .table(tableName)
                .where("$COLUMN_PATIENT_UUID = ? AND $COLUMN_TYPE = ? AND $COLUMN_IS_DELETED = 0")
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
                .where("$COLUMN_PATIENT_UUID = ? AND $COLUMN_IS_DELETED = 0")
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

    fun markAsDeleted(uuid: String): Completable =
        getById(uuid).flatMapCompletable { optional ->
            if (optional.isPresent) {
                save(optional.get().copy(isChangedLocally = 1, isDeleted = 1)).ignoreElement()
            } else {
                Completable.complete()
            }
        }

    fun getParametersToSynchronizeForPatient(
        patientUuid: String
    ): Single<List<BodyParameterEntity>> =
        getAllByQuery(
            Query.builder()
                .table(tableName)
                .where("$COLUMN_PATIENT_UUID = ? AND $COLUMN_IS_CHANGED_LOCALLY = 1")
                .whereArgs(patientUuid)
                .build()
        )

}