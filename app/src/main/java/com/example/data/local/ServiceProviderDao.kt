package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ServiceProviderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceProviderDao {
    @Query("SELECT * FROM service_providers ORDER BY rating DESC, reviewCount DESC")
    fun getAllProviders(): Flow<List<ServiceProviderEntity>>

    @Query("SELECT * FROM service_providers ORDER BY rating DESC, reviewCount DESC")
    suspend fun getAllProvidersDirect(): List<ServiceProviderEntity>

    @Query("SELECT * FROM service_providers WHERE id = :id")
    fun getProviderById(id: Long): Flow<ServiceProviderEntity?>

    @Query("SELECT * FROM service_providers WHERE id = :id LIMIT 1")
    suspend fun getProviderByIdDirect(id: Long): ServiceProviderEntity?

    @Query("SELECT * FROM service_providers WHERE userId = :userId LIMIT 1")
    fun getProviderByUserId(userId: Long): Flow<ServiceProviderEntity?>

    @Query("SELECT * FROM service_providers WHERE userId = :userId LIMIT 1")
    suspend fun getProviderByUserIdDirect(userId: Long): ServiceProviderEntity?

    @Query("SELECT * FROM service_providers WHERE isSaved = 1 ORDER BY rating DESC")
    fun getSavedProviders(): Flow<List<ServiceProviderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProvider(provider: ServiceProviderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProviders(providers: List<ServiceProviderEntity>)

    @Update
    suspend fun updateProvider(provider: ServiceProviderEntity)

    @Query("UPDATE service_providers SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSavedStatus(id: Long, isSaved: Boolean)

    @Query("UPDATE service_providers SET isAvailable = :isAvailable WHERE id = :id")
    suspend fun updateAvailability(id: Long, isAvailable: Boolean)

    @Query("UPDATE service_providers SET photo1 = :p1, photo2 = :p2, photo3 = :p3 WHERE id = :id")
    suspend fun updatePortfolioPhotos(id: Long, p1: String?, p2: String?, p3: String?)

    @Query("SELECT * FROM service_providers WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun findByUsername(username: String): ServiceProviderEntity?

    @Query("DELETE FROM service_providers WHERE id = :id")
    suspend fun deleteProvider(id: Long)

    @Query("UPDATE service_providers SET isVerified = :isVerified WHERE id = :id")
    suspend fun updateVerifiedStatus(id: Long, isVerified: Boolean)

    @Query("UPDATE service_providers SET rating = :rating, reviewCount = :reviewCount WHERE id = :id")
    suspend fun updateRating(id: Long, rating: Double, reviewCount: Int)

    @Query("DELETE FROM service_providers WHERE isDemoAccount = 1 OR username LIKE '%_power' OR username LIKE '%_plumbing' OR username LIKE '%_beauty' OR username LIKE '%_mechanic'")
    suspend fun deleteDemoProviders()

    @Query("SELECT COUNT(*) FROM service_providers")
    suspend fun countProviders(): Int
}
