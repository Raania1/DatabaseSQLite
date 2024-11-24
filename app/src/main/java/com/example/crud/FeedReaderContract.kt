package com.example.crud

import android.provider.BaseColumns

object FeedReaderContract{
    object FeedEntry : BaseColumns{
        const val TABLE_NAME = "entry"
        const val COLUMN_NAME_NOM = "Nom"
        const val COLUMN_NAME_CIN = "Cin"
    }
    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${FeedEntry.TABLE_NAME}("+
                "${BaseColumns._ID} INTEGER PRIMARY KEY,"+
                "${FeedEntry.COLUMN_NAME_NOM} TEXT,"+
                "${FeedEntry.COLUMN_NAME_CIN} TEXT)"
    const val  SQL_DELETE_ENTRIES = "DROP TABELE IF EXIST ${FeedEntry.TABLE_NAME}"
}

