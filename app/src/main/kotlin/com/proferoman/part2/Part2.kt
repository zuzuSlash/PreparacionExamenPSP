/**
 * La función main2 que ves en este fichero recibe un texto y tiene que crear un proceso que
 * cuente las vocales (sin acentos) que hay en dicho texto y le comunique dicho resultado a
 * través de un fichero que contendrá el número de vocales, sin más. Este fichero estará
 * ubicado en la ruta ./files/part2 y el nombre será dado por la función main2, es decir, por
 * el proceso padre.
 *
 * Así, el proceso padre tiene que pasar al hijo: el texto y el nombre del fichero donde
 * guardará el resultado.
 *
 * El padre tendrá que esperar que termine el proceso hijo para leer del fichero el resultado
 * y mostrar por pantalla un mensaje como este:
 *
 * "Hay un total de 13 vocales en el texto"
 */
package com.proferoman.part2

import java.io.File
import java.util.*

class VocalCount {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.isEmpty()) {
                println("Se necesita una frase")
                return
            }

            val text = args[0]
            val filePath = args[1]
            vocalCount(text, filePath)
        }
    }
}

fun vocalCount(text: String, fileName: String) {
    var countVocal = 0
    val vocales = setOf('a', 'e', 'i', 'o', 'u')

    val lowerText = text.lowercase(Locale.getDefault())
        .replace("á", "a")
        .replace("é", "e")
        .replace("í", "i")
        .replace("ó", "o")
        .replace("ú", "u")

    for (char in lowerText) {
        if (char in vocales) {
            countVocal++
        }
    }

    val file = File(fileName)
    file.writeText(countVocal.toString())
}

fun main2(text: String) {
    println("Ejecutando main2 que es el proceso padre")

    // Definimos el directorio y el archivo para el resultado
    val pathDir = "./files/part2"
    val resultFile = "$pathDir/vocalCountResult.txt"

    // Iniciamos el proceso hijo directamente aquí
    val className = "com.proferoman.part2.VocalCount"
    val classPath = System.getProperty("java.class.path")

    val processBuilder = ProcessBuilder(
        "java", "-cp", classPath, className,
        text, resultFile
    )

    println("Iniciando el proceso hijo...")

    val vocalProcess = processBuilder.start()
    vocalProcess.waitFor() // Espera que el proceso hijo termine

    // Una vez que el proceso hijo termina, leemos el archivo generado
    val resultFileRead = File(resultFile)
    val countVocal = resultFileRead.readText().toInt()

    println("Hay un total de ($countVocal) vocales en el texto")
}
