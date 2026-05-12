package com.example.pruebita.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.pruebita.MainActivity
import com.example.pruebita.R
import com.example.pruebita.models.LoginRequest
import com.example.pruebita.network.RetrofitClient
import com.example.pruebita.utils.Session
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvGoRegister: TextView
    private lateinit var btnLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        tvGoRegister = findViewById(R.id.tvGoRegister)
        btnLogin = findViewById(R.id.btnLogin)

        // 🔵 ir a registro
        tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {

                try {

                    Log.d("LOGIN", "email=$email password=$password")

                    val response = RetrofitClient.api.login(
                        LoginRequest(email, password)
                    )

                    if (response.isSuccessful) {

                        val user = response.body()

                        if (user != null) {

                            Session.userId = user.id

                            Toast.makeText(
                                this@LoginActivity,
                                "Login correcto",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(
                                Intent(this@LoginActivity, MainActivity::class.java)
                            )
                            finish()

                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Usuario vacío",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {

                        Log.e("LOGIN_ERROR", response.errorBody()?.string().toString())

                        Toast.makeText(
                            this@LoginActivity,
                            "Credenciales incorrectas",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: Exception) {

                    Log.e("LOGIN_EXCEPTION", e.toString())

                    Toast.makeText(
                        this@LoginActivity,
                        "Error de conexión",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}