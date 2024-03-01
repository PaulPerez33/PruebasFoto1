package com.example.pruebaine
// Importar las librerías necesarias
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition

// Definir una constante para el código de solicitud de la cámara
const val REQUEST_IMAGE_CAPTURE = 1

// Crear una clase que herede de AppCompatActivity
class MainActivity : AppCompatActivity() {

    // Declarar las variables para los elementos de la interfaz
    private lateinit var imageView: ImageView
    private lateinit var button: Button

    // Sobreescribir el método onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar las variables con los elementos de la interfaz
        imageView = findViewById(R.id.imageView)
        button = findViewById(R.id.button)

        // Establecer un listener para el botón
        button.setOnClickListener {
            // Invocar el método para tomar una foto
            takePicture()
        }
    }

    // Crear un método para tomar una foto
    private fun takePicture() {
        // Crear un intent para la acción de capturar una imagen
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Verificar si hay una actividad que pueda manejar el intent
        if (intent.resolveActivity(packageManager) != null) {
            // Iniciar la actividad esperando un resultado
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    // Sobreescribir el método onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Verificar si el código de solicitud y el código de resultado son los esperados
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Obtener el extra que contiene el bitmap de la imagen capturada
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // Mostrar la imagen en el imageView
            imageView.setImageBitmap(imageBitmap)
            // Invocar el método para reconocer el texto de la imagen
            recognizeText(imageBitmap)
        }
    }

    // Crear un método para reconocer el texto de la imagen
    private fun recognizeText(imageBitmap: Bitmap) {
        // Crear un objeto InputImage a partir del bitmap
        val inputImage = InputImage.fromBitmap(imageBitmap, 0)
        // Obtener una instancia del reconocedor de texto
        val textRecognizer = TextRecognition.getClient()
        // Procesar la imagen con el reconocedor de texto
        textRecognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                // Si el proceso fue exitoso, obtener el texto reconocido
                val text = visionText.text
                // Invocar el método para extraer los datos de la credencial
                extractData(text)
            }
            .addOnFailureListener { e ->
                // Si el proceso falló, mostrar el error
                e.printStackTrace()
            }
    }

    // Crear un método para extraer los datos de la credencial
    private fun extractData(text: String) {
        // Definir un patrón de expresión regular para cada dato de la credencial
        val namePattern = "Nombre: (.*)"
        val lastNamePattern = "Apellido: (.*)"
        val idPattern = "Cédula: (.*)"
        val birthDatePattern = "Fecha de nacimiento: (.*)"
        val genderPattern = "Género: (.*)"

        // Crear un objeto Regex para cada patrón
        val nameRegex = Regex(namePattern)
        val lastNameRegex = Regex(lastNamePattern)
        val idRegex = Regex(idPattern)
        val birthDateRegex = Regex(birthDatePattern)
        val genderRegex = Regex(genderPattern)

        // Buscar el primer resultado que coincida con cada patrón en el texto
        val nameResult = nameRegex.find(text)
        val lastNameResult = lastNameRegex.find(text)
        val idResult = idRegex.find(text)
        val birthDateResult = birthDateRegex.find(text)
        val genderResult = genderRegex.find(text)

        // Obtener el valor del grupo capturado por cada resultado, o una cadena vacía si no hay resultado
        val name = nameResult?.groupValues?.get(1) ?: ""
        val lastName = lastNameResult?.groupValues?.get(1) ?: ""
        val id = idResult?.groupValues?.get(1) ?: ""
        val birthDate = birthDateResult?.groupValues?.get(1) ?: ""
        val gender = genderResult?.groupValues?.get(1) ?: ""

        // Mostrar los datos extraídos en la consola
        println("Nombre: $name")
        println("Apellido: $lastName")
        println("Cédula: $id")
        println("Fecha de nacimiento: $birthDate")
        println("Género: $gender")
    }
}
