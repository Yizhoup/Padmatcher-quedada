package com.example.pruebita.utils

class SessionManager {
    companion object {
        private const val PREFS_NAME = "padmatcher_session"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"

        fun saveSession(context: android.content.Context, token: String, userId: Int) {
            context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_TOKEN, token)
                .putInt(KEY_USER_ID, userId)
                .apply()
        }

        fun getAuthHeader(context: android.content.Context): String? {
            val token = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
                .getString(KEY_TOKEN, null)

            return token?.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
        }

        fun getUserId(context: android.content.Context): Int {
            return context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
                .getInt(KEY_USER_ID, 1)
        }

        fun clearSession(context: android.content.Context) {
            context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
        }
    }
}
