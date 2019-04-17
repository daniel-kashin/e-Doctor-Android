package com.edoctor.data.entity.local.parameter

import com.edoctor.data.entity.local.message.MessageEntityContract

object BodyParameterEntityContract {
    const val TABLE_NAME = "body_parameters"

    const val COLUMN_UUID = "uuid"
    const val COLUMN_TYPE = "type"
    const val COLUMN_MEASUREMENT_TIMESTAMP = "measurement_timestamp"
    const val COLUMN_PATIENT_UUID = "patient_uuid"
    const val COLUMN_FIRST_VALUE = "first_value"
    const val COLUMN_SECOND_VALUE = "second_value"
    const val COLUMN_CUSTOM_MODEL_NAME = "custom_model_name"
    const val COLUMN_CUSTOM_MODEL_UNIT = "custom_model_unit"

    const val CREATE_TABLE_QUERY = """
        CREATE TABLE ${MessageEntityContract.TABLE_NAME}(
            $COLUMN_UUID TEXT NOT NULL PRIMARY KEY,
            $COLUMN_TYPE INTEGER NOT NULL,
            $COLUMN_MEASUREMENT_TIMESTAMP INTEGER NOT NULL,
            $COLUMN_PATIENT_UUID TEXT NOT NULL,
            $COLUMN_FIRST_VALUE REAL NOT NULL,
            $COLUMN_SECOND_VALUE REAL,
            $COLUMN_CUSTOM_MODEL_NAME TEXT,
            $COLUMN_CUSTOM_MODEL_UNIT TEXT
    );"""

    internal val DELETE_TABLE_QUERY = "DROP TABLE IF EXISTS ${MessageEntityContract.TABLE_NAME}"

}