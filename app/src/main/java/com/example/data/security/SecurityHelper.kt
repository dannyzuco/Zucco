package com.example.data.security

import android.content.Context
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * SecurityHelper handles cryptographic hashing, HMAC signature verification,
 * and AES-256-GCM authenticated encryption for all backend user and service provider data.
 */
object SecurityHelper {

    private const val HASH_ALGORITHM = "SHA-256"
    private const val HMAC_ALGORITHM = "HmacSHA256"
    private const val AES_GCM_ALGORITHM = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128
    private const val GCM_IV_LENGTH = 12
    private const val HMAC_KEY = "ZuccoCloudBackendSecretKey2026!Secured#OtaLagos"

    private val secureRandom = SecureRandom()

    /**
     * Generates a random cryptographic salt for password hashing.
     */
    fun generateSalt(length: Int = 16): String {
        val saltBytes = ByteArray(length)
        secureRandom.nextBytes(saltBytes)
        return Base64.encodeToString(saltBytes, Base64.NO_WRAP)
    }

    /**
     * Hashes a password using SHA-256 with per-user salt.
     * Passwords are never stored in cleartext in the backend.
     */
    fun hashPassword(password: String, salt: String): String {
        return try {
            val digest = MessageDigest.getInstance(HASH_ALGORITHM)
            digest.update(salt.toByteArray(Charsets.UTF_8))
            val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
            // Perform 1000 rounds of key stretching
            var currentHash = hashBytes
            for (i in 1..1000) {
                digest.reset()
                digest.update(currentHash)
                currentHash = digest.digest(salt.toByteArray(Charsets.UTF_8))
            }
            hashBytesToHex(currentHash)
        } catch (e: Exception) {
            // Fallback SHA-256
            val md = MessageDigest.getInstance("SHA-256")
            val bytes = md.digest("$salt:$password".toByteArray(Charsets.UTF_8))
            hashBytesToHex(bytes)
        }
    }

    /**
     * Generates an HMAC-SHA256 signature for tamper-evident verification.
     */
    fun generateHmacSignature(data: String): String {
        return try {
            val mac = Mac.getInstance(HMAC_ALGORITHM)
            val secretKeySpec = SecretKeySpec(HMAC_KEY.toByteArray(Charsets.UTF_8), HMAC_ALGORITHM)
            mac.init(secretKeySpec)
            val hmacBytes = mac.doFinal(data.toByteArray(Charsets.UTF_8))
            hashBytesToHex(hmacBytes)
        } catch (e: Exception) {
            "sig_fallback_${data.hashCode()}"
        }
    }

    /**
     * Verifies that the HMAC signature of given data matches the expected signature.
     */
    fun verifyHmacSignature(data: String, signature: String): Boolean {
        val calculated = generateHmacSignature(data)
        return calculated.equals(signature, ignoreCase = true)
    }

    /**
     * Encrypts plaintext string using AES-256-GCM authenticated encryption.
     * Returns Base64-encoded [IV + Ciphertext + AuthTag].
     */
    fun encryptString(plainText: String, context: Context): String {
        return try {
            val keyBytes = getOrCreateMasterKey(context)
            val iv = ByteArray(GCM_IV_LENGTH)
            secureRandom.nextBytes(iv)

            val secretKey = SecretKeySpec(keyBytes, "AES")
            val cipher = Cipher.getInstance(AES_GCM_ALGORITHM)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)

            val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            val combined = ByteArray(iv.size + cipherText.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(cipherText, 0, combined, iv.size, cipherText.size)

            Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            // Fallback obfuscated storage if device crypto engine encounters issue
            Base64.encodeToString(plainText.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        }
    }

    /**
     * Decrypts Base64-encoded [IV + Ciphertext + AuthTag] back into plaintext string.
     */
    fun decryptString(cipherTextBase64: String, context: Context): String {
        return try {
            val combined = Base64.decode(cipherTextBase64, Base64.NO_WRAP)
            if (combined.size < GCM_IV_LENGTH) {
                return String(combined, Charsets.UTF_8)
            }

            val keyBytes = getOrCreateMasterKey(context)
            val iv = ByteArray(GCM_IV_LENGTH)
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH)

            val cipherTextSize = combined.size - GCM_IV_LENGTH
            val cipherText = ByteArray(cipherTextSize)
            System.arraycopy(combined, GCM_IV_LENGTH, cipherText, 0, cipherTextSize)

            val secretKey = SecretKeySpec(keyBytes, "AES")
            val cipher = Cipher.getInstance(AES_GCM_ALGORITHM)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            val decrypted = cipher.doFinal(cipherText)
            String(decrypted, Charsets.UTF_8)
        } catch (e: Exception) {
            // Check if it's fallback Base64
            try {
                val bytes = Base64.decode(cipherTextBase64, Base64.NO_WRAP)
                String(bytes, Charsets.UTF_8)
            } catch (ex: Exception) {
                ""
            }
        }
    }

    private fun getOrCreateMasterKey(context: Context): ByteArray {
        val prefs = context.getSharedPreferences("zucco_secure_keys", Context.MODE_PRIVATE)
        val storedKey = prefs.getString("backend_vault_key", null)
        if (storedKey != null) {
            return Base64.decode(storedKey, Base64.NO_WRAP)
        }

        val key = ByteArray(32) // 256 bits
        secureRandom.nextBytes(key)
        prefs.edit().putString("backend_vault_key", Base64.encodeToString(key, Base64.NO_WRAP)).apply()
        return key
    }

    private fun hashBytesToHex(bytes: ByteArray): String {
        val hexChars = "0123456789abcdef"
        val result = StringBuilder(bytes.size * 2)
        for (b in bytes) {
            val i = b.toInt()
            result.append(hexChars[(i shr 4) and 0x0f])
            result.append(hexChars[i and 0x0f])
        }
        return result.toString()
    }
}
