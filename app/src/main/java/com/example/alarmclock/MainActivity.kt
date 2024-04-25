package com.example.alarmclock

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmclock.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var alarmManager: AlarmManager

    private lateinit var NovyBudikButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private lateinit var dbHelper: MyDBHelper

    // Požadavek na povolení překrývání obrazovky
    private val REQUEST_CODE_OVERLAY_PERMISSION = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializace AlarmManageru
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Nastavení tlačítka pro vytvoření nového budíku
        NovyBudikButton = findViewById(R.id.novy_budik_button)
        recyclerView = findViewById(R.id.seznam_budiku)

        NovyBudikButton.setOnClickListener {
            // Přechod na aktivitu pro vytvoření nového budíku
            val intent = Intent(this, Vytvorenibudiku::class.java)
            startActivity(intent)
        }

        // Nastavení recycler view a adapteru pro zobrazení seznamu budíků
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(this, mutableListOf())
        recyclerView.adapter = adapter

        // Inicializace databázového pomocníka a načtení dat z databáze pro zobrazení
        dbHelper = MyDBHelper(this)
        val data = readDataFromDatabase()
        adapter.setData(data)

        // Pokud verze Androidu je 6.0 nebo vyšší a uživatel nepovolil překrývání obrazovky, zobrazí se dialog pro povolení
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName))
            startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION)
        } else {
            // Jinak nastaví alarmy podle dat z databáze
            setupAlarmsFromDatabase()
        }
    }

    // Metoda pro zpracování výsledku požadavku na povolení překrývání obrazovky
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                setupAlarmsFromDatabase()
            } else {
                Toast.makeText(this, "You need to grant overlay permission to use this feature", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Metoda pro nastavení alarmů podle dat z databáze
    private fun setupAlarmsFromDatabase() {
        val dataList = readDataFromDatabase()
        for (budik in dataList) {
            // Vytvoření intentu pro AlarmReceiver a předání dat budíku
            val intent = Intent(this, AlarmReceiver::class.java)
            intent.putExtra("time", budik.time)
            intent.putExtra("monday", budik.monday)
            intent.putExtra("tuesday", budik.tuesday)
            intent.putExtra("wednesday", budik.wednesday)
            intent.putExtra("thursday", budik.thursday)
            intent.putExtra("friday", budik.friday)
            intent.putExtra("saturday", budik.saturday)
            intent.putExtra("sunday", budik.sunday)
            intent.putExtra("gentle", budik.gentle)

            // Vytvoření PendingIntent pro přenos intentu
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                budik.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Nastavení kalendáře pro spuštění alarmu
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                val timeParts = budik.time.split(":")
                val hour = timeParts[0].toInt()
                val minute = timeParts[1].toInt()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                val daysOfWeek = listOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY)
                val currentDayIndex = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
                for (i in currentDayIndex until currentDayIndex + daysOfWeek.size) {
                    val index = i % daysOfWeek.size
                    set(Calendar.DAY_OF_WEEK, daysOfWeek[index])
                    break
                }
                if (this.timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.WEEK_OF_YEAR, 1)
                }
            }

            // Pokud je budík nastavený jako gentle, upraví čas spuštění o 1 minutu dříve
            if (budik.gentle) {
                calendar.add(Calendar.MINUTE, -1)
            }

            // Nastavení alarmu
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    // Metoda pro čtení dat z databáze
    @SuppressLint("Range")
    private fun readDataFromDatabase(): List<MyDataModel> {
        val dataList = mutableListOf<MyDataModel>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM MyTable", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val time = cursor.getString(cursor.getColumnIndex("time"))
            val monday = cursor.getInt(cursor.getColumnIndex("monday")) == 1
            val tuesday = cursor.getInt(cursor.getColumnIndex("tuesday")) == 1
            val wednesday = cursor.getInt(cursor.getColumnIndex("wednesday")) == 1
            val thursday = cursor.getInt(cursor.getColumnIndex("thursday")) == 1
            val friday = cursor.getInt(cursor.getColumnIndex("friday")) == 1
            val saturday = cursor.getInt(cursor.getColumnIndex("saturday")) == 1
            val sunday = cursor.getInt(cursor.getColumnIndex("sunday")) == 1
            val gentle = cursor.getInt(cursor.getColumnIndex("gentle")) == 1
            dataList.add(
                MyDataModel(
                    id,
                    time,
                    monday,
                    tuesday,
                    wednesday,
                    thursday,
                    friday,
                    saturday,
                    sunday,
                    gentle
                )
            )
        }
        cursor.close()
        return dataList
    }
}
