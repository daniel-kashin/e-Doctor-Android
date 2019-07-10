package com.danielkashin.edoctor.data.entity.local.parameter

import com.pushtorefresh.storio3.sqlite.operations.delete.DefaultDeleteResolver
import com.pushtorefresh.storio3.sqlite.queries.DeleteQuery

/**
 * Generated resolver for Delete Operation.
 */
class BodyParameterEntityStorIOSQLiteDeleteResolver : DefaultDeleteResolver<BodyParameterEntity>() {
    /**
     * {@inheritDoc}
     */
    public override fun mapToDeleteQuery(`object`: BodyParameterEntity): DeleteQuery {
        return DeleteQuery.builder()
            .table("body_parameters")
            .where("uuid = ?")
            .whereArgs(`object`.uuid)
            .build()
    }
}
