package com.example.pruebita.ui.home

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.pruebita.R
import com.example.pruebita.models.MatchPublic
import com.example.pruebita.network.RetrofitClient
import com.example.pruebita.utils.SessionManager
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var statusText: TextView
    private lateinit var matchesLayout: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusText = view.findViewById(R.id.tvHomeStatus)
        matchesLayout = view.findViewById(R.id.layoutMatches)

        loadMatches()
    }

    private fun loadMatches() {
        val authHeader = SessionManager.getAuthHeader(requireContext())
        if (authHeader == null) {
            statusText.text = "Inicia sesion para ver los partidos disponibles."
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getAvailableMatches(authHeader)
                val matches = response.body().orEmpty()

                matchesLayout.removeAllViews()

                val joinedIds = getJoinedMatchIds(authHeader)

                if (response.isSuccessful && matches.isNotEmpty()) {
                    statusText.text = "Partidos disponibles"
                    matches.forEach { match ->
                        matchesLayout.addView(createMatchView(match, joinedIds.contains(match.id)))
                    }
                } else if (response.code() == 404) {
                    statusText.text = "No hay partidos disponibles ahora mismo."
                } else {
                    statusText.text = "No se pudieron cargar los partidos (${response.code()})."
                }
            } catch (e: Exception) {
                statusText.text = "No se pudieron cargar los partidos."
            }
        }
    }

    private suspend fun getJoinedMatchIds(authHeader: String): Set<Int> {
        return try {
            val response = RetrofitClient.api.getMyInscriptions(authHeader)
            if (response.isSuccessful) {
                response.body().orEmpty().map { it.partido_id }.toSet()
            } else {
                emptySet()
            }
        } catch (e: Exception) {
            emptySet()
        }
    }

    private fun createMatchView(match: MatchPublic, isJoined: Boolean): LinearLayout {
        val card = LinearLayout(requireContext())
        card.orientation = LinearLayout.VERTICAL
        card.setBackgroundResource(R.drawable.box_section)
        card.setPadding(dp(16), dp(14), dp(16), dp(14))

        val topRow = LinearLayout(requireContext())
        topRow.orientation = LinearLayout.HORIZONTAL
        topRow.gravity = Gravity.CENTER_VERTICAL

        val titleText = TextView(requireContext())
        titleText.text = match.ubicacion
        titleText.setTextColor(resources.getColor(R.color.text, null))
        titleText.textSize = 17f
        titleText.setTypeface(null, android.graphics.Typeface.BOLD)

        val statusText = TextView(requireContext())
        statusText.text = if (isJoined) "Apuntado" else "Disponible"
        statusText.setTextColor(resources.getColor(if (isJoined) R.color.primary else R.color.muted, null))
        statusText.textSize = 13f

        val titleBox = LinearLayout(requireContext())
        titleBox.orientation = LinearLayout.VERTICAL
        titleBox.addView(titleText)
        titleBox.addView(statusText)

        val infoText = TextView(requireContext())
        infoText.text = "${match.fecha} a las ${match.hora.take(5)}\n${match.plazas_totales ?: "-"} plazas · ${match.estado ?: "-"}"
        infoText.setTextColor(resources.getColor(R.color.muted, null))
        infoText.textSize = 14f
        infoText.setLineSpacing(3f, 1.0f)

        val actionButton = Button(requireContext())
        actionButton.text = if (isJoined) "Cancelar" else "Unirme"
        actionButton.setTextColor(resources.getColor(if (isJoined) R.color.danger else R.color.white, null))
        actionButton.setBackgroundResource(if (isJoined) R.drawable.button_cancel else R.drawable.button_style)
        actionButton.isAllCaps = false
        actionButton.textSize = 14f
        actionButton.minWidth = dp(92)
        actionButton.minHeight = 0
        actionButton.setPadding(dp(14), 0, dp(14), 0)
        actionButton.setOnClickListener {
            if (isJoined) {
                showCancelDialog(match, actionButton)
            } else {
                showJoinDialog(match, actionButton)
            }
        }

        val titleParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )
        titleBox.layoutParams = titleParams

        val actionParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            dp(38)
        )
        actionButton.layoutParams = actionParams

        topRow.addView(titleBox)
        topRow.addView(actionButton)

        val infoParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        infoParams.setMargins(0, dp(12), 0, 0)
        infoText.layoutParams = infoParams

        card.addView(topRow)
        card.addView(infoText)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 12)
        card.layoutParams = params

        return card
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun showJoinDialog(match: MatchPublic, button: Button) {
        AlertDialog.Builder(requireContext())
            .setTitle("Unirme al partido")
            .setMessage("Quieres apuntarte al partido en ${match.ubicacion} el ${match.fecha} a las ${match.hora.take(5)}?")
            .setNegativeButton("No", null)
            .setPositiveButton("Si") { _, _ ->
                joinMatch(match.id, button)
            }
            .show()
    }

    private fun showCancelDialog(match: MatchPublic, button: Button) {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancelar inscripcion")
            .setMessage("Seguro que quieres salir del partido en ${match.ubicacion}?")
            .setNegativeButton("No", null)
            .setPositiveButton("Cancelar") { _, _ ->
                cancelInscription(match.id, button)
            }
            .show()
    }

    private fun joinMatch(matchId: Int, button: Button) {
        val authHeader = SessionManager.getAuthHeader(requireContext())
        if (authHeader == null) {
            Toast.makeText(requireContext(), "Inicia sesion primero", Toast.LENGTH_SHORT).show()
            return
        }

        button.isEnabled = false
        button.text = "..."

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.joinMatch(matchId, authHeader)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Te has unido al partido", Toast.LENGTH_SHORT).show()
                    loadMatches()
                } else {
                    Toast.makeText(requireContext(), "No se pudo unir (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de conexion", Toast.LENGTH_SHORT).show()
            } finally {
                button.isEnabled = true
                button.text = "Unirme"
            }
        }
    }

    private fun cancelInscription(matchId: Int, button: Button) {
        val authHeader = SessionManager.getAuthHeader(requireContext())
        if (authHeader == null) {
            Toast.makeText(requireContext(), "Inicia sesion primero", Toast.LENGTH_SHORT).show()
            return
        }

        button.isEnabled = false
        button.text = "..."

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.cancelInscription(matchId, authHeader)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Inscripcion cancelada", Toast.LENGTH_SHORT).show()
                    loadMatches()
                } else {
                    Toast.makeText(requireContext(), "No se pudo cancelar (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de conexion", Toast.LENGTH_SHORT).show()
            } finally {
                button.isEnabled = true
                button.text = "Cancelar"
            }
        }
    }
}
