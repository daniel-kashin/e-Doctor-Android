package com.edoctor.data.entity.local.parameter

import android.content.ContentValues
import com.pushtorefresh.storio3.sqlite.operations.put.DefaultPutResolver
import com.pushtorefresh.storio3.sqlite.queries.InsertQuery
import com.pushtorefresh.storio3.sqlite.queries.UpdateQuery

/**
 * Generated resolver for Put Operation.
 */
class BodyParameterEntityStorIOSQLitePutResolver : DefaultPutResolver<BodyParameterEntity>() {
    /**
     * {@inheritDoc}
     */
    public override fun mapToInsertQuery(`object`: BodyParameterEntity): InsertQuery {
        return InsertQuery.builder()
            .table("body_parameters")
            .build()
    }

    /**
     * {@inheritDoc}
     */
    public override fun mapToUpdateQuery(`object`: BodyParameterEntity): UpdateQuery {
        return UpdateQuery.builder()
            .table("body_parameters")
            .where("uuid = ?")
            .whereArgs(`object`.uuid)
            .build()
    }

    /**
     * {@inheritDoc}
     */
    public override fun mapToContentValues(`object`: BodyParameterEntity): ContentValues {
        val contentValues = ContentValues(10)

        contentValues.put("uuid", `object`.uuid)
        contentValues.put("type", `object`.type)
        contentValues.put("is_changed_locally", `object`.isChangedLocally)
        contentValues.put("is_deleted", `object`.isDeleted)
        contentValues.put("measurement_timestamp", `object`.measurementTimestamp)
        contentValues.put("patient_uuid", `object`.patientUuid)
        contentValues.put("first_value", `object`.firstValue)
        contentValues.put("second_value", `object`.secondValue)
        contentValues.put("custom_model_name", `object`.customModelName)
        contentValues.put("custom_model_unit", `object`.customModelUnit)

        return contentValues
    }
}
