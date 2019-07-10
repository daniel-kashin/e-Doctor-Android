package com.infostrategic.edoctor.data.entity.local.event

object MedicalEventEntityContract {
    const val TABLE_NAME = "medical_events"

    const val COLUMN_UUID = "uuid"
    const val COLUMN_TYPE = "type"
    const val COLUMN_IS_CHANGED_LOCALLY = "is_changed_locally"
    const val COLUMN_IS_DELETED = "is_deleted"
    const val COLUMN_TIMESTAMP = "timestamp"
    const val COLUMN_PATIENT_UUID = "patient_uuid"
    const val COLUMN_IS_ADDED_FROM_DOCTOR = "is_added_from_doctor"
    const val COLUMN_END_TIMESTAMP = "end_timestamp"
    const val COLUMN_DOCTOR_CREATOR_UUID = "doctor_creator_uuid"
    const val COLUMN_NAME = "name"
    const val COLUMN_CLINIC = "clinic"
    const val COLUMN_DOCTOR_NAME = "doctor_name"
    const val COLUMN_DOCTOR_SPECIALIZATION = "doctor_specialization"
    const val COLUMN_SYMPTOMS = "symptoms"
    const val COLUMN_DIAGNOSIS = "diagnosis"
    const val COLUMN_RECIPE = "recipe"
    const val COLUMN_COMMENT = "comment"

    const val CREATE_TABLE_QUERY = """
        CREATE TABLE $TABLE_NAME(
            $COLUMN_UUID TEXT NOT NULL PRIMARY KEY,
            $COLUMN_TIMESTAMP INTEGER NOT NULL,
            $COLUMN_TYPE INTEGER NOT NULL,
            $COLUMN_IS_CHANGED_LOCALLY INTEGER NOT NULL,
            $COLUMN_IS_DELETED INTEGER NOT NULL,
            $COLUMN_PATIENT_UUID TEXT NOT NULL,
            $COLUMN_IS_ADDED_FROM_DOCTOR INTEGER DEFAULT 0,
            $COLUMN_END_TIMESTAMP INTEGER,
            $COLUMN_DOCTOR_CREATOR_UUID TEXT,
            $COLUMN_NAME TEXT,
            $COLUMN_CLINIC TEXT,
            $COLUMN_DOCTOR_NAME TEXT,
            $COLUMN_DOCTOR_SPECIALIZATION TEXT,
            $COLUMN_SYMPTOMS TEXT,
            $COLUMN_DIAGNOSIS TEXT,
            $COLUMN_RECIPE TEXT,
            $COLUMN_COMMENT TEXT
    );"""

    internal val DELETE_TABLE_QUERY = "DROP TABLE IF EXISTS $TABLE_NAME"
}