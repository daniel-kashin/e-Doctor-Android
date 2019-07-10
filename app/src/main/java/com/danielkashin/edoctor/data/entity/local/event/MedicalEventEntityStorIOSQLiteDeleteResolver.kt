package com.danielkashin.edoctor.data.entity.local.event

import com.pushtorefresh.storio3.sqlite.operations.delete.DefaultDeleteResolver
import com.pushtorefresh.storio3.sqlite.queries.DeleteQuery

/**
 * Generated resolver for Delete Operation.
 */
class MedicalEventEntityStorIOSQLiteDeleteResolver : DefaultDeleteResolver<MedicalEventEntity>() {
    /**
     * {@inheritDoc}
     */
    public override fun mapToDeleteQuery(`object`: MedicalEventEntity): DeleteQuery {
        return DeleteQuery.builder()
            .table("medical_events")
            .where("uuid = ?")
            .whereArgs(`object`.uuid)
            .build()
    }
}
