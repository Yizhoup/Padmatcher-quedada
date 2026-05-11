package com.example.pruebita.data.repository

class AuthRepository {

    fun login(email: String, password: String): Boolean {
        // simulación básica
        return email == "test@test.com" && password == "1234"
    }
}