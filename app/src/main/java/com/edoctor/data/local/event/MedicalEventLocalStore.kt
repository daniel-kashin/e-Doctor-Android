package com.edoctor.data.local.event

import com.edoctor.data.entity.local.event.MedicalEventEntity
import com.edoctor.data.entity.local.event.MedicalEventEntityContract
import com.edoctor.data.local.base.BaseLocalStore
import com.pushtorefresh.storio3.sqlite.StorIOSQLite
import com.pushtorefresh.storio3.sqlite.queries.Query
import io.reactivex.Single

class MedicalEventLocalStore(storIOSQLite: StorIOSQLite) : BaseLocalStore<MedicalEventEntity>(storIOSQLite) {

    override val objectClass = MedicalEventEntity::class.java
    override val tableName = MedicalEventEntityContract.TABLE_NAME
    override val idColumnName = MedicalEventEntityContract.COLUMN_UUID

    fun getRequestedEventsForPatient(doctorUuid: String, patientUuid: String): Single<List<MedicalEventEntity>> {
        return getAllByQuery(
            Query.builder()
                .table(tableName)
                .where(
                    "${MedicalEventEntityContract.COLUMN_PATIENT_UUID} = ? AND " +
                            "${MedicalEventEntityContract.COLUMN_DOCTOR_CREATOR_UUID} = ?"
                )
                .whereArgs(patientUuid, doctorUuid)
                .build()
        )
    }

    fun getEventsForPatient(patientUuid: String): Single<List<MedicalEventEntity>> {
        return getAllByQuery(
            Query.builder()
                .table(tableName)
                .where("${MedicalEventEntityContract.COLUMN_PATIENT_UUID} = ?")
                .whereArgs(patientUuid)
                .build()
        )
    }

}