@file:Suppress("DEPRECATION")

package com.example.afinal

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*



@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    val API: String = "5a2a4925a72a96ba0cfe86ddf882c163"; // API-key
    var city = "молодечно" // Город по умолчанию

    var data: AdditionalData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.Button)
        val additionalButton = findViewById<Button>(R.id.additional)
        val editField = findViewById<EditText>(R.id.editCity)

        weatherTask().execute()  // Запрос по API

        editField.setOnFocusChangeListener() { v, hasFocus ->
            if (!hasFocus) hideKeyboard()
        }

        searchButton.setOnClickListener {
            city = editField.text.toString().trim().toLowerCase()
            editField.setText("");
            weatherTask().execute() // Запрос по API
        }

        additionalButton.setOnClickListener {
            val dialog = CustomDialog(data)
            dialog?.show(supportFragmentManager, "Подробные данные")
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }

    inner class weatherTask(): AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.main).setBackgroundResource(R.drawable.gradient_bg)
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response: String? = try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$API&lang=ru")
                    .build()
                val res = client.newCall(request).execute()
                res.body?.string()
            } catch (e: Exception) {
                null
            }
            return response
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            try {
                val jsonObj = JSONObject(result)
                val wind = jsonObj.getJSONObject("wind")
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val temp = main.getString("temp").toDouble().toInt().toString() + "°C"
                val seems = "Ощущается как " + main.getString("feels_like").toDouble().toInt().toString() + "°C"
                val weatherDescription = weather.getString("description").capitalize()
                val address = jsonObj.getString("name") + ", " + sys.getString("country")

                setBackground(weatherDescription)

                val humidity = main.getString("humidity") + "%"
                val pressure = main.getString("pressure") + " гПа"
                val windSpeed = wind.getString("speed") + " м/c"
                val cords = jsonObj.getJSONObject("coord")
                val formattedCords = cords.getString("lat") + "°, " + cords.getString("lon")  + "°"
                val sunrise = convertUnixTimestampToDateTime(sys.getString("sunrise").toLong()).split(" ")[1]
                val sunset = convertUnixTimestampToDateTime(sys.getString("sunset").toLong()).split(" ")[1]

                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.status).text = weatherDescription
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.seems).text = seems

                data = AdditionalData(address, formattedCords, temp, seems, weatherDescription,
                    humidity, pressure, windSpeed, sunrise.toString(), sunset.toString())

                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
            } catch (e: Exception) {
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }
        }

        private fun setBackground(weatherDescription: String?) {
            val layout = findViewById<RelativeLayout>(R.id.main)

            when (weatherDescription) {
                "Ясно", "Ясное небо" -> {
                    layout.setBackgroundResource(R.drawable.wallpaper_sun)
                }
                "Малооблачно", "Небольшая облачность", "Переменная облачность", "Облачно с прояснениями" -> {
                    layout.setBackgroundResource(R.drawable.wallpaper_small_cloudly)
                }
                "Облачно", "Пасмурно" -> {
                    layout.setBackgroundResource(R.drawable.wallpaper_cloudly)
                }
                "Дождь", "Ливень", "Проливной дождь", "Легкий дождь",
                "Небольшой дождь", "Морось", "Умеренный дождь", "Сильный дождь",
                "Очень сильный дождь", "Экстремальный дождь", "Дождь со льдом",
                "Ливневый дождь", "Небольшой проливной дождь" -> {
                    layout.setBackgroundResource(R.drawable.wallpaper_rain)
                }
                "Снег", "Небольшой снег", "Сильный снег", "Метель", "Ледяной дождь",
                "Мокрый снег", "Умеренный снег", "Дождь со снегом" -> {
                    layout.setBackgroundResource(R.drawable.wallpaper_snow)
                }
                "Туман", "Плотный туман", "Мгла", "Сильная мгла", "Туман с мглой", "Смог",
                "Дым" -> {
                    layout.setBackgroundResource(R.drawable.wallpaper_fog)
                }
                "Гроза", "Сильная гроза", "Гроза с градом", "Гроза с ливнем",
                "Гроза с небольшим дождем", "Гроза с дождем", "Гроза с сильным дождем",
                "Небольшая гроза", "Умеренная гроза", "Гроза с порывистым ветром",
                "Гроза с небольшим дождем и моросью", "Гроза с дождем и моросью",
                "Гроза с сильным дождем и моросью" -> {
                    layout.setBackgroundResource(R.drawable.wallpaper_thunder)
                }
                "Пыль", "Песок", "Песчаная буря", "Вулканический пепел", "Песчаный ветер" -> {
                    layout.setBackgroundResource(R.drawable.wallpaper_dust)
                }
                "Шторм", "Сильный шторм", "Ураган", "Смерч", "Торнадо", "Шквалы" -> {
                    layout.setBackgroundResource(R.drawable.wallpaper_storm)
                }
                else -> {
                    layout.setBackgroundResource(R.drawable.gradient_bg)
                }
            }


        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertUnixTimestampToDateTime(unixTimestamp: Long): String {
        val timeZoneId = "Europe/Minsk"
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val zoneId = ZoneId.of(timeZoneId)
        val dateTime = Instant.ofEpochSecond(unixTimestamp).atZone(zoneId).format(dateTimeFormatter)
        return dateTime
    }
}