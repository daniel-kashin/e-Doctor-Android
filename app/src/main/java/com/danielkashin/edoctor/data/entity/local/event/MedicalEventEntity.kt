package com.danielkashin.edoctor.data.entity.local.event

import com.pushtorefresh.storio3.sqlite.annotations.StorIOSQLiteColumn
import com.pushtorefresh.storio3.sqlite.annotations.StorIOSQLiteCreator
import com.pushtorefresh.storio3.sqlite.annotations.StorIOSQLiteType

@StorIOSQLiteType(table = MedicalEventEntityContract.TABLE_NAME)
data class MedicalEventEntity @StorIOSQLiteCreator constructor(

    @StorIOSQLiteColumn(name = MedicalEventEntityContract.COLUMN_UUID, key = true)
    val uuid: String,

    @StorIOSQLiteColumn(name = MedicalEventEntityContract.COLUMN_TYPE)
    val type: Int,

    @StorIOSQLiteColumn(name = MedicalEventEntityContract.COLUMN_IS_CHANGED_LOCALLY)
    val isChangedLocally: Int,

    @StorIOSQLiteColumn(name = MedicalEventEntityContract.COLUMN_IS_DELETED)
    var isDeleted: Int,

    @StorIOSQLiteColumn(name = MedicalEventEntityContract.COLUMN_TIMESTAMP)
    var timestamp: Long,

    @StorIOSQLiteColumn(name = MedicalEventEntityContract.COLUMN_PATIENT_UUID)
    val patientUuid: String,

    @StorIOSQLiteColumn(name = MedicalEventEntityContract.COLUMN_IS_ADDED_FROM_DOCTOR)
    var isAddedFromDoctor: Int = 0,

    @StorIOSQLiteColumn(name = MedicalEventEntityContract.COLUMN_END_TIMESTAMP)
    var endTimestamp: Long? = null,

    @StorIOSQLiteColumn(name = MedicalEventEntityContract.COLUMN_DOCTOR_CREATOR_UUID)
    val doctorCreatorUuid: String? = null,

    @StorIOSQLiteColumn(name = MedicalEventEntityContract.COLUMN_NAME)
    var name: String? = null,

    @StorIOSQLiteColumn(name = MedicalEventEntityContract.COLUMN_CLINIC)
    var clinic: String? = null,

    @StorIOSQLiteColumn(name = MedicalEventEntityContract.COLUMN_DOCTOR_NAME)
    var doctorName: String? = null,

    @StorIOSQLiteColumn(name = MedicalEventEntityContract.COLUMN_DOCTOR_SPECIALIZATION)
    var doctorSpecialization: String? = null,

    @StorIOSQLiteColumn(name = MedicalEventEntityContract.COLUMN_SYMPTOMS)
    var symptoms: String? = null,

    @StorIOSQLiteColumn(name = MedicalEventEntityContract.COLUMN_DIAGNOSIS)
    var diagnosis: String? = null,

    @StorIOSQLiteColumn(name = MedicalEventEntityContract.COLUMN_RECIPE)
    var recipe: String? = null,

    @StorIOSQLiteColumn(name = MedicalEventEntityContract.COLUMN_COMMENT)
    var comment: String? = null

)

class MedicalEventEntityType(
    val type: Int
)