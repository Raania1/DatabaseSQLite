package com.example.crud

import android.content.ContentValues
import android.os.Bundle
import android.provider.BaseColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.crud.ui.theme.CrudTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbHelper = FeedReaderDbHelper(this)

        enableEdgeToEdge()
        setContent {
            CrudTheme {
                CrudScreen(dbHelper)
            }
        }
    }
}

@Composable
fun CrudScreen(dbHelper: FeedReaderDbHelper) {
    var nom by remember { mutableStateOf("") }
    var cin by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var editingId by remember { mutableStateOf<Long?>(null) }
    var items by remember { mutableStateOf(readAllDB(dbHelper)) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { padding ->
            Column(
                modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = cin,
                    onValueChange = { cin = it },
                    label = { Text("CIN") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (isEditing) {
                            updateDB(dbHelper, editingId, nom, cin)
                            isEditing = false
                            editingId = null
                        } else {
                            insertDB(dbHelper, nom, cin)
                        }
                        nom = ""
                        cin = ""
                        items = readAllDB(dbHelper)
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(if (isEditing) "Enregistrer" else "Insérer")
                }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items.size) { index ->
                        val item = items[index]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        ) {
                            Text("${item["nom"]} (${item["cin"]})", modifier = Modifier.weight(1f))
                            Button(onClick = {
                                isEditing = true
                                editingId = item["id"] as Long
                                nom = item["nom"] as String
                                cin = item["cin"] as String
                            }) {
                                Text("Modifier")
                            }
                            Button(onClick = {
                                deleteDB(dbHelper, item["id"] as Long)
                                items = readAllDB(dbHelper)
                            }) {
                                Text("Supprimer")
                            }
                        }
                    }
                }
            }
        }
    )
}

fun insertDB(dbHelper: FeedReaderDbHelper, nom: String, cin: String) {
    val db = dbHelper.writableDatabase
    val values = ContentValues().apply {
        put(FeedReaderContract.FeedEntry.COLUMN_NAME_NOM, nom)
        put(FeedReaderContract.FeedEntry.COLUMN_NAME_CIN, cin)
    }
    db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values)
}

fun updateDB(dbHelper: FeedReaderDbHelper, id: Long?, nom: String, cin: String) {
    if (id == null) return
    val db = dbHelper.writableDatabase
    val values = ContentValues().apply {
        put(FeedReaderContract.FeedEntry.COLUMN_NAME_NOM, nom)
        put(FeedReaderContract.FeedEntry.COLUMN_NAME_CIN, cin)
    }
    db.update(
        FeedReaderContract.FeedEntry.TABLE_NAME,
        values,
        "${BaseColumns._ID} = ?",
        arrayOf(id.toString())
    )
}

fun deleteDB(dbHelper: FeedReaderDbHelper, id: Long) {
    val db = dbHelper.writableDatabase
    db.delete(
        FeedReaderContract.FeedEntry.TABLE_NAME,
        "${BaseColumns._ID} = ?",
        arrayOf(id.toString())
    )
}

fun readAllDB(dbHelper: FeedReaderDbHelper): List<Map<String, Any>> {
    val db = dbHelper.readableDatabase
    val cursor = db.query(
        FeedReaderContract.FeedEntry.TABLE_NAME,
        null,
        null,
        null,
        null,
        null,
        null
    )
    val items = mutableListOf<Map<String, Any>>()
    with(cursor) {
        while (moveToNext()) {
            val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
            val nom = getString(getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NOM))
            val cin = getString(getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_CIN))
            items.add(mapOf("id" to id, "nom" to nom, "cin" to cin))
        }
    }
    cursor.close()
    return items
}
