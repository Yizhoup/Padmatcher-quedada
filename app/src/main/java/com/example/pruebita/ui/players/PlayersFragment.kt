package com.example.pruebita.ui.players

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebita.R
import com.example.pruebita.network.RetrofitClient
import com.example.pruebita.ui.adapters.UsersAdapter
import kotlinx.coroutines.launch

class PlayersFragment : Fragment(R.layout.fragment_players) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cargarUsuarios(view)
    }

    private fun cargarUsuarios(view: View) {

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerUsers)

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val response = RetrofitClient.api.getUsers()

                if (response.isSuccessful) {

                    val users = response.body() ?: emptyList()

                    Log.d("USERS", "Usuarios recibidos: ${users.size}")

                    recycler.layoutManager = LinearLayoutManager(requireContext())

                    recycler.adapter = UsersAdapter(
                        users.sortedByDescending { it.online }
                    )

                } else {

                    Log.e("API_ERROR", response.errorBody()?.string() ?: "Error desconocido")
                }

            } catch (e: Exception) {

                Log.e("API_EXCEPTION", e.toString())
            }
        }
    }
}