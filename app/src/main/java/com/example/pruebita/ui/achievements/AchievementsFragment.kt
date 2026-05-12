package com.example.pruebita.ui.achievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.pruebita.R
import com.example.pruebita.network.RetrofitClient
import kotlinx.coroutines.launch

class AchievementsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_achievements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvMatchesPlayed = view.findViewById<TextView>(R.id.tvMatchesPlayed)
        val tvWins = view.findViewById<TextView>(R.id.tvWins)
        val tvLosses = view.findViewById<TextView>(R.id.tvLosses)
        val tvWinRate = view.findViewById<TextView>(R.id.tvWinRate)

        val userId = 1

        viewLifecycleOwner.lifecycleScope.launch {
            try {

                val response = RetrofitClient.api.getAchievements(userId)

                tvMatchesPlayed.text = "Partidos jugados: ${response.matchesPlayed}"
                tvWins.text = "Victorias: ${response.wins}"
                tvLosses.text = "Derrotas: ${response.losses}"
                tvWinRate.text = "Ratio victoria: ${response.winRate}%"

            } catch (e: Exception) {

                tvMatchesPlayed.text = "Error: ${e.message}"
                tvWins.text = ""
                tvLosses.text = ""
                tvWinRate.text = ""

            }
        }
    }
}