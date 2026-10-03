package com.example.data.local

import com.example.data.model.BookingRequestEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.ServiceProviderEntity
import com.example.data.model.UserEntity

object InitialSeedData {
    val seedUsers = emptyList<UserEntity>()

    fun getSeedProviders(packageName: String = "com.example"): List<ServiceProviderEntity> {
        return emptyList()
    }

    val seedReviews = emptyList<ReviewEntity>()

    val seedBookings = emptyList<BookingRequestEntity>()
}
