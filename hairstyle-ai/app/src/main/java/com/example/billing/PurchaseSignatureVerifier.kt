package com.example.billing

import android.util.Base64
import android.util.Log
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

/**
 * Verifies that purchase data was signed by Google Play with this app's license key
 * (Play Console → Monetization setup → Licensing, "Base64-encoded RSA public key").
 *
 * The public key is safe to ship in the app. This blocks forged purchases from tampered clients;
 * for the strongest protection also verify purchase tokens on a server with the Play Developer API.
 */
object PurchaseSignatureVerifier {

    private const val TAG = "PurchaseVerifier"
    private const val KEY_ALGORITHM = "RSA"
    private const val SIGNATURE_ALGORITHM = "SHA1withRSA"

    fun verify(base64PublicKey: String, signedData: String, signature: String): Boolean {
        if (base64PublicKey.isBlank() || signedData.isEmpty() || signature.isBlank()) return false
        return try {
            val publicKey = decodePublicKey(base64PublicKey)
            val signatureBytes = Base64.decode(signature, Base64.DEFAULT)
            Signature.getInstance(SIGNATURE_ALGORITHM).run {
                initVerify(publicKey)
                update(signedData.toByteArray(Charsets.UTF_8))
                verify(signatureBytes)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Purchase signature verification failed", e)
            false
        }
    }

    private fun decodePublicKey(base64PublicKey: String): PublicKey {
        val keyBytes = Base64.decode(base64PublicKey, Base64.DEFAULT)
        return KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(X509EncodedKeySpec(keyBytes))
    }
}
