package com.example.pruebita.ui.players

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.pruebita.R
import com.example.pruebita.models.PlayerPublic
import com.example.pruebita.network.RetrofitClient
import com.example.pruebita.utils.SessionManager
import kotlinx.coroutines.launch

class PlayersFragment : Fragment(R.layout.fragment_players) {

    private lateinit var statusText: TextView
    private lateinit var playersLayout: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusText = view.findViewById(R.id.tvPlayersStatus)
        playersLayout = view.findViewById(R.id.layoutPlayers)

        loadPlayers()
    }

    private fun loadPlayers() {
        val authHeader = SessionManager.getAuthHeader(requireContext())
        if (authHeader == null) {
            statusText.text = "Inicia sesion para ver los jugadores."
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getPlayers(authHeader)
                val players = response.body().orEmpty()

                playersLayout.removeAllViews()

                if (response.isSuccessful && players.isNotEmpty()) {
                    statusText.text = "Jugadores registrados"
                    players.forEach { player ->
                        playersLayout.addView(createPlayerView(player))
                    }
                } else if (response.code() == 404) {
                    statusText.text = "No hay jugadores registrados."
                } else {
                    statusText.text = "No se pudieron cargar los jugadores (${response.code()})."
                }
            } catch (e: Exception) {
                statusText.text = "No se pudieron cargar los jugadores."
            }
        }
    }

    private fun createPlayerView(player: PlayerPublic): LinearLayout {
        val card = LinearLayout(requireContext())
        card.orientation = LinearLayout.HORIZONTAL
        card.gravity = Gravity.CENTER_VERTICAL
        card.setBackgroundResource(R.drawable.box_section)
        card.setPadding(dp(14), dp(14), dp(14), dp(14))

        val avatar = TextView(requireContext())
        avatar.text = player.nombre.take(1).uppercase()
        avatar.gravity = Gravity.CENTER
        avatar.setTextColor(resources.getColor(R.color.primary, null))
        avatar.textSize = 20f
        avatar.setTypeface(null, android.graphics.Typeface.BOLD)
        avatar.setBackgroundResource(R.drawable.profile_circle)

        val nameText = TextView(requireContext())
        nameText.text = player.nombre
        nameText.setTextColor(resources.getColor(R.color.text, null))
        nameText.textSize = 18f
        nameText.setTypeface(null, android.graphics.Typeface.BOLD)

        val emailText = TextView(requireContext())
        emailText.text = player.email
        emailText.setTextColor(resources.getColor(R.color.muted, null))
        emailText.textSize = 14f

        val tagsRow = LinearLayout(requireContext())
        tagsRow.orientation = LinearLayout.HORIZONTAL
        tagsRow.addView(createTag(player.nivel ?: "sin nivel", true))
        tagsRow.addView(createTag(player.ciudad ?: "sin ciudad", false))

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 12)
        card.layoutParams = params

        val avatarParams = LinearLayout.LayoutParams(dp(52), dp(52))
        avatar.layoutParams = avatarParams

        val textBox = LinearLayout(requireContext())
        textBox.orientation = LinearLayout.VERTICAL

        val textBoxParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT
            , 1f
        )
        textBoxParams.setMargins(dp(14), 0, 0, 0)
        textBox.layoutParams = textBoxParams

        val emailParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        emailParams.setMargins(0, dp(3), 0, 0)
        emailText.layoutParams = emailParams

        val tagsParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        tagsParams.setMargins(0, dp(9), 0, 0)
        tagsRow.layoutParams = tagsParams

        textBox.addView(nameText)
        textBox.addView(emailText)
        textBox.addView(tagsRow)

        card.addView(avatar)
        card.addView(textBox)
        return card
    }

    private fun createTag(text: String, selected: Boolean): TextView {
        val tag = TextView(requireContext())
        tag.text = text
        tag.textSize = 12f
        tag.setTextColor(resources.getColor(if (selected) R.color.primary else R.color.muted, null))
        tag.setBackgroundResource(if (selected) R.drawable.tag_blue else R.drawable.tag_gray)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, dp(8), 0)
        tag.layoutParams = params

        return tag
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
