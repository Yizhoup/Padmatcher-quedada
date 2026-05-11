package com.example.pruebita.ui.discover

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.pruebita.network.RetrofitClient
import com.example.pruebita.models.Partido
import com.example.pruebita.R

class DiscoverFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cargarPartidos()
    }

    private fun cargarPartidos() {

        lifecycleScope.launch {

            try {
                val response = RetrofitClient.api.getPartidos()

                if (response.isSuccessful) {
                    val partidos = response.body()
                    Log.d("API", "Partidos: $partidos")
                } else {
                    Log.e("API", "Error response")
                }

            } catch (e: Exception) {
                Log.e("API", "Error: ${e.message}")
            }
        }
    }
}