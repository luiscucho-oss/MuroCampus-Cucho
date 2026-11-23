package com.tecsup.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.murocampus.data.PostRepositoryRemote
import com.tecsup.murocampus.data.remote.RetrofitClient

// Aquí se "arma" la cadena: RetrofitClient -> PostRepositoryRemote -> PostsViewModel
class PostsViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(PostsViewModel::class.java)) {
            "ViewModel desconocido: ${modelClass.name}"
        }
        return PostsViewModel(PostRepositoryRemote(RetrofitClient.api)) as T
    }
}
