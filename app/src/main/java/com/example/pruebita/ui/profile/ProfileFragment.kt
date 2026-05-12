package com.example.pruebita.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.pruebita.R
import com.example.pruebita.models.NotificationItem
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
    private lateinit var notificationsStatus: TextView
    private lateinit var notificationsLayout: LinearLayout
    private lateinit var markReadButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusText = view.findViewById(R.id.tvProfileStatus)
        nameText = view.findViewById(R.id.tvProfileName)
        emailText = view.findViewById(R.id.tvProfileEmail)
        infoText = view.findViewById(R.id.tvProfileInfo)
        refreshButton = view.findViewById(R.id.btnRefreshProfile)
        logoutButton = view.findViewById(R.id.btnLogout)
        notificationsStatus = view.findViewById(R.id.tvNotificationsStatus)
        notificationsLayout = view.findViewById(R.id.layoutNotifications)
        markReadButton = view.findViewById(R.id.btnMarkNotificationsRead)

        refreshButton.setOnClickListener {
            loadProfile()
            loadNotifications()
        }
        logoutButton.setOnClickListener {
            SessionManager.clearSession(requireContext())
            showNoSession()
            Toast.makeText(requireContext(), "Sesion cerrada", Toast.LENGTH_SHORT).show()
        }
        markReadButton.setOnClickListener { markNotificationsRead() }

        loadProfile()
        loadNotifications()
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
        notificationsStatus.text = "Inicia sesion para ver tus notificaciones"
        notificationsLayout.removeAllViews()
    }

    private fun loadNotifications() {
        val authHeader = SessionManager.getAuthHeader(requireContext())
        if (authHeader == null) {
            notificationsStatus.text = "Inicia sesion para ver tus notificaciones"
            notificationsLayout.removeAllViews()
            return
        }

        notificationsStatus.text = "Cargando..."

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getNotifications(authHeader)
                val notifications = response.body().orEmpty()

                notificationsLayout.removeAllViews()

                if (response.isSuccessful && notifications.isNotEmpty()) {
                    val unread = notifications.count { !it.leida }
                    notificationsStatus.text = "$unread sin leer"
                    notifications.forEach { notification ->
                        notificationsLayout.addView(createNotificationView(notification))
                    }
                } else {
                    notificationsStatus.text = "No hay notificaciones"
                }
            } catch (e: Exception) {
                notificationsStatus.text = "No se pudieron cargar"
            }
        }
    }

    private fun createNotificationView(notification: NotificationItem): LinearLayout {
        val card = LinearLayout(requireContext())
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(dp(12), dp(10), dp(12), dp(10))
        card.setBackgroundResource(R.drawable.input_box)

        val title = TextView(requireContext())
        title.text = formatNotificationType(notification.tipo)
        title.setTextColor(resources.getColor(R.color.text, null))
        title.textSize = 15f
        title.setTypeface(null, android.graphics.Typeface.BOLD)

        val detail = TextView(requireContext())
        detail.text = buildString {
            append(if (notification.leida) "Leida" else "Nueva")
            notification.partido_id?.let { append(" · Partido $it") }
        }
        detail.setTextColor(resources.getColor(R.color.muted, null))
        detail.textSize = 13f

        val deleteButton = AppCompatButton(requireContext())
        deleteButton.text = "Eliminar"
        deleteButton.isAllCaps = false
        deleteButton.setTextColor(resources.getColor(R.color.primary, null))
        deleteButton.setBackgroundResource(R.drawable.button_light)
        deleteButton.setOnClickListener { deleteNotification(notification.id) }

        val buttonParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            dp(38)
        )
        buttonParams.setMargins(0, dp(8), 0, 0)
        deleteButton.layoutParams = buttonParams

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, dp(8))
        card.layoutParams = params

        card.addView(title)
        card.addView(detail)
        card.addView(deleteButton)
        return card
    }

    private fun markNotificationsRead() {
        val authHeader = SessionManager.getAuthHeader(requireContext()) ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.markNotificationsRead(authHeader)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Notificaciones leidas", Toast.LENGTH_SHORT).show()
                    loadNotifications()
                } else {
                    Toast.makeText(requireContext(), "No se pudo actualizar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de conexion", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteNotification(notificationId: Int) {
        val authHeader = SessionManager.getAuthHeader(requireContext()) ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.deleteNotification(notificationId, authHeader)
                if (response.isSuccessful) {
                    loadNotifications()
                } else {
                    Toast.makeText(requireContext(), "No se pudo eliminar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de conexion", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatNotificationType(type: String): String {
        return when (type) {
            "nuevo partido" -> "Nuevo partido"
            "partido completo" -> "Partido completo"
            "partido cancelado" -> "Partido cancelado"
            else -> type
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
