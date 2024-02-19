package com.example.printtest.PrintFiles

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.core.app.ActivityCompat
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.S)
fun printBut(
    context: Context,
    impresora : String
) {
    print("Entre \n\n\n\n\n\n\n\n\n")
    val name = "Gregory Suarez"

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH
        ) != PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_ADMIN
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Salir de la función si los permisos no están concedidos
        return
    }

    // Aquí va el código para imprimir, adaptado según lo mencionado anteriormente
    // Obtén la instancia del BluetoothAdapter
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    // Asegúrate de que el Bluetooth esté activado y disponible en tu dispositivo
    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
        print("falle")
        // Manejar caso cuando el Bluetooth no está disponible o no está activado
        return
    }

    // Busca y empareja la impresora Bluetooth
    val dispositivosEmparejados: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
    val impresora: BluetoothDevice? = dispositivosEmparejados?.firstOrNull { device ->
        device.name?.startsWith( impresora ?: "MTP-2", ignoreCase = true) == true
    }

    // Verifica si se encontró la impresora
    if (impresora == null) {
        // Manejar caso cuando la impresora no se encontró
        print("No la encontre \n\n\n\n")
        return
    }

    // Establece la conexión Bluetooth con la impresora
    val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // UUID para el perfil SPP (Serial Port Profile)
    val socket: BluetoothSocket = impresora.createRfcommSocketToServiceRecord(uuid)

    // Conecta el socket
    socket.connect()

    // Obtén el OutputStream del socket Bluetooth
    val outputStream: OutputStream = socket.outputStream
    print("Generando el contenido ")
    // Genera el contenido de la factura
    val contenidoFactura = generarContenido(name) // Asegúrate de definir y rellenar la función generarContenidoFactura() con tu lógica de generación de facturas

    // Envía los datos a la impresora Bluetooth
    enviarDatosImpresoraBluetooth(contenidoFactura, outputStream)

    // Cierra el OutputStream y el socket Bluetooth
    outputStream.close()
    socket.close()
}

@SuppressLint("SimpleDateFormat")
fun generarContenido(
    name : String
): String {

    val builder = StringBuilder()
    builder.appendLine("================================\n")
    builder.appendLine("  ----------------------------  ")
    builder.appendLine("   R E C I B O   DE   P A G O  ")
    builder.appendLine("--------------------------------\n")
    appendCenteredLine(builder, name, 32)
    builder.appendLine("________________________________")
    builder.appendLine("         Firma Recibido ")
    builder.appendLine("\n\n")
    return builder.toString()
}

fun enviarDatosImpresoraBluetooth(datos: String, outputStream: OutputStream) {
    val bytes = datos.toByteArray()
    outputStream.write(bytes)
    print("Se envio \n\n\n\n\n")
    outputStream.flush()

}

fun splitString(input: String): List<String> {
    return input.split("\r\n")
}

fun appendCenteredLine(builder: StringBuilder, line: String, totalWidth: Int) {
    val lineLength = line.length
    val padding = (totalWidth - lineLength) / 2
    val centeredLine = line.padStart(padding + lineLength).padEnd(totalWidth)
    builder.appendLine(centeredLine)
}
fun completarString(str: String, length: Int): String {
    return if (str.length < length) {
        str.padEnd(length)
    } else {
        str.substring(0, length)
    }
}
