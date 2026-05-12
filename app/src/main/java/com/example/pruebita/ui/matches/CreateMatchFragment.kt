package com.example.pruebita.ui.matches

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.pruebita.R
import com.example.pruebita.models.CreatePartidoRequest
import com.example.pruebita.network.RetrofitClient
import com.example.pruebita.utils.SessionManager
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

class CreateMatchFragment : Fragment(R.layout.fragment_create_match) {

    private val districts = listOf("Centro", "Salamanca", "Chamartin", "Retiro", "Moncloa")
    private val courtsByDistrict = mapOf(
        "Centro" to listOf("La Latina Club", "Padel Sol", "Plaza Central Courts"),
        "Salamanca" to listOf("Goya Padel Hub", "Lista Indoor", "Velazquez Courts"),
        "Chamartin" to listOf("Bernabeu Padel", "Chamartin Arena", "Castilla Racquet Club"),
        "Retiro" to listOf("Parque Central Padel", "Retiro Club", "Ibiza Match Point"),
        "Moncloa" to listOf("Green Park Courts", "Moncloa Match Point", "Ciudad Universitaria Padel")
    )

    private lateinit var districtSpinner: Spinner
    private lateinit var courtSpinner: Spinner
    private lateinit var levelSpinner: Spinner
    private lateinit var playersSpinner: Spinner
    private lateinit var dateText: TextView
    private lateinit var timeText: TextView
    private lateinit var descriptionInput: EditText
    private lateinit var descriptionCounter: TextView
    private lateinit var resumenText: TextView
    private lateinit var createButton: Button

    private var selectedDate = ""
    private var selectedTime = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        districtSpinner = view.findViewById(R.id.spinnerDistrict)
        courtSpinner = view.findViewById(R.id.spinnerCourt)
        levelSpinner = view.findViewById(R.id.spinnerLevel)
        playersSpinner = view.findViewById(R.id.spinnerPlayers)
        dateText = view.findViewById(R.id.tvSelectedDate)
        timeText = view.findViewById(R.id.tvSelectedTime)
        descriptionInput = view.findViewById(R.id.etDescription)
        descriptionCounter = view.findViewById(R.id.tvDescriptionCounter)
        resumenText = view.findViewById(R.id.tvResumen)
        createButton = view.findViewById(R.id.btnCreateMatch)

        setupSpinners()
        setupDescriptionCounter()
        updateResumen()

        view.findViewById<Button>(R.id.btnPickDate).setOnClickListener { showDatePicker() }
        view.findViewById<Button>(R.id.btnPickTime).setOnClickListener { showTimePicker() }
        createButton.setOnClickListener { createMatch() }
    }

    private fun setupSpinners() {
        districtSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            districts
        )

        districtSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateCourtOptions(districts[position])
                updateResumen()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        levelSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("iniciacion", "intermedio", "avanzado", "cualquiera")
        )

        playersSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf(2, 4)
        )

        levelSpinner.onItemSelectedListener = simpleSpinnerListener()
        playersSpinner.onItemSelectedListener = simpleSpinnerListener()
    }

    private fun updateCourtOptions(district: String) {
        courtSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            courtsByDistrict[district].orEmpty()
        )
        courtSpinner.onItemSelectedListener = simpleSpinnerListener()
    }

    private fun simpleSpinnerListener(): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                updateResumen()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun setupDescriptionCounter() {
        descriptionInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                descriptionCounter.text = "${s?.length ?: 0}/120"
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                dateText.text = selectedDate
                updateResumen()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000L
        }.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                selectedTime = String.format("%02d:%02d:00", hour, minute)
                timeText.text = selectedTime.take(5)
                updateResumen()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun createMatch() {
        val validationError = validateForm()
        if (validationError != null) {
            Toast.makeText(requireContext(), validationError, Toast.LENGTH_SHORT).show()
            return
        }

        val district = districtSpinner.selectedItem.toString()
        val court = courtSpinner.selectedItem.toString()
        val authHeader = SessionManager.getAuthHeader(requireContext())

        if (authHeader == null) {
            Toast.makeText(requireContext(), "Inicia sesion antes de crear una quedada", Toast.LENGTH_SHORT).show()
            return
        }

        val request = CreatePartidoRequest(
            creador_id = SessionManager.getUserId(requireContext()),
            fecha = selectedDate,
            hora = selectedTime,
            ubicacion = "$court, $district",
            nivel_requerido = levelSpinner.selectedItem.toString(),
            plazas_totales = playersSpinner.selectedItem as Int,
            descripcion = descriptionInput.text.toString().trim().ifBlank { null }
        )

        createButton.isEnabled = false
        createButton.text = "Creando..."

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.createPartido(authHeader, request)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Quedada creada", Toast.LENGTH_SHORT).show()
                    clearForm()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error al crear: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                createButton.isEnabled = true
                createButton.text = "Crear quedada"
            }
        }
    }

    private fun validateForm(): String? {
        if (selectedDate.isBlank()) return "Selecciona una fecha"
        if (selectedTime.isBlank()) return "Selecciona una hora"

        val date = LocalDate.parse(selectedDate)
        val time = LocalTime.parse(selectedTime)
        val today = LocalDate.now()

        if (date.isBefore(today) || (date.isEqual(today) && time.isBefore(LocalTime.now()))) {
            return "No puedes crear una quedada en una fecha pasada"
        }

        return null
    }

    private fun clearForm() {
        selectedDate = ""
        selectedTime = ""
        dateText.text = "Sin fecha"
        timeText.text = "Sin hora"
        descriptionInput.text.clear()
        levelSpinner.setSelection(1)
        playersSpinner.setSelection(1)
        updateResumen()
    }

    private fun updateResumen() {
        if (!::resumenText.isInitialized || !::courtSpinner.isInitialized || courtSpinner.selectedItem == null) {
            return
        }

        val district = districtSpinner.selectedItem?.toString() ?: "-"
        val court = courtSpinner.selectedItem?.toString() ?: "-"
        val level = levelSpinner.selectedItem?.toString() ?: "-"
        val players = playersSpinner.selectedItem?.toString() ?: "-"
        val date = selectedDate.ifBlank { "fecha pendiente" }
        val time = selectedTime.ifBlank { "hora pendiente" }.take(5)

        resumenText.text = "Resumen: $court ($district), $date a las $time. Nivel $level, $players plazas."
    }
}
