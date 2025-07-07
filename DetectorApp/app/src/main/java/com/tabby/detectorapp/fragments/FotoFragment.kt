package com.tabby.detectorapp.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.tabby.detectorapp.R
import com.tabby.detectorapp.api.LinkCompra
import com.tabby.detectorapp.api.RespuestaServidor
import com.tabby.detectorapp.api.RetrofitClient
import com.tabby.detectorapp.viewmodel.LibroViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class FotoFragment : Fragment() {

    private lateinit var radioModo: RadioGroup
    private lateinit var btnTomarFoto: Button
    private lateinit var btnSubirImagen: Button
    private lateinit var progressBar: ProgressBar

    private val libroViewModel: LibroViewModel by activityViewModels()

    private val REQUEST_CODE_PICK_IMAGE = 1
    private val REQUEST_CODE_TAKE_PHOTO = 2
    private var currentImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_foto, container, false)

        radioModo = view.findViewById(R.id.radioModo)
        btnTomarFoto = view.findViewById(R.id.btnTomarFoto)
        btnSubirImagen = view.findViewById(R.id.btnSubirImagen)
        progressBar = ProgressBar(requireContext()).apply {
            isIndeterminate = true
            visibility = View.GONE
        }

        (view as ViewGroup).addView(progressBar)

        btnSubirImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
        }

        btnTomarFoto.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_CODE_PICK_IMAGE -> {
                    currentImageUri = data.data
                    currentImageUri?.let { uri ->
                        enviarImagenAlServidor(uri)
                    }
                }

                REQUEST_CODE_TAKE_PHOTO -> {
                    val photoBitmap = data.extras?.get("data") as? Bitmap
                    val uri = guardarBitmapEnCache(photoBitmap)
                    uri?.let {
                        currentImageUri = it
                        enviarImagenAlServidor(it)
                    }
                }
            }
        }
    }

    private fun guardarBitmapEnCache(bitmap: Bitmap?): Uri? {
        if (bitmap == null) return null
        val file = File(requireContext().cacheDir, "temp_image.jpg")
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
            it.flush()
        }
        return Uri.fromFile(file)
    }

    private fun enviarImagenAlServidor(uri: Uri) {
        progressBar.visibility = View.VISIBLE

        val modoSeleccionado = when (radioModo.checkedRadioButtonId) {
            R.id.radioYoloClip -> "yolo+clip"
            else -> "clip"
        }

        val file = File(uri.path ?: return)
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val modo = RequestBody.create("text/plain".toMediaTypeOrNull(), modoSeleccionado)

        val call = RetrofitClient.instance.enviarImagenConModo(body, modo)
        call.enqueue(object : Callback<RespuestaServidor> {
            override fun onResponse(call: Call<RespuestaServidor>, response: Response<RespuestaServidor>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val res = response.body()
                    res?.let {
                        val imagePath = if (it.modo.contains("yolo") && it.imagen_procesada != null) {
                            it.imagen_procesada
                        } else {
                            uri.toString()
                        }

                        // Guardar links en el ViewModel compartido
                        Log.d("FotoFragment", "Links recibidos: ${it.linksCompra?.size}")
                        libroViewModel.linksCompra = it.linksCompra ?: emptyList()

                        val bundle = Bundle().apply {
                            putString("imagePath", imagePath)
                            putString("titulo", it.titulo)
                            putString("autor", it.autor)
                            putString("genero", it.genero)
                            putString("descripcion", it.descripcion)
                            putString("modo", it.modo)
                            putDouble("similitud", it.similitud)
                        }

                        findNavController().navigate(R.id.action_fotoFragment_to_resultadosFragment, bundle)
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RespuestaServidor>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
