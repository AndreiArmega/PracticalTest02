package ro.pub.cs.systems.eim.practicaltest02

import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.URL

class CommunicationThread(
    private val serverThread: ServerThread,
    private val socket: Socket
) : Thread() {

    override fun run() {
        try {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val writer = PrintWriter(socket.getOutputStream(), true)

            val city = reader.readLine()
            val type = reader.readLine()

            if (city == null || type == null) {
                socket.close()
                return
            }

            // 1. Verificăm cache-ul
            var weatherModel = serverThread.getData()[city]

            if (weatherModel == null) {
                Log.i("PracticalTest02", "[COMM] Fetching from API for $city")

                // 2. HTTP Call
                val urlString = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=e03c3b32cfb5a6f7069f2ef29237d87e&units=metric"
                val response = URL(urlString).readText()

                // 3. Parsare JSON
                val json = JSONObject(response)
                val main = json.getJSONObject("main")
                val wind = json.getJSONObject("wind")
                val condition = json.getJSONArray("weather").getJSONObject(0).getString("main")

                weatherModel = WeatherForecastModel(
                    temperature = main.getString("temp"),
                    windSpeed = wind.getString("speed"),
                    condition = condition,
                    pressure = main.getString("pressure"),
                    humidity = main.getString("humidity")
                )

                // 4. Salvare în cache
                serverThread.setData(city, weatherModel)
            }

            // 5. FILTRAREA: Aceasta este partea care rezolvă Spinner-ul
            val result = when (type.lowercase()) {
                "all" -> weatherModel.toString()
                "temperature" -> weatherModel.temperature
                "wind_speed" -> weatherModel.windSpeed
                "condition" -> weatherModel.condition
                "pressure" -> weatherModel.pressure
                "humidity" -> weatherModel.humidity
                else -> "Unknown Type: $type"
            }

            // 6. Trimitem răspunsul și închidem
            writer.println(result)
            socket.close()

        } catch (e: Exception) {
            Log.e("CommThread", "Eroare: ${e.message}")
            socket.close()
        }
    }
}