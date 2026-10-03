package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BookingStatus {
    PENDING,
    ACCEPTED,
    COMPLETED,
    CANCELLED
}

@Entity(tableName = "booking_requests")
data class BookingRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientId: Long,
    val clientName: String,
    val clientPhone: String,
    val clientLocation: String,
    val providerId: Long,
    val providerBusinessName: String,
    val serviceCategory: String,
    val issueDescription: String,
    val scheduledDate: String,
    val urgency: String = "Standard",
    val status: BookingStatus = BookingStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis()
)
