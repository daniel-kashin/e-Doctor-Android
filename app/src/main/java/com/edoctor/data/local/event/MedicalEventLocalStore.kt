package com.edoctor.data.local.event

import com.edoctor.data.entity.local.event.MedicalEventEntity
import com.edoctor.data.entity.local.event.MedicalEventEntityContract
import com.edoctor.data.local.base.BaseLocalStore
import com.pushtorefresh.storio3.sqlite.StorIOSQLite

class MedicalEventLocalStore(storIOSQLite: StorIOSQLite) : BaseLocalStore<MedicalEventEntity>(storIOSQLite) {

    override val objectClass = MedicalEventEntity::class.java
    override val tableName = MedicalEventEntityContract.TABLE_NAME
    override val idColumnName = MedicalEventEntityContract.COLUMN_UUID

}