package com.example.util

import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Stores small secrets (API tokens) in SharedPreferences encrypted with an AES-256-GCM key that
 * lives in the Android Keystore and never leaves the device. Values written before encryption
 * existed are returned as-is so callers can migrate them.
 */
object SecureStore {

    private const val TAG = "SecureStore"
    private const val KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "hairlook_secure_store_v1"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_BITS = 128
    private const val PREFIX = "enc1:"

    fun putString(prefs: SharedPreferences, key: String, value: String) {
        if (value.isEmpty()) {
            prefs.edit().remove(key).apply()
            return
        }
        val encrypted = encrypt(value)
        if (encrypted != null) {
            prefs.edit().putString(key, encrypted).apply()
        } else {
            // Never fall back to plaintext storage
            Log.e(TAG, "Encryption unavailable; secret not stored")
            prefs.edit().remove(key).apply()
        }
    }

    /**
     * Returns the decrypted value, "" when missing or no longer decryptable (e.g. restored on a
     * new device), or the raw value when it was stored before encryption ([isLegacyPlaintext]).
     */
    fun getString(prefs: SharedPreferences, key: String): String {
        val stored = prefs.getString(key, null) ?: return ""
        if (!stored.startsWith(PREFIX)) return stored
        return decrypt(stored) ?: ""
    }

    fun isLegacyPlaintext(prefs: SharedPreferences, key: String): Boolean {
        val stored = prefs.getString(key, null) ?: return false
        return !stored.startsWith(PREFIX)
    }

    private fun encrypt(plain: String): String? = try {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val cipherText = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
        PREFIX + Base64.encodeToString(cipher.iv, Base64.NO_WRAP) + ":" +
                Base64.encodeToString(cipherText, Base64.NO_WRAP)
    } catch (e: Exception) {
        Log.e(TAG, "Encryption failed", e)
        null
    }

    private fun decrypt(stored: String): String? = try {
        val parts = stored.removePrefix(PREFIX).split(":")
        require(parts.size == 2)
        val iv = Base64.decode(parts[0], Base64.NO_WRAP)
        val cipherText = Base64.decode(parts[1], Base64.NO_WRAP)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(GCM_TAG_BITS, iv))
        String(cipher.doFinal(cipherText), Charsets.UTF_8)
    } catch (e: Exception) {
        Log.w(TAG, "Could not decrypt stored secret", e)
        null
    }

    private fun secretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE).apply { load(null) }
        (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE)
        generator.init(
            KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generator.generateKey()
    }
}
