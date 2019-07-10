package com.danielkashin.edoctor.data.entity.local.parameter

import com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping

/**
 * Generated mapping with collection of resolvers.
 */
class BodyParameterEntitySQLiteTypeMapping : SQLiteTypeMapping<BodyParameterEntity>(
    BodyParameterEntityStorIOSQLitePutResolver(),
    BodyParameterEntityStorIOSQLiteGetResolver(),
    BodyParameterEntityStorIOSQLiteDeleteResolver()
)
