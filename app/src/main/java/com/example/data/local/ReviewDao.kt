package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE providerId = :providerId ORDER BY id DESC")
    fun getReviewsForProvider(providerId: Long): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE providerId = :providerId")
    suspend fun getReviewsForProviderDirect(providerId: Long): List<ReviewEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)
}
