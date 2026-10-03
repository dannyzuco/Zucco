package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    fun getUserByEmail(email: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE (LOWER(email) = LOWER(:identifier) OR phoneNumber = :identifier) AND password = :password LIMIT 1")
    suspend fun authenticate(identifier: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE (LOWER(email) = LOWER(:identifier) OR phoneNumber = :identifier) LIMIT 1")
    suspend fun findByIdentifier(identifier: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE phoneNumber = :phone LIMIT 1")
    suspend fun findByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun findByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    suspend fun getAllUsersDirect(): List<UserEntity>

    @Query("UPDATE users SET isApproved = :isApproved WHERE id = :id")
    suspend fun updateUserApproval(id: Long, isApproved: Boolean)

    @Query("UPDATE users SET password = :newPassword WHERE id = :id")
    suspend fun updatePassword(id: Long, newPassword: String)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: Long)

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserDirect(id: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE email LIKE '%@zucco.ng' OR email LIKE '%@example.com'")
    suspend fun deleteDemoUsers()

    @Query("SELECT COUNT(*) FROM users")
    suspend fun countUsers(): Int
}
