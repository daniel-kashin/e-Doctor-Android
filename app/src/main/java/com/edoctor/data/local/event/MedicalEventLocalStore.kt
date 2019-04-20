package com.edoctor.data.local.event

import com.edoctor.data.entity.local.event.MedicalEventEntity
import com.edoctor.data.entity.local.event.MedicalEventEntityContract
import com.edoctor.data.entity.local.event.MedicalEventEntityContract.COLUMN_DOCTOR_CREATOR_UUID
import com.edoctor.data.entity.local.event.MedicalEventEntityContract.COLUMN_IS_ADDED_FROM_DOCTOR
import com.edoctor.data.entity.local.event.MedicalEventEntityContract.COLUMN_IS_CHANGED_LOCALLY
import com.edoctor.data.entity.local.event.MedicalEventEntityContract.COLUMN_IS_DELETED
import com.edoctor.data.entity.local.event.MedicalEventEntityContract.COLUMN_PATIENT_UUID
import com.edoctor.data.local.base.BaseLocalStore
import com.pushtorefresh.storio3.sqlite.StorIOSQLite
import com.pushtorefresh.storio3.sqlite.queries.Query
import io.reactivex.Completable
import io.reactivex.Single

class MedicalEventLocalStore(storIOSQLite: StorIOSQLite) : BaseLocalStore<MedicalEventEntity>(storIOSQLite) {

    override val objectClass = MedicalEventEntity::class.java
    override val tableName = MedicalEventEntityContract.TABLE_NAME
    override val idColumnName = MedicalEventEntityContract.COLUMN_UUID

    fun getRequestedEventsForPatient(doctorUuid: String, patientUuid: String): Single<List<MedicalEventEntity>> {
        return getAllByQuery(
            Query.builder()
                .table(tableName)
                .where("$COLUMN_PATIENT_UUID = ? AND $COLUMN_DOCTOR_CREATOR_UUID = ? AND $COLUMN_IS_DELETED = 0")
                .whereArgs(patientUuid, doctorUuid)
                .build()
        )
    }

    fun getEventsForPatient(patientUuid: String): Single<List<MedicalEventEntity>> {
        return getAllByQuery(
            Query.builder()
                .table(tableName)
                .where("$COLUMN_PATIENT_UUID = ? AND $COLUMN_IS_DELETED = 0 AND ($COLUMN_DOCTOR_CREATOR_UUID IS NULL or $COLUMN_IS_ADDED_FROM_DOCTOR = 1)")
                .whereArgs(patientUuid)
                .build()
        )
    }

    fun markAsDeleted(uuid: String): Completable =
        getById(uuid).flatMapCompletable { optional ->
            if (optional.isPresent) {
                save(optional.get().copy(isChangedLocally = 1, isDeleted = 1)).ignoreElement()
            } else {
                Completable.complete()
            }
        }

    fun getEventsToSynchronizeForPatient(
        patientUuid: String
    ): Single<List<MedicalEventEntity>> =
        getAllByQuery(
            Query.builder()
                .table(tableName)
                .where("$COLUMN_PATIENT_UUID = ? AND $COLUMN_IS_CHANGED_LOCALLY = 1")
                .whereArgs(patientUuid)
                .build()
        )

}