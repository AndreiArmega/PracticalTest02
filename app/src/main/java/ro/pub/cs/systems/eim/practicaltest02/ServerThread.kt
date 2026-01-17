package ro.pub.cs.systems.eim.practicaltest02

import android.util.Log
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.HashMap

class ServerThread(private val port: Int) : Thread() {
    var serverSocket: ServerSocket? = null
    private val data = HashMap<String, WeatherForecastModel>()

    init {
        try {
            this.serverSocket = ServerSocket(port)
        } catch (e: IOException) {
            Log.e("PracticalTest02", "Eroare la pornirea serverului: ${e.message}")
        }
    }

    override fun run() {
        try {
            while (!Thread.currentThread().isInterrupted) {
                Log.i("PracticalTest02", "[SERVER] Astept conexiune client...")

                val socket: Socket = serverSocket?.accept() ?: break
                Log.i("PracticalTest02", "[SERVER] Conexiune primita de la: ${socket.inetAddress}")

                val communicationThread = CommunicationThread(this, socket)
                communicationThread.start()
            }
        } catch (e: IOException) {
            Log.e("PracticalTest02", "Eroare in bucla de accept: ${e.message}")
        }
    }

    @Synchronized
    fun setData(city: String, weatherInfo: WeatherForecastModel) {
        this.data[city] = weatherInfo
    }

    @Synchronized
    fun getData(): HashMap<String, WeatherForecastModel> {
        return data
    }

    fun stopThread() {
        interrupt()
        try {
            serverSocket?.close()
        } catch (e: IOException) {
            Log.e("PracticalTest02", "Eroare la inchiderea serverului: ${e.message}")
        }
    }
}

