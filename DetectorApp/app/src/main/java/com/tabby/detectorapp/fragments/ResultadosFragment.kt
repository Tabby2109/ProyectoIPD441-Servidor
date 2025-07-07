package com.tabby.detectorapp.fragments

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.tabby.detectorapp.R

class ResultadosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_resultados, container, false)

        val imageView = view.findViewById<ImageView>(R.id.resultImageView)
        val tituloText = view.findViewById<TextView>(R.id.resultTitulo)
        val autorText = view.findViewById<TextView>(R.id.resultAutor)
        val generoText = view.findViewById<TextView>(R.id.resultGenero)
        val descripcionText = view.findViewById<TextView>(R.id.resultDescripcion)
        val modoText = view.findViewById<TextView>(R.id.resultModo)
        val similitudText = view.findViewById<TextView>(R.id.resultSimilitud)

        val imagePath = arguments?.getString("imagePath")
        val titulo = arguments?.getString("titulo") ?: ""
        val autor = arguments?.getString("autor") ?: ""
        val genero = arguments?.getString("genero") ?: ""
        val descripcion = arguments?.getString("descripcion") ?: ""
        val modo = arguments?.getString("modo") ?: ""
        val similitud = arguments?.getDouble("similitud") ?: 0.0

        // Mostrar imagen dependiendo del origen
        if (imagePath != null) {
            when {
                imagePath.startsWith("http") || imagePath.startsWith("https") -> {
                    Glide.with(requireContext()).load(imagePath).into(imageView)
                }
                imagePath.startsWith("content://") || imagePath.startsWith("file://") -> {
                    imageView.setImageURI(Uri.parse(imagePath))
                }
                imagePath.startsWith("/") -> {
                    val fullUrl = "http://192.168.100.210:5000$imagePath"
                    Glide.with(requireContext()).load(fullUrl).into(imageView)
                }
                imagePath.startsWith("data:image") -> {
                    val base64Image = imagePath.substringAfter(",")
                    val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    imageView.setImageBitmap(bitmap)
                }
            }
        }

        // Mostrar resultados
        tituloText.text = titulo
        autorText.text = autor
        generoText.text = genero
        descripcionText.text = descripcion
        modoText.text = "Modo: $modo"
        similitudText.text = "Similitud: %.2f".format(similitud)

        return view
    }
}
