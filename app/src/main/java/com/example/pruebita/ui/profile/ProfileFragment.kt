package com.example.pruebita.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.pruebita.R
import com.example.pruebita.network.RetrofitClient
import com.example.pruebita.utils.SessionManager
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var statusText: TextView
    private lateinit var nameText: TextView
    private lateinit var emailText: TextView
    private lateinit var infoText: TextView
    private lateinit var refreshButton: Button
    private lateinit var logoutButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusText = view.findViewById(R.id.tvProfileStatus)
        nameText = view.findViewById(R.id.tvProfileName)
        emailText = view.findViewById(R.id.tvProfileEmail)
        infoText = view.findViewById(R.id.tvProfileInfo)
        refreshButton = view.findViewById(R.id.btnRefreshProfile)
        logoutButton = view.findViewById(R.id.btnLogout)

        refreshButton.setOnClickListener { loadProfile() }
        logoutButton.setOnClickListener {
            SessionManager.clearSession(requireContext())
            showNoSession()
            Toast.makeText(requireContext(), "Sesion cerrada", Toast.LENGTH_SHORT).show()
        }

        loadProfile()
    }

    private fun loadProfile() {
        val authHeader = SessionManager.getAuthHeader(requireContext())
        if (authHeader == null) {
            showNoSession()
            return
        }

        statusText.text = "Cargando perfil..."
        refreshButton.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getProfile(authHeader)
                val profile = response.body()

                if (response.isSuccessful && profile != null) {
                    val player = profile.player
                    nameText.text = player.nombre
                    emailText.text = player.email
                    infoText.text = "Nivel: ${player.nivel ?: "-"}\nCiudad: ${player.ciudad ?: "-"}\nRol: ${player.rol}"
                    statusText.text = "Datos del usuario"
                } else {
                    statusText.text = "No se pudo cargar el perfil (${response.code()})"
                }
            } catch (e: Exception) {
                statusText.text = "No se pudo cargar el perfil"
            } finally {
                refreshButton.isEnabled = true
            }
        }
    }

    private fun showNoSession() {
        statusText.text = "No hay sesion iniciada"
        nameText.text = "Usuario sin login"
        emailText.text = "Inicia sesion para ver tu perfil"
        infoText.text = "Nivel: -\nCiudad: -\nRol: -"
    }
}
