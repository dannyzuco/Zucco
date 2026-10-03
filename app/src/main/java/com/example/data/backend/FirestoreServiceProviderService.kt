package com.example.data.backend

import android.util.Log
import com.example.data.model.ServiceProviderEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * FirestoreServiceProviderService manages storing, updating, deleting, and retrieving
 * service provider profiles in Firebase Firestore under the "service_providers" collection.
 */
class FirestoreServiceProviderService(
    private val firestoreSupplier: () -> FirebaseFirestore = { FirebaseFirestore.getInstance() },
    private val authSupplier: () -> FirebaseAuth = { FirebaseAuth.getInstance() }
) {

    private val COLLECTION_PROVIDERS = "service_providers"

    private val firestore: FirebaseFirestore? by lazy {
        try {
            firestoreSupplier()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Firestore initialization bypassed or not available in current environment.", e)
            null
        }
    }

    private val auth: FirebaseAuth? by lazy {
        try {
            authSupplier()
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseAuth initialization bypassed or not available in current environment.", e)
            null
        }
    }

    /**
     * Resolves the target document ID for a provider:
     * Prefers the authenticated user's UID to ensure full alignment with Firebase Auth and the users collection.
     */
    private fun resolveDocumentId(fallbackId: String?): String? {
        val currentUid = auth?.currentUser?.uid
        if (!currentUid.isNullOrBlank()) return currentUid
        if (!fallbackId.isNullOrBlank() && fallbackId != "0" && fallbackId != "1") return fallbackId
        return null
    }

    private fun String?.isNullOrBlank(): Boolean = this == null || this.trim().isEmpty()

    /**
     * Stores or updates a service provider profile using String UID (Firebase Auth UID).
     */
    suspend fun saveProviderProfileWithUid(provider: ServiceProviderEntity, uid: String): Result<Unit> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext Result.failure(Exception("Firestore instance unavailable"))
        try {
            val dto = provider.toBackendDto()
            val docRef = fs.collection(COLLECTION_PROVIDERS).document(uid)
            docRef.set(dto, SetOptions.merge()).await()
            Log.d(TAG, "Successfully saved provider profile to Firestore using UID: $uid")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving provider profile to Firestore for UID $uid", e)
            Result.failure(e)
        }
    }

    /**
     * Stores or updates a service provider profile in Firestore using the current user's Auth UID.
     */
    suspend fun saveProviderProfile(provider: ServiceProviderEntity): Result<Unit> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext Result.failure(Exception("Firestore instance unavailable"))
        val docId = resolveDocumentId(provider.id.toString())
            ?: return@withContext Result.failure(Exception("No valid Auth UID or Provider ID found."))

        try {
            val dto = provider.toBackendDto()
            val docRef = fs.collection(COLLECTION_PROVIDERS).document(docId)
            docRef.set(dto, SetOptions.merge()).await()
            Log.d(TAG, "Successfully saved provider profile to Firestore: ${provider.businessName} (Doc ID: $docId)")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving provider profile to Firestore for Doc ID $docId", e)
            Result.failure(e)
        }
    }

    /**
     * Stores or updates multiple service provider profiles in Firestore.
     */
    suspend fun saveProviderProfiles(providers: List<ServiceProviderEntity>): Result<Unit> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext Result.failure(Exception("Firestore instance unavailable"))
        try {
            providers.forEach { provider ->
                saveProviderProfile(provider)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving provider profiles to Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Stores or updates a ProviderBackendDto profile using String UID.
     */
    suspend fun saveProviderDto(dto: ProviderBackendDto, documentUid: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext Result.failure(Exception("Firestore instance unavailable"))
        val docId = documentUid ?: resolveDocumentId(dto.id.toString())
        ?: return@withContext Result.failure(Exception("No valid Auth UID or DTO ID found."))

        try {
            val docRef = fs.collection(COLLECTION_PROVIDERS).document(docId)
            docRef.set(dto, SetOptions.merge()).await()
            Log.d(TAG, "Successfully saved ProviderBackendDto to Firestore Doc ID: $docId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving ProviderBackendDto to Firestore Doc ID $docId", e)
            Result.failure(e)
        }
    }

    /**
     * Retrieves a single service provider profile from Firestore by String UID (Firebase Auth UID).
     */
    suspend fun getProviderProfileByUid(uid: String): ProviderBackendDto? = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext null
        try {
            val doc = fs.collection(COLLECTION_PROVIDERS)
                .document(uid)
                .get()
                .await()
            if (doc.exists()) {
                doc.toObject(ProviderBackendDto::class.java)
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving provider profile for UID $uid from Firestore", e)
            null
        }
    }

    /**
     * Retrieves a single service provider profile from Firestore by provider ID (supports both string UID and numeric string).
     */
    suspend fun getProviderProfile(providerId: Long): ProviderBackendDto? = withContext(Dispatchers.IO) {
        getProviderProfileByUid(providerId.toString())
    }

    /**
     * Retrieves all service provider profiles stored in Firestore.
     */
    suspend fun getAllProviderProfiles(): List<ProviderBackendDto> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext emptyList()
        try {
            val snapshot = fs.collection(COLLECTION_PROVIDERS)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(ProviderBackendDto::class.java)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving all provider profiles from Firestore", e)
            emptyList()
        }
    }

    /**
     * Retrieves service provider profiles filtered by service category.
     */
    suspend fun getProvidersByCategory(category: String): List<ProviderBackendDto> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext emptyList()
        try {
            val snapshot = fs.collection(COLLECTION_PROVIDERS)
                .whereEqualTo("serviceCategory", category)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(ProviderBackendDto::class.java)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving provider profiles by category $category from Firestore", e)
            emptyList()
        }
    }

    /**
     * Retrieves service provider profiles filtered by city.
     */
    suspend fun getProvidersByCity(city: String): List<ProviderBackendDto> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext emptyList()
        try {
            val snapshot = fs.collection(COLLECTION_PROVIDERS)
                .whereEqualTo("city", city)
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(ProviderBackendDto::class.java)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving provider profiles by city $city from Firestore", e)
            emptyList()
        }
    }

    /**
     * Deletes a service provider profile document by String UID.
     */
    suspend fun deleteProviderProfileByUid(uid: String): Result<Unit> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext Result.failure(Exception("Firestore instance unavailable"))
        try {
            fs.collection(COLLECTION_PROVIDERS)
                .document(uid)
                .delete()
                .await()
            Log.d(TAG, "Deleted provider profile UID $uid from Firestore.")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting provider profile UID $uid from Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Deletes a service provider profile document from Firestore by provider ID.
     */
    suspend fun deleteProviderProfile(providerId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        deleteProviderProfileByUid(providerId.toString())
    }

    /**
     * Real-time listener for service provider profiles as a Kotlin Flow.
     */
    fun listenToProviderProfiles(): Flow<List<ProviderBackendDto>> = callbackFlow {
        val fs = firestore
        if (fs == null) {
            close(Exception("Firestore instance unavailable"))
            return@callbackFlow
        }
        val registration = fs.collection(COLLECTION_PROVIDERS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Firestore snapshot listener error", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val providers = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ProviderBackendDto::class.java)
                    }
                    trySend(providers)
                }
            }
        awaitClose { registration.remove() }
    }

    companion object {
        private const val TAG = "FirestoreProviderSvc"
    }
}
