package com.example.pruebita.ui.achievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pruebita.R

class AchievementsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_achievements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvMatchesPlayed = view.findViewById<TextView>(R.id.tvMatchesPlayed)
        val tvWins = view.findViewById<TextView>(R.id.tvWins)
        val tvLosses = view.findViewById<TextView>(R.id.tvLosses)
        val tvWinRate = view.findViewById<TextView>(R.id.tvWinRate)

        // 🔥 DATOS DE EJEMPLO
        val matchesPlayed = 25
        val wins = 18
        val losses = matchesPlayed - wins

        val winRate = if (matchesPlayed > 0) {
            (wins * 100) / matchesPlayed
        } else {
            0
        }

        tvMatchesPlayed.text = "Partidos jugados: $matchesPlayed"
        tvWins.text = "Victorias: $wins"
        tvLosses.text = "Derrotas: $losses"
        tvWinRate.text = "Ratio victoria: $winRate%"
    }
}