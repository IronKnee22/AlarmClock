package com.example.alarmclock

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Handler
import android.util.Log
import android.widget.Button

class VypnutiBudiku : AppCompatActivity() {
    lateinit var button_vypnout: Button
    lateinit var button_odlozit: Button
    lateinit var AlarmPlayer: MediaPlayer
    lateinit var birdPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vyp_budik)

        // Získání informace o tom, zda je budík nastaven na postupné probouzení
        val gentle = intent.getBooleanExtra("gentle", false)


        // Inicializace přehrávačů zvuků
        birdPlayer = MediaPlayer.create(this, R.raw.birds_gentle)
        AlarmPlayer = MediaPlayer.create(this, R.raw.zvuk_alarmu)

        // Nastavení úrovně hlasitosti pro postupné probouzení
        val maxVolume = 1.0f
        val minVolume = 0.1f

        // Nastavení trvání a intervalu zeslabování zvuku pro postupné probouzení
        val fadeDuration = 10000
        val totalFadeTime = 60000

        var elapsedTime = 0

        if (gentle) {
            // Pokud je budík nastaven na postupné probouzení

            birdPlayer.isLooping = true
            birdPlayer.setVolume(0.2f, 0.2f)
            birdPlayer.start()

            // Postupné zeslabování zvuku ptáků
            val handler = Handler()
            val runnable = object : Runnable {
                var currentVolume = minVolume

                override fun run() {
                    if (currentVolume < maxVolume && elapsedTime < totalFadeTime) {
                        currentVolume += 0.2f
                        birdPlayer.setVolume(currentVolume, currentVolume)
                        handler.postDelayed(this, fadeDuration.toLong())
                        elapsedTime += fadeDuration

                        if (elapsedTime >= 50000) {
                            // Po uplynutí 50 sekund se zastaví zvuk ptáků a spustí se alarm
                            Log.e("zkouska", "hned nebo až po minutě")
                            birdPlayer.stop()
                            AlarmPlayer.isLooping = true
                            AlarmPlayer.start()
                        }
                    }
                }
            }
            handler.postDelayed(runnable, fadeDuration.toLong())

        } else {
            // Pokud není budík nastaven na postupné probouzení

            AlarmPlayer.isLooping = true
            AlarmPlayer.start()
        }

        // Nastavení posluchačů na tlačítka
        button_vypnout = findViewById(R.id.button_vypnout)
        button_vypnout.setOnClickListener {
            // Zastavení alarmu a případně zvuku ptáků a ukončení aktivity
            zastavitAlarm()
            if (gentle) {
                birdPlayer.stop()
            } else {
                AlarmPlayer.stop()
            }
            finish()
        }

        button_odlozit = findViewById(R.id.button_odlozit)
        button_odlozit.setOnClickListener {
            // Nastavení odložení alarmu o 1 minutu a ukončení aktivity
            val intent = Intent(this, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.MINUTE, 1)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            finish()
        }
    }

    // Metoda pro zastavení probíhajícího alarmu
    private fun zastavitAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.e("com.example.alarmclock.VypnutiBudiku", "Alarm byl vypnut.")
    }

    // Uvolnění prostředků přehrávačů při ukončení aktivity
    override fun onDestroy() {
        super.onDestroy()
        birdPlayer.release()
        AlarmPlayer.release()
    }
}
