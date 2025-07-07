package com.tabby.detectorapp.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tabby.detectorapp.R
import com.tabby.detectorapp.api.LinkCompra
import com.tabby.detectorapp.viewmodel.LibroViewModel

class StoreFragment : Fragment() {

    private val libroViewModel: LibroViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_store, container, false)

        val containerLinks = view.findViewById<LinearLayout>(R.id.containerLinksCompra)
        containerLinks.removeAllViews()

        val links = libroViewModel.linksCompra

        // 🔍 Log para depuración
        Log.d("StoreFragment", "Links de compra cargados: ${links.size}")
        links.forEachIndexed { i, link ->
            Log.d("StoreFragment", "[$i] ${link.origen} - ${link.precio} - ${link.similitud} - ${link.url}")
        }

        if (links.isNotEmpty()) {
            for ((index, link) in links.withIndex()) {
                val linkView = crearVistaLink(link, index)
                containerLinks.addView(linkView)
            }
        } else {
            val noDataView = TextView(requireContext()).apply {
                text = "No hay links de compra disponibles."
                textSize = 16f
                setPadding(16, 16, 16, 16)
            }
            containerLinks.addView(noDataView)
        }

        return view
    }

    private fun crearVistaLink(link: LinkCompra, index: Int): View {
        val itemView = layoutInflater.inflate(R.layout.item_link_compra, null)

        val textOrigen = itemView.findViewById<TextView>(R.id.textOrigen)
        val textPrecio = itemView.findViewById<TextView>(R.id.textPrecio)
        val textSimilitud = itemView.findViewById<TextView>(R.id.textSimilitud)
        val textUrl = itemView.findViewById<TextView>(R.id.textUrl)

        textOrigen.text = "${index + 1}. ${link.origen}"
        textPrecio.text = "Precio: $${link.precio}"
        textSimilitud.text = "Similitud: %.2f".format(link.similitud)
        textUrl.text = link.url

        itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
            startActivity(intent)
        }

        return itemView
    }
}
