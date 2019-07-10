package com.danielkashin.edoctor.data.entity.local.parameter

import android.database.Cursor
import com.pushtorefresh.storio3.sqlite.StorIOSQLite
import com.pushtorefresh.storio3.sqlite.operations.get.DefaultGetResolver

/**
 * Generated resolver for Get Operation.
 */
class BodyParameterEntityStorIOSQLiteGetResolver : DefaultGetResolver<BodyParameterEntity>() {
    /**
     * {@inheritDoc}
     */
    override fun mapFromCursor(
        storIOSQLite: StorIOSQLite,
        cursor: Cursor
    ): BodyParameterEntity {

        val uuid = cursor.getString(cursor.getColumnIndex("uuid"))
        val type = cursor.getInt(cursor.getColumnIndex("type"))
        val isChangedLocally = cursor.getInt(cursor.getColumnIndex("is_changed_locally"))
        val isDeleted = cursor.getInt(cursor.getColumnIndex("is_deleted"))
        val measurementTimestamp = cursor.getLong(cursor.getColumnIndex("measurement_timestamp"))
        val patientUuid = cursor.getString(cursor.getColumnIndex("patient_uuid"))
        val firstValue = cursor.getDouble(cursor.getColumnIndex("first_value"))
        var secondValue: Double? = null
        if (!cursor.isNull(cursor.getColumnIndex("second_value"))) {
            secondValue = cursor.getDouble(cursor.getColumnIndex("second_value"))
        }
        val customModelName = cursor.getString(cursor.getColumnIndex("custom_model_name"))
        val customModelUnit = cursor.getString(cursor.getColumnIndex("custom_model_unit"))

        return BodyParameterEntity(
            uuid,
            type,
            isChangedLocally,
            isDeleted,
            measurementTimestamp,
            patientUuid,
            firstValue,
            secondValue,
            customModelName,
            customModelUnit
        )
    }
}
