package ro.pub.cs.systems.eim.practicaltest02

import android.util.Log
import android.widget.TextView
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class ClientThread(
    private val address: String,
    private val port: Int,
    private val city: String,
    private val informationType: String,
    private val resultTextView: TextView
) : Thread() {

    override fun run() {
        try {
            // 1. Establish connection to the server
            val socket = Socket(address, port)

            // 2. Setup communication streams
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val writer = PrintWriter(socket.getOutputStream(), true)

            // 3. Send the request (each parameter on a new line)
            writer.println(city)
            writer.println(informationType)

            // 4. Read the response from the server
            val fullResponse = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                fullResponse.append(line).append("\n")
            }

            // 5. Update the UI from the background thread
            resultTextView.post {
                resultTextView.text = fullResponse.toString()
            }

            // 6. Close the connection
            socket.close()

        } catch (e: Exception) {
            Log.e("PracticalTest02", "[CLIENT THREAD] An error occurred: ${e.message}")
        }
    }
}