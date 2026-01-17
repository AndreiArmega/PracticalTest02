package ro.pub.cs.systems.eim.practicaltest02

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

// Modelul de date (păstrat aici dacă vrei un singur fișier, dar asigură-te că nu e duplicat în proiect)
data class WeatherForecastModel(
    val temperature: String,
    val windSpeed: String,
    val condition: String,
    val pressure: String,
    val humidity: String
) {
    override fun toString(): String {
        return "Temp: $temperature, Wind: $windSpeed, Cond: $condition, Pres: $pressure, Hum: $humidity"
    }
}

class MainActivity : AppCompatActivity() {

    // 1. Declarații variabile pentru UI
    private lateinit var serverPortEditText: EditText
    private lateinit var connectButton: Button
    private lateinit var clientAddressEditText: EditText
    private lateinit var clientPortEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var informationTypeSpinner: Spinner
    private lateinit var getWeatherButton: Button
    private lateinit var resultTextView: TextView

    private var serverThread: ServerThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ATENȚIE: Numele layout-ului trebuie să fie cel din folderul res/layout (ex: activity_main)
        setContentView(R.layout.activity_practical_test02_main)

        // 2. Inițializare obiecte din interfață
        serverPortEditText = findViewById(R.id.server_port_edit_text)
        connectButton = findViewById(R.id.connect_button)
        clientAddressEditText = findViewById(R.id.client_address_edit_text)
        clientPortEditText = findViewById(R.id.client_port_edit_text)
        cityEditText = findViewById(R.id.city_edit_text)
        informationTypeSpinner = findViewById(R.id.information_type_spinner)
        getWeatherButton = findViewById(R.id.get_weather_forecast_button)
        resultTextView = findViewById(R.id.weather_forecast_text_view)

        // 3. Logica Buton Connect (Server)
        connectButton.setOnClickListener {
            val serverPort = serverPortEditText.text.toString()
            if (serverPort.isEmpty()) {
                Toast.makeText(applicationContext, "Server port should be filled!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            serverThread = ServerThread(serverPort.toInt())
            if (serverThread?.serverSocket == null) {
                Log.e("PracticalTest02", "Could not create server thread!")
                return@setOnClickListener
            }
            serverThread?.start()
            Toast.makeText(this, "Server started on port $serverPort", Toast.LENGTH_SHORT).show()
        }

        // 4. Logica Buton Get Weather (Client)
        getWeatherButton.setOnClickListener {
            val address = clientAddressEditText.text.toString()
            val port = clientPortEditText.text.toString()
            val city = cityEditText.text.toString()
            val infoType = informationTypeSpinner.selectedItem.toString()

            if (address.isEmpty() || port.isEmpty() || city.isEmpty()) {
                Toast.makeText(this, "All client fields must be filled!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lansăm thread-ul de client
            val clientThread = ClientThread(address, port.toInt(), city, infoType, resultTextView)
            clientThread.start()
        }
    }

    // 5. Oprire server la închiderea aplicației
    override fun onDestroy() {
        serverThread?.stopThread()
        super.onDestroy()
    }
}