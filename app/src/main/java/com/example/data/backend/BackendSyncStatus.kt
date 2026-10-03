package com.example.data.backend

import com.squareup.moshi.JsonClass

/**
 * Information regarding cloud backend connectivity, encryption state,
 * HMAC record integrity, and total stored records.
 */
@JsonClass(generateAdapter = true)
data class BackendSyncStatus(
    val isConnected: Boolean = true,
    val encryptionType: String = "AES-256-GCM Authenticated Encryption",
    val integrityStatus: String = "HMAC-SHA256 Validated (Tamper-Proof)",
    val totalUsersSynced: Int = 0,
    val totalProvidersSynced: Int = 0,
    val lastSyncTimestamp: Long = System.currentTimeMillis(),
    val serverEndpoint: String = "https://api.zucco.ng/v1/secure-vault"
)
