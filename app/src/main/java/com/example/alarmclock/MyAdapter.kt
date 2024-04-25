package com.example.alarmclock

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val context: Context, private val dataList: MutableList<MyDataModel>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // ViewHolder pro položky v RecyclerView
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTime: TextView = itemView.findViewById(R.id.textTime)
        val textDny: TextView = itemView.findViewById(R.id.textDny)
        val textGentle: TextView = itemView.findViewById(R.id.textGentle)
        val buttonUpravit: Button = itemView.findViewById(R.id.buttonUpravit)
    }

    // Vytvoření nového ViewHolderu
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.recycle_view, parent, false)
        return MyViewHolder(itemView)
    }

    // Nastavení dat do ViewHolderu
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = dataList[position]

        // Získání zkráceného názvu dnů podle nastavení v databázi
        val daysAbbreviation = if (currentItem.monday && currentItem.tuesday && currentItem.wednesday &&
            currentItem.thursday && currentItem.friday && currentItem.saturday &&
            currentItem.sunday) {
            "Každý den"
        } else {
            getDaysAbbreviation(currentItem.monday, currentItem.tuesday, currentItem.wednesday,
                currentItem.thursday, currentItem.friday, currentItem.saturday,
                currentItem.sunday)}

        // Nastavení textů do jednotlivých prvků ViewHolderu
        holder.textTime.text = "Time: ${formatTime(currentItem.time)}"
        holder.textDny.text = "Dny: $daysAbbreviation"
        holder.textGentle.text = "Gentle: ${currentItem.gentle}"
        holder.buttonUpravit.text = "Upravit"
        // Nastavení posluchače na tlačítko pro upravení budíku
        holder.buttonUpravit.setOnClickListener {
            val intent = Intent(context, UpravaBudiku::class.java)
            intent.putExtra("budik_id", currentItem.id)
            Log.e("id", "${currentItem.id}")
            context.startActivity(intent)
        }
    }

    // Metoda pro správný formát času
    private fun formatTime(time: String): String {
        val timeParts = time.split(":")
        val hour = timeParts[0].toInt().toString().padStart(2, '0')
        val minute = timeParts[1].toInt().toString().padStart(2, '0')
        return "$hour:$minute"
    }

    // Metoda pro získání zkráceného názvu dnů
    private fun getDaysAbbreviation(monday: Boolean, tuesday: Boolean, wednesday: Boolean,
                                    thursday: Boolean, friday: Boolean, saturday: Boolean,
                                    sunday: Boolean): String {
        val daysStringBuilder = StringBuilder()

        if (monday) daysStringBuilder.append("Po ")
        if (tuesday) daysStringBuilder.append("Út ")
        if (wednesday) daysStringBuilder.append("St ")
        if (thursday) daysStringBuilder.append("Čt ")
        if (friday) daysStringBuilder.append("Pá ")
        if (saturday) daysStringBuilder.append("So ")
        if (sunday) daysStringBuilder.append("Ne ")

        return daysStringBuilder.toString()
    }

    // Vrátí počet položek v seznamu
    override fun getItemCount(): Int {
        return dataList.size
    }

    // Metoda pro nastavení dat do adapteru
    fun setData(data: List<MyDataModel>) {
        dataList.clear()
        dataList.addAll(data)
        notifyDataSetChanged()
    }
}
