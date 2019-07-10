package com.danielkashin.edoctor.data.entity.local.event

import com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping

/**
 * Generated mapping with collection of resolvers.
 */
class MedicalEventEntitySQLiteTypeMapping : SQLiteTypeMapping<MedicalEventEntity>(
    MedicalEventEntityStorIOSQLitePutResolver(),
    MedicalEventEntityStorIOSQLiteGetResolver(),
    MedicalEventEntityStorIOSQLiteDeleteResolver()
)
