package com.tabby.detectorapp.viewmodel

import androidx.lifecycle.ViewModel
import com.tabby.detectorapp.api.LinkCompra

class LibroViewModel : ViewModel() {
    var linksCompra: List<LinkCompra> = emptyList()
}
