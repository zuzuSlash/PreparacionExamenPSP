/**
 * Modifica la parte 2 para que la función principal, en este caso main3, divida la tarea de
 * contar el número de vocales en diferentes procesos. Cada proceso se encargará de contar las
 * vocales que hay en un rango.
 *
 * El proceso padre dividirá el texto en trozos del tamaño indicado por el argumento chunkSize.
 *
 * El proceso padre tendrá que indicar al hijo: el texto sobre el que contar y el nombre del
 * fichero donde dejará su resultado. Por ejemplo, si el texto fuera:
 *
 * "Este es un texto tal cual, sin nada más que una serie de palabras."
 *
 * Y el chunkSize es igual a 10, entonces repartirá la tarea tal que así:
 *
 * - Primer proceso: "Este es un"
 * - Segundo proceso: " texto tal"
 * - Tercer proceso: " cual, sin"
 * - Cuarto proceso: " nada más "
 * - Quinto proceso: "que una se"
 * - Sexto proceso: "rie de pal"
 * - Séptimo proceso: "abras."
 *
 * Cada proceso escribirá el resultado en un fichero de texto, dentro de la carpeta ./files/part3
 * que ya está creada. Así, siguiendo con el ejemplo anterior:
 *
 * - Primer proceso: escribirá el resultado en ./files/part3/file1.txt
 * - Segundo proceso: escribirá el resultado en ./files/part3/file2.txt
 * - Tercer proceso: escribirá el resultado en ./files/part3/file3.txt
 * - Cuarto proceso: escribirá el resultado en ./files/part3/file4.txt
 * - Quinto proceso: escribirá el resultado en ./files/part3/file5.txt
 * - Sexto proceso: escribirá el resultado en ./files/part3/file6.txt
 * - Séptimo proceso: escribirá el resultado en ./files/part3/file7.txt
 *
 * Consideraciones a tener en cuenta:
 *
 * - Tiene que ser el proceso padre el que indique al hijo el nombre del fichero donde tiene que
 *   guardar el resulto.
 * - El proceso padre tiene que esperar a que todos los hijos terminen para poder leer de todos
 *   los ficheros y sumar los resultados para tener la cuenta total de vocales.
 * - El programa finalmente tendrá que mostrar cuántas vocales hay en el texto.
 * - Te recomiendo que imprimas, además, la información de cada proceso que te resultará útil
 *   para cuestiones de depuración y validación.
 *
 * Imagina que llamamos al programa con los valores anteriores. Un ejemplo de salida
 * podría ser:
 *
 * Proceso con PID 169319: buscando vocales en "Este es un"
 * Proceso con PID 169330: buscando vocales en " texto tal"
 * Proceso con PID 169320: buscando vocales en " cual, sin"
 * Proceso con PID 169322: buscando vocales en " nada más"
 * Proceso con PID 169324: buscando vocales en "que na se"
 * Proceso con PID 168323: buscando vocales en "rie de pal"
 * Proceso con PID 168326: buscando vocales en "abras."
 * Hay un total de 23 vocales en el texto dado.
 */
package com.proferoman.part3

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

fun launchVocal(text: String, resultFile: String): Process {
    val className = "com.proferoman.part2.VocalCount"
    val classPath = System.getProperty("java.class.path")

    println("Aqui se inicia el proceso hijo")
    val processBuilder = ProcessBuilder(
        "java", "-cp", classPath, className,
        text, resultFile
    )

    return processBuilder.start()

}

fun vocalCount(text: String, fileName: String){
    var countVocal = 0;
    val vocales = setOf('a','e','i','o','u')

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

fun main3(text: String, chunkSize: Int) {
    println("Inicio proceso padre")
    val processes = mutableListOf<Process>()
    val pathDir = "./files/part2"

    var count = 1
    var from = 0

    while (from < text.length) {
        // Calculate the range for the current chunk
        val to = if (from + chunkSize < text.length) from + chunkSize else text.length
        val chunk = text.substring(from, to)

        // Prepare the result file path
        val resultFile = "$pathDir/file$count.txt"

        // Launch the process to count vowels in the chunk
        println("Lanzando proceso $count para contar vocales en: \"$chunk\"")
        processes.add(launchVocal(chunk, resultFile))

        // Move to the next chunk
        from += chunkSize
        count++
    }

    // Wait for all processes to finish
    for (p in processes) {
        p.waitFor()
    }

    // Compute total vowels
    var totalVowels = 0
    try {
        for (i in 1 until count) {
            val filePath = "$pathDir/file$i.txt"
            val vowelCount = File(filePath).readText().toInt()
            totalVowels += vowelCount
        }
        println("Hay un total de $totalVowels vocales en el texto.")
    } catch (e: NumberFormatException) {
        println("Error al leer los resultados de los archivos: ${e.message}")
        e.printStackTrace()
    }
}

