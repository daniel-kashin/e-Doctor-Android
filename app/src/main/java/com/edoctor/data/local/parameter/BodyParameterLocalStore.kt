package com.edoctor.data.local.parameter

import com.edoctor.data.entity.local.parameter.BodyParameterEntity
import com.edoctor.data.entity.local.parameter.BodyParameterEntityContract
import com.edoctor.data.local.base.BaseLocalStore
import com.pushtorefresh.storio3.sqlite.StorIOSQLite

class BodyParameterLocalStore(storIOSQLite: StorIOSQLite) : BaseLocalStore<BodyParameterEntity>(storIOSQLite) {

    override val objectClass = BodyParameterEntity::class.java
    override val tableName = BodyParameterEntityContract.TABLE_NAME
    override val idColumnName = BodyParameterEntityContract.COLUMN_UUID

}