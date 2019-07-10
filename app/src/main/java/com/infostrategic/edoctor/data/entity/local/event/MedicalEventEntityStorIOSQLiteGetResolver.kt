package com.infostrategic.edoctor.data.entity.local.event

import android.database.Cursor
import com.pushtorefresh.storio3.sqlite.StorIOSQLite
import com.pushtorefresh.storio3.sqlite.operations.get.DefaultGetResolver

/**
 * Generated resolver for Get Operation.
 */
class MedicalEventEntityStorIOSQLiteGetResolver : DefaultGetResolver<MedicalEventEntity>() {
    /**
     * {@inheritDoc}
     */
    override fun mapFromCursor(
        storIOSQLite: StorIOSQLite,
        cursor: Cursor
    ): MedicalEventEntity {

        val uuid = cursor.getString(cursor.getColumnIndex("uuid"))
        val type = cursor.getInt(cursor.getColumnIndex("type"))
        val timestamp = cursor.getLong(cursor.getColumnIndex("timestamp"))
        val patientUuid = cursor.getString(cursor.getColumnIndex("patient_uuid"))
        val isAddedFromDoctor = cursor.getInt(cursor.getColumnIndex("is_added_from_doctor"))
        var endTimestamp: Long? = null
        if (!cursor.isNull(cursor.getColumnIndex("end_timestamp"))) {
            endTimestamp = cursor.getLong(cursor.getColumnIndex("end_timestamp"))
        }
        val doctorCreatorUuid = cursor.getString(cursor.getColumnIndex("doctor_creator_uuid"))
        val name = cursor.getString(cursor.getColumnIndex("name"))
        val clinic = cursor.getString(cursor.getColumnIndex("clinic"))
        val doctorName = cursor.getString(cursor.getColumnIndex("doctor_name"))
        val doctorSpecialization = cursor.getString(cursor.getColumnIndex("doctor_specialization"))
        val symptoms = cursor.getString(cursor.getColumnIndex("symptoms"))
        val diagnosis = cursor.getString(cursor.getColumnIndex("diagnosis"))
        val recipe = cursor.getString(cursor.getColumnIndex("recipe"))
        val comment = cursor.getString(cursor.getColumnIndex("comment"))
        val isChangedLocally = cursor.getInt(cursor.getColumnIndex("is_changed_locally"))
        val isDeleted = cursor.getInt(cursor.getColumnIndex("is_deleted"))

        return MedicalEventEntity(
            uuid,
            type,
            isChangedLocally,
            isDeleted,
            timestamp,
            patientUuid,
            isAddedFromDoctor,
            endTimestamp,
            doctorCreatorUuid,
            name,
            clinic,
            doctorName,
            doctorSpecialization,
            symptoms,
            diagnosis,
            recipe,
            comment
        )
    }
}
