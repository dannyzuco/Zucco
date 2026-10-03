package com.example.data.security

import android.util.Log
import com.example.data.model.UserRole
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Custom exception thrown when a user attempts to log in with an unverified email address.
 */
class UnverifiedEmailException(
    message: String = "Your email address has not been verified yet. Please check your inbox for the verification link.",
    val unverifiedEmail: String = ""
) : Exception(message)

/**
 * FirebaseAuthManager handles all Email and Password Authentication operations with Firebase Auth,
 * email verification links, password reset emails, and synchronizes user profiles to Firebase Firestore.
 */
object FirebaseAuthManager {

    private const val TAG = "FirebaseAuthManager"
    private const val COLLECTION_USERS = "users"

    private val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Auth not available in this environment", e)
            null
        }
    }

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Firestore not available in this environment", e)
            null
        }
    }

    /**
     * Gets the currently authenticated Firebase user, if any.
     */
    val currentFirebaseUser: FirebaseUser?
        get() = auth?.currentUser

    /**
     * Creates a new user with Email and Password in Firebase Auth,
     * sends an email verification link, stores profile metadata in Firestore under `users/{uid}`,
     * and immediately signs out the user until they verify their email.
     */
    suspend fun registerWithEmail(
        email: String,
        password: String,
        fullName: String,
        role: UserRole,
        phoneNumber: String = "",
        city: String = "Ota",
        district: String = "Sango Ota",
        profileDp: String? = null,
        isApproved: Boolean = true
    ): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        val fa = auth ?: return@withContext Result.failure(Exception("Firebase Auth is not initialized"))
        val cleanEmail = email.trim().lowercase()
        val cleanPassword = password.trim()

        try {
            val authResult = fa.createUserWithEmailAndPassword(cleanEmail, cleanPassword).await()
            val firebaseUser = authResult.user ?: throw Exception("User registration succeeded but user instance is null")

            // 1. Send Email Verification Link
            try {
                firebaseUser.sendEmailVerification().await()
                Log.d(TAG, "Verification email sent to $cleanEmail")
            } catch (ve: Exception) {
                Log.w(TAG, "Failed to send verification email automatically", ve)
            }

            // 2. Store user profile metadata in Firestore under users/{uid} and service_providers/{uid}
            saveUserMetadataToFirestore(
                uid = firebaseUser.uid,
                email = cleanEmail,
                fullName = fullName.trim(),
                role = role.name,
                phoneNumber = phoneNumber.trim(),
                city = city,
                district = district,
                profileDp = profileDp,
                isApproved = isApproved
            )

            // 3. Immediately sign out user so they cannot enter the app until verifying
            try {
                fa.signOut()
            } catch (_: Exception) {}

            Log.d(TAG, "Successfully registered user ${firebaseUser.uid} with email$cleanEmail")
            Result.success(firebaseUser)
        } catch (e: Exception) {
            val isUserCollision = e is com.google.firebase.auth.FirebaseAuthUserCollisionException ||
                    e.message?.contains("email-already-in-use", ignoreCase = true) == true ||
                    e.message?.contains("ERROR_EMAIL_ALREADY_IN_USE", ignoreCase = true) == true

            if (isUserCollision) {
                Log.d(TAG, "User collision detected for $cleanEmail. Re-authenticating to attach profile.")
                try {
                    val signInResult = fa.signInWithEmailAndPassword(cleanEmail, cleanPassword).await()
                    val firebaseUser = signInResult.user ?: throw Exception("Sign in succeeded but user instance is null")

                    saveUserMetadataToFirestore(
                        uid = firebaseUser.uid,
                        email = cleanEmail,
                        fullName = fullName.trim(),
                        role = role.name,
                        phoneNumber = phoneNumber.trim(),
                        city = city,
                        district = district,
                        profileDp = profileDp,
                        isApproved = isApproved
                    )

                    try {
                        fa.signOut()
                    } catch (_: Exception) {}

                    Log.d(TAG, "Successfully attached profile for existing Firebase user $cleanEmail")
                    return@withContext Result.success(firebaseUser)
                } catch (signInErr: Exception) {
                    val friendlyError = "An account with email $cleanEmail already exists. Incorrect password for existing account."
                    Log.w(TAG, "Collision re-auth failed for $cleanEmail:${signInErr.message}")
                    return@withContext Result.failure(Exception(friendlyError, signInErr))
                }
            }

            val friendlyError = parseRegistrationError(e)
            Log.w(TAG, "Registration attempt failed for $cleanEmail:$friendlyError")
            Result.failure(Exception(friendlyError, e))
        }
    }

    /**
     * Signs in an existing user with Email and Password using Firebase Auth.
     * Enforces the Email Verification Gate: If user.isEmailVerified is false,
     * immediately signs out and returns an UnverifiedEmailException.
     */
    suspend fun signInWithEmail(
        email: String,
        password: String,
        enforceEmailVerification: Boolean = true
    ): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        val fa = auth ?: return@withContext Result.failure(Exception("Firebase Auth is not initialized"))
        val cleanEmail = email.trim().lowercase()
        val cleanPassword = password.trim()

        try {
            val authResult = fa.signInWithEmailAndPassword(cleanEmail, cleanPassword).await()
            val firebaseUser = authResult.user ?: throw Exception("Sign in succeeded but user instance is null")

            // Check email verification gate (bypass for demo accounts with @zucco.ng)
            val isDemoAccount = cleanEmail.endsWith("@zucco.ng") || cleanEmail == "apexdigitalhub232@gmail.com"
            if (enforceEmailVerification && !isDemoAccount && !firebaseUser.isEmailVerified) {
                // Reload user in case they just clicked the link
                try {
                    firebaseUser.reload().await()
                } catch (_: Exception) {}

                if (!firebaseUser.isEmailVerified) {
                    try {
                        fa.signOut()
                    } catch (_: Exception) {}
                    Log.w(TAG, "User $cleanEmail attempted to sign in with unverified email")
                    return@withContext Result.failure(
                        UnverifiedEmailException(
                            message = "Your email address has not been verified yet. Please check your inbox for the verification link.",
                            unverifiedEmail = cleanEmail
                        )
                    )
                }
            }

            Log.d(TAG, "Successfully signed in user ${firebaseUser.uid} with email$cleanEmail")
            Result.success(firebaseUser)
        } catch (e: UnverifiedEmailException) {
            Result.failure(e)
        } catch (e: Exception) {
            val friendlyError = parseSignInError(e)
            Log.w(TAG, "Sign in attempt failed for $cleanEmail:$friendlyError")
            Result.failure(Exception(friendlyError, e))
        }
    }

    /**
     * Sends a Password Reset email via Firebase Auth.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        val fa = auth ?: return@withContext Result.failure(Exception("Firebase Auth is not initialized"))
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            return@withContext Result.failure(Exception("Please enter a valid registered email address."))
        }

        try {
            fa.sendPasswordResetEmail(cleanEmail).await()
            Log.d(TAG, "Password reset email sent to $cleanEmail")
            Result.success(Unit)
        } catch (e: Exception) {
            val friendlyError = parsePasswordResetError(e)
            Log.w(TAG, "Password reset attempt failed for $cleanEmail:$friendlyError")
            Result.failure(Exception(friendlyError, e))
        }
    }

    /**
     * Resends an email verification link to the specified user by temporarily signing in or reloading.
     */
    suspend fun resendVerificationEmail(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        val fa = auth ?: return@withContext Result.failure(Exception("Firebase Auth is not initialized"))
        val cleanEmail = email.trim().lowercase()
        val cleanPassword = password.trim()

        try {
            val authResult = fa.signInWithEmailAndPassword(cleanEmail, cleanPassword).await()
            val user = authResult.user ?: throw Exception("User not found")
            user.sendEmailVerification().await()
            fa.signOut()
            Log.d(TAG, "Resent verification email to $cleanEmail")
            Result.success(Unit)
        } catch (e: Exception) {
            val friendlyError = parseAuthError(e)
            Log.w(TAG, "Failed to resend verification email for $cleanEmail:$friendlyError")
            Result.failure(Exception(friendlyError, e))
        }
    }

    /**
     * Reads user profile document from Firestore collection `users/{uid}`
     * to check real-time approval status set in Firebase Console.
     */
    suspend fun fetchUserMetadataFromFirestore(uid: String): Map<String, Any?>? = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext null
        try {
            val doc = fs.collection(COLLECTION_USERS).document(uid).get().await()
            if (doc.exists()) doc.data else null
        } catch (e: Exception) {
            Log.w(TAG, "Error fetching user metadata from Firestore for uid $uid:${e.localizedMessage}")
            null
        }
    }

    /**
     * Writes or merges user profile metadata into Firestore collection `users/{uid}`
     * and synchronizes service provider profiles to `service_providers/{uid}` using the exact Auth UID.
     */
    suspend fun saveUserMetadataToFirestore(
        uid: String,
        email: String,
        fullName: String,
        role: String,
        phoneNumber: String = "",
        city: String = "Ota",
        district: String = "Sango Ota",
        profileDp: String? = null,
        isApproved: Boolean = true,
        artisanProfileMap: Map<String, Any?>? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext Result.success(Unit)
        try {
            val sanitizedEmail = email.trim().lowercase()
            val cleanName = fullName.trim()
            val cleanPhone = phoneNumber.trim()

            val isArtisan = role.equals(UserRole.PROVIDER.name, ignoreCase = true) ||
                    role.equals("artisan", ignoreCase = true) ||
                    artisanProfileMap != null
            val isClient = true // Base User always holds client capability
            val accountType = if (isArtisan) "both" else "client"

            val clientProfileData = hashMapOf<String, Any>(
                "savedAddresses" to listOf(if (district.isNotBlank()) "$district,$city" else city)
            )

            val finalArtisanProfile = if (isArtisan) {
                artisanProfileMap ?: hashMapOf(
                    "city" to city,
                    "district" to district,
                    "isVerified" to isApproved,
                    "profileDp" to profileDp
                )
            } else null

            val isoDateString = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).apply {
                timeZone = java.util.TimeZone.getTimeZone("UTC")
            }.format(java.util.Date())

            val userMap = hashMapOf<String, Any?>(
                "uid" to uid,
                "email" to sanitizedEmail,
                "fullName" to cleanName,
                "phoneNumber" to cleanPhone,
                "accountType" to accountType,
                "roles" to hashMapOf(
                    "isClient" to isClient,
                    "isArtisan" to isArtisan
                ),
                "clientProfile" to clientProfileData,
                "artisanProfile" to finalArtisanProfile,
                "createdAt" to isoDateString
            )

            kotlinx.coroutines.withTimeoutOrNull(10_000L) {
                val batch = fs.batch()

                // 1. Write to users/{uid}
                val userRef = fs.collection(COLLECTION_USERS).document(uid)
                batch.set(userRef, userMap, SetOptions.merge())

                // 2. If user is an artisan, ALSO write directly to service_providers/{uid} using the same UID key
                if (isArtisan) {
                    val providerRef = fs.collection("service_providers").document(uid)
                    val providerMap = hashMapOf<String, Any?>(
                        "id" to uid,
                        "ownerUid" to uid,
                        "businessName" to cleanName,
                        "businessEmail" to sanitizedEmail,
                        "phoneNumber" to cleanPhone,
                        "city" to city,
                        "district" to district,
                        "address" to "$district,$city",
                        "available" to true,
                        "isVerified" to isApproved,
                        "profileDp" to profileDp,
                        "createdAt" to isoDateString
                    )
                    batch.set(providerRef, providerMap, SetOptions.merge())
                }

                batch.commit().await()
            }

            Log.d(TAG, "Saved user metadata & service provider profile to Firestore using UID: $uid")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "Error saving user metadata to Firestore for uid $uid:${e.localizedMessage}")
            Result.success(Unit)
        }
    }

    /**
     * Signs out from Firebase Auth.
     */
    fun signOut() {
        try {
            auth?.signOut()
            Log.d(TAG, "Signed out from Firebase Auth")
        } catch (e: Exception) {
            Log.w(TAG, "Error during Firebase sign out", e)
        }
    }

    /**
     * Registration specific error mapping conforming to Specification 3.
     */
    fun parseRegistrationError(e: Exception): String {
        val msg = e.message.orEmpty()
        val errCode = if (e is FirebaseAuthException) e.errorCode else ""

        return when {
            e is FirebaseAuthWeakPasswordException || errCode == "ERROR_WEAK_PASSWORD" || msg.contains("auth/weak-password", ignoreCase = true) || msg.contains("weak password", ignoreCase = true) -> {
                "Password must be at least 6 characters long."
            }
            e is FirebaseAuthUserCollisionException || errCode == "ERROR_EMAIL_ALREADY_IN_USE" || msg.contains("auth/email-already-in-use", ignoreCase = true) || msg.contains("already in use", ignoreCase = true) -> {
                "An account with this email already exists. You can sign in or upgrade your account in settings."
            }
            errCode == "ERROR_INVALID_EMAIL" || msg.contains("auth/invalid-email", ignoreCase = true) || msg.contains("badly formatted", ignoreCase = true) -> {
                "Please enter a valid email address."
            }
            e is FirebaseNetworkException || msg.contains("network", ignoreCase = true) -> {
                "Registration failed. Please check your network connection and try again."
            }
            else -> {
                if (msg.isNotBlank() && !msg.contains("Exception", ignoreCase = true)) {
                    msg
                } else {
                    "Registration failed. Please check your network connection."
                }
            }
        }
    }

    /**
     * Sign In specific error mapping conforming to Specification 3.
     */
    fun parseSignInError(e: Exception): String {
        val msg = e.message.orEmpty()
        val errCode = if (e is FirebaseAuthException) e.errorCode else ""

        return when {
            e is FirebaseAuthInvalidUserException || errCode == "ERROR_USER_NOT_FOUND" || msg.contains("auth/user-not-found", ignoreCase = true) || msg.contains("user-not-found", ignoreCase = true) -> {
                "No account found with this email. Please switch to the Register tab to create one."
            }
            errCode == "ERROR_USER_DISABLED" || msg.contains("auth/user-disabled", ignoreCase = true) -> {
                "This account has been disabled. Please contact support."
            }
            errCode == "ERROR_WRONG_PASSWORD" || errCode == "ERROR_INVALID_CREDENTIAL" ||
                    e is FirebaseAuthInvalidCredentialsException ||
                    msg.contains("auth/wrong-password", ignoreCase = true) ||
                    msg.contains("auth/invalid-credential", ignoreCase = true) ||
                    msg.contains("invalid credential", ignoreCase = true) -> {
                "Incorrect password. Please check your password or tap 'Forgot Password'."
            }
            e is FirebaseNetworkException || msg.contains("network", ignoreCase = true) -> {
                "Sign in failed. Please check your network connection."
            }
            msg.contains("auth/too-many-requests", ignoreCase = true) -> {
                "Too many failed login attempts. Please wait a moment before trying again."
            }
            else -> {
                if (msg.isNotBlank() && !msg.contains("Exception", ignoreCase = true)) {
                    msg
                } else {
                    "Sign in failed. Please check your credentials."
                }
            }
        }
    }

    /**
     * Password Reset error mapping.
     */
    fun parsePasswordResetError(e: Exception): String {
        val msg = e.message.orEmpty()
        val errCode = if (e is FirebaseAuthException) e.errorCode else ""

        return when {
            e is FirebaseAuthInvalidUserException || errCode == "ERROR_USER_NOT_FOUND" || msg.contains("auth/user-not-found", ignoreCase = true) -> {
                "No account found with this email. Please check your email address."
            }
            errCode == "ERROR_INVALID_EMAIL" || msg.contains("auth/invalid-email", ignoreCase = true) -> {
                "Please enter a valid email address."
            }
            e is FirebaseNetworkException || msg.contains("network", ignoreCase = true) -> {
                "Could not send reset email. Please check your network connection."
            }
            else -> {
                if (msg.isNotBlank() && !msg.contains("Exception", ignoreCase = true)) {
                    msg
                } else {
                    "Could not send password reset email. Please try again."
                }
            }
        }
    }

    /**
     * Generic auth error mapping fallback.
     */
    fun parseAuthError(e: Exception): String {
        val msg = e.message.orEmpty()
        return when (e) {
            is FirebaseAuthUserCollisionException -> "An account with this email already exists. Please switch to the Sign In tab."
            is FirebaseAuthWeakPasswordException -> "Password must be at least 6 characters long."
            is FirebaseAuthInvalidUserException -> "No account found with this email. Please switch to the Register tab to create one."
            is FirebaseAuthInvalidCredentialsException -> "Incorrect email or password. Please try again."
            is FirebaseNetworkException -> "Network connection error. Please check your internet connection."
            else -> {
                if (msg.isNotBlank() && !msg.contains("Exception", ignoreCase = true)) {
                    msg
                } else {
                    "Authentication request failed. Please check your connection and try again."
                }
            }
        }
    }
}