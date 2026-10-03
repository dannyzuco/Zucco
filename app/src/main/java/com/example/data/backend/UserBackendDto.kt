package com.example.data.backend

import com.example.data.model.UserEntity
import com.example.data.security.SecurityHelper
import com.squareup.moshi.JsonClass

/**
 * UserBackendDto represents user account information stored in the secure backend.
 * Passwords are never stored in cleartext; only salted cryptographic hashes and HMAC
 * signatures are stored to prevent tampering and credential leakage.
 */
@JsonClass(generateAdapter = true)
data class UserBackendDto(
    val id: Long,
    val email: String,
    val passwordHash: String,
    val salt: String,
    val fullName: String,
    val phoneNumber: String,
    val role: String,
    val username: String?,
    val city: String,
    val district: String,
    val profileDp: String? = null,
    val isApproved: Boolean,
    val createdAt: Long,
    val lastSyncedAt: Long = System.currentTimeMillis(),
    val hmacSignature: String
)

/**
 * Converts a UserEntity to a secure UserBackendDto with salted SHA-256 password hash
 * and an HMAC tamper-detection signature.
 */
fun UserEntity.toSecureBackendDto(existingSalt: String? = null): UserBackendDto {
    val salt = existingSalt ?: SecurityHelper.generateSalt()
    val passwordHash = SecurityHelper.hashPassword(password, salt)
    val signatureData = "user:$id:$email:$role:$isApproved:$passwordHash:$city"
    val signature = SecurityHelper.generateHmacSignature(signatureData)

    return UserBackendDto(
        id = id,
        email = email,
        passwordHash = passwordHash,
        salt = salt,
        fullName = fullName,
        phoneNumber = phoneNumber,
        role = role.name,
        username = username,
        city = city,
        district = district,
        profileDp = profileDp,
        isApproved = isApproved,
        createdAt = createdAt,
        lastSyncedAt = System.currentTimeMillis(),
        hmacSignature = signature
    )
}
