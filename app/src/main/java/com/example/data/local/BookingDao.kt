package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.BookingRequestEntity
import com.example.data.model.BookingStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Query("SELECT * FROM booking_requests WHERE clientId = :clientId ORDER BY timestamp DESC")
    fun getBookingsForClient(clientId: Long): Flow<List<BookingRequestEntity>>

    @Query("SELECT * FROM booking_requests WHERE providerId = :providerId ORDER BY timestamp DESC")
    fun getBookingsForProvider(providerId: Long): Flow<List<BookingRequestEntity>>

    @Query("SELECT * FROM booking_requests ORDER BY timestamp DESC")
    fun getAllBookings(): Flow<List<BookingRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingRequestEntity): Long

    @Query("UPDATE booking_requests SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: Long, status: BookingStatus)
}
