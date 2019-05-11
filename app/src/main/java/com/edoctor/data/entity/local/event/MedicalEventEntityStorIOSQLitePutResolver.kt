package com.edoctor.data.entity.local.event

import android.content.ContentValues
import com.pushtorefresh.storio3.sqlite.operations.put.DefaultPutResolver
import com.pushtorefresh.storio3.sqlite.queries.InsertQuery
import com.pushtorefresh.storio3.sqlite.queries.UpdateQuery

/**
 * Generated resolver for Put Operation.
 */
class MedicalEventEntityStorIOSQLitePutResolver : DefaultPutResolver<MedicalEventEntity>() {
    /**
     * {@inheritDoc}
     */
    public override fun mapToInsertQuery(`object`: MedicalEventEntity): InsertQuery {
        return InsertQuery.builder()
            .table("medical_events")
            .build()
    }

    /**
     * {@inheritDoc}
     */
    public override fun mapToUpdateQuery(`object`: MedicalEventEntity): UpdateQuery {
        return UpdateQuery.builder()
            .table("medical_events")
            .where("uuid = ?")
            .whereArgs(`object`.uuid)
            .build()
    }

    /**
     * {@inheritDoc}
     */
    public override fun mapToContentValues(`object`: MedicalEventEntity): ContentValues {
        val contentValues = ContentValues(17)

        contentValues.put("uuid", `object`.uuid)
        contentValues.put("type", `object`.type)
        contentValues.put("timestamp", `object`.timestamp)
        contentValues.put("is_changed_locally", `object`.isChangedLocally)
        contentValues.put("is_deleted", `object`.isDeleted)
        contentValues.put("patient_uuid", `object`.patientUuid)
        contentValues.put("is_added_from_doctor", `object`.isAddedFromDoctor)
        contentValues.put("end_timestamp", `object`.endTimestamp)
        contentValues.put("doctor_creator_uuid", `object`.doctorCreatorUuid)
        contentValues.put("name", `object`.name)
        contentValues.put("clinic", `object`.clinic)
        contentValues.put("doctor_name", `object`.doctorName)
        contentValues.put("doctor_specialization", `object`.doctorSpecialization)
        contentValues.put("symptoms", `object`.symptoms)
        contentValues.put("diagnosis", `object`.diagnosis)
        contentValues.put("recipe", `object`.recipe)
        contentValues.put("comment", `object`.comment)

        return contentValues
    }
}
