package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    CLIENT,
    PROVIDER,
    ADMIN
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val password: String = "123456",
    val fullName: String,
    val phoneNumber: String,
    val role: UserRole,
    val username: String? = null,
    val city: String = "Ota",
    val district: String = "Sango Ota",
    val profileDp: String? = null,
    val isApproved: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
