package com.rewindvideo.plex.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.util.UUID

/** Persists the Plex account token, the chosen server's credentials, and our device identity. */
class TokenStore(context: Context) {

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "rewind_video_club_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    /** Stable per-install identifier Plex uses to recognize this "device". */
    val clientIdentifier: String
        get() = prefs.getString(KEY_CLIENT_ID, null) ?: UUID.randomUUID().toString().also {
            prefs.edit().putString(KEY_CLIENT_ID, it).apply()
        }

    var accountAuthToken: String?
        get() = prefs.getString(KEY_ACCOUNT_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_ACCOUNT_TOKEN, value).apply()

    var serverName: String?
        get() = prefs.getString(KEY_SERVER_NAME, null)
        set(value) = prefs.edit().putString(KEY_SERVER_NAME, value).apply()

    var serverBaseUrl: String?
        get() = prefs.getString(KEY_SERVER_URL, null)
        set(value) = prefs.edit().putString(KEY_SERVER_URL, value).apply()

    var serverAccessToken: String?
        get() = prefs.getString(KEY_SERVER_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_SERVER_TOKEN, value).apply()

    val hasServerSession: Boolean
        get() = serverBaseUrl != null && serverAccessToken != null

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_CLIENT_ID = "client_identifier"
        private const val KEY_ACCOUNT_TOKEN = "account_auth_token"
        private const val KEY_SERVER_NAME = "server_name"
        private const val KEY_SERVER_URL = "server_base_url"
        private const val KEY_SERVER_TOKEN = "server_access_token"
    }
}
