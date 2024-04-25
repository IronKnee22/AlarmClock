package com.example.alarmclock

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.Switch
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity

class UpravaBudiku : AppCompatActivity() {

    lateinit var CancelButton: Button
    lateinit var SaveButton: Button
    lateinit var timePicker: TimePicker
    lateinit var smazatButton: Button

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.upravit_budiku)

        val budikId = intent.getIntExtra("budik_id", -1)

        // Inicializace prvků layoutu
        timePicker = findViewById(R.id.timePicker)
        timePicker.setIs24HourView(true)

        CancelButton = findViewById(R.id.cancel_button)
        // Nastavení posluchače na tlačítko pro zrušení úpravy budíku
        CancelButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        smazatButton = findViewById(R.id.button_smazat)
        // Nastavení posluchače na tlačítko pro smazání budíku
        smazatButton.setOnClickListener {
            if (budikId != -1) {
                deleteAlarm(budikId)
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        SaveButton = findViewById(R.id.save_button)
        // Nastavení posluchače na tlačítko pro uložení úpravy budíku
        SaveButton.setOnClickListener {
            if (budikId != -1) {
                updateAlarm(budikId)
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Načtení dat budíku pro úpravu
        if (budikId != -1) {
            val db = MyDBHelper(this).writableDatabase
            val cursor = db.query("MyTable", null, "id = ?", arrayOf(budikId.toString()), null, null, null)
            if (cursor.moveToFirst()) {
                val time = cursor.getString(cursor.getColumnIndex("time"))
                val monday = cursor.getInt(cursor.getColumnIndex("monday")) == 1
                val tuesday = cursor.getInt(cursor.getColumnIndex("tuesday")) == 1
                val wednesday = cursor.getInt(cursor.getColumnIndex("wednesday")) == 1
                val thursday = cursor.getInt(cursor.getColumnIndex("thursday")) == 1
                val friday = cursor.getInt(cursor.getColumnIndex("friday")) == 1
                val saturday = cursor.getInt(cursor.getColumnIndex("saturday")) == 1
                val sunday = cursor.getInt(cursor.getColumnIndex("sunday")) == 1
                val gentle = cursor.getInt(cursor.getColumnIndex("gentle")) == 1

                // Nastavení hodnot do prvků layoutu podle načtených dat z databáze
                timePicker.hour = time.split(":")[0].toInt()
                timePicker.minute = time.split(":")[1].toInt()
                findViewById<CheckBox>(R.id.checkBox1).isChecked = monday
                findViewById<CheckBox>(R.id.checkBox2).isChecked = tuesday
                findViewById<CheckBox>(R.id.checkBox3).isChecked = wednesday
                findViewById<CheckBox>(R.id.checkBox4).isChecked = thursday
                findViewById<CheckBox>(R.id.checkBox5).isChecked = friday
                findViewById<CheckBox>(R.id.checkBox6).isChecked = saturday
                findViewById<CheckBox>(R.id.checkBox7).isChecked = sunday
                findViewById<Switch>(R.id.switch1).isChecked = gentle

                Log.d("TIME", "Time: $time")
                Log.d("MONDAY", "Monday: $monday")
            }
            cursor.close()
            Log.e("není problém", "načetlo se to")
        } else {
            Log.e("Prblém", "nenačetlo se to")
        }
    }

    // Metoda pro aktualizaci údajů budíku v databázi
    private fun updateAlarm(alarmId: Int) {
        val time = "${timePicker.hour}:${timePicker.minute}"
        val monday = findViewById<CheckBox>(R.id.checkBox1).isChecked
        val tuesday = findViewById<CheckBox>(R.id.checkBox2).isChecked
        val wednesday = findViewById<CheckBox>(R.id.checkBox3).isChecked
        val thursday = findViewById<CheckBox>(R.id.checkBox4).isChecked
        val friday = findViewById<CheckBox>(R.id.checkBox5).isChecked
        val saturday = findViewById<CheckBox>(R.id.checkBox6).isChecked
        val sunday = findViewById<CheckBox>(R.id.checkBox7).isChecked
        val gentle = findViewById<Switch>(R.id.switch1).isChecked

        val dbHelper = MyDBHelper(this)
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("time", time)
            put("monday", monday)
            put("tuesday", tuesday)
            put("wednesday", wednesday)
            put("thursday", thursday)
            put("friday", friday)
            put("saturday", saturday)
            put("sunday", sunday)
            put("gentle", gentle)
        }

        val selection = "id = ?"
        val selectionArgs = arrayOf(alarmId.toString())

        val count = db.update("MyTable", values, selection, selectionArgs)

        if (count > 0) {
            Log.d("Update", "Alarm with ID $alarmId updated successfully.")
        } else {
            Log.d("Update", "Failed to update alarm with ID $alarmId.")
        }

        db.close()
    }

    // Metoda pro smazání budíku z databáze
    private fun deleteAlarm(alarmId: Int) {
        val dbHelper = MyDBHelper(this)
        val db = dbHelper.writableDatabase

        val selection = "id = ?"
        val selectionArgs = arrayOf(alarmId.toString())

        val count = db.delete("MyTable", selection, selectionArgs)

        if (count > 0) {
            Log.d("Delete", "Alarm with ID $alarmId deleted successfully.")
        } else {
            Log.d("Delete", "Failed to delete alarm with ID $alarmId.")
        }

        db.close()
    }
}
