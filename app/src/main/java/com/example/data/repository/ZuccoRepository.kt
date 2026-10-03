package com.example.data.repository

import android.content.Context
import com.example.data.local.BookingDao
import com.example.data.local.InitialSeedData
import com.example.data.local.ReviewDao
import com.example.data.local.ServiceProviderDao
import com.example.data.local.UserDao
import com.example.data.local.ZuccoDatabase
import com.example.data.model.BookingRequestEntity
import com.example.data.model.BookingStatus
import com.example.data.model.LocationArea
import com.example.data.model.LocationCatalog
import com.example.data.model.ReviewEntity
import com.example.data.model.ServiceProviderEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.security.FirebaseAuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ZuccoRepository(
    private val context: Context,
    private val userDao: UserDao,
    private val providerDao: ServiceProviderDao,
    private val bookingDao: BookingDao,
    private val reviewDao: ReviewDao,
    private val backendService: com.example.data.backend.ServiceProviderBackendService = com.example.data.backend.ServiceProviderBackendService(context)
) {
    private val prefs = context.getSharedPreferences("zucco_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _userLocation = MutableStateFlow(LocationCatalog.defaultUserLocation)
    val userLocation: StateFlow<LocationArea> = _userLocation.asStateFlow()

    init {
        // In Android runtime, the Application or ViewModel ensures seeding
    }

    fun isFirstInstall(): Boolean {
        return prefs.getBoolean("is_first_install", true)
    }

    fun markFirstInstallCompleted() {
        prefs.edit().putBoolean("is_first_install", false).apply()
    }

    suspend fun restoreSavedUserSession() {
        val lastUserId = prefs.getLong("logged_in_user_id", -1L)
        if (lastUserId > 0) {
            val fbUser = FirebaseAuthManager.currentFirebaseUser
            if (fbUser != null) {
                val firestoreMap = FirebaseAuthManager.fetchUserMetadataFromFirestore(fbUser.uid)
                if (firestoreMap != null) {
                    val isApprovedInFs = (firestoreMap["isApproved"] == true) || 
                            (firestoreMap["isVerified"] == true) || 
                            ((firestoreMap["artisanProfile"] as? Map<*, *>)?.get("isVerified") == true) ||
                            ((firestoreMap["artisanProfile"] as? Map<*, *>)?.get("isApproved") == true)
                    
                    if (isApprovedInFs) {
                        val existingUser = userDao.getUserDirect(lastUserId)
                        if (existingUser != null) {
                            userDao.updateUser(existingUser.copy(isApproved = true))
                            val prov = providerDao.getProviderByUserIdDirect(existingUser.id)
                            if (prov != null) {
                                providerDao.updateProvider(prov.copy(isVerified = true))
                            }
                        }
                    }
                }
            }
            val existing = userDao.getUserDirect(lastUserId)
            _currentUser.value = existing
        }
    }

    suspend fun seedDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        // Clean up and wipe all legacy test/demo data to prepare for production launch
        userDao.deleteDemoUsers()
        providerDao.deleteDemoProviders()
    }

    fun getAllProviders(): Flow<List<ServiceProviderEntity>> = providerDao.getAllProviders()

    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    fun getProviderById(id: Long): Flow<ServiceProviderEntity?> = providerDao.getProviderById(id)

    fun getProviderByUserId(userId: Long): Flow<ServiceProviderEntity?> = providerDao.getProviderByUserId(userId)

    fun getSavedProviders(): Flow<List<ServiceProviderEntity>> = providerDao.getSavedProviders()

    suspend fun getBackendProviderRecords(): List<com.example.data.backend.ProviderBackendDto> {
        return backendService.getAllProvidersFromBackend()
    }

    suspend fun getBackendUserRecords(): List<com.example.data.backend.UserBackendDto> {
        return backendService.getAllUsersFromBackend()
    }

    suspend fun getBackendSyncStatus(): com.example.data.backend.BackendSyncStatus {
        return backendService.getBackendSyncStatus()
    }

    suspend fun forceCloudSync(): com.example.data.backend.BackendSyncStatus = withContext(Dispatchers.IO) {
        val users = userDao.getAllUsersDirect()
        val providers = providerDao.getAllProvidersDirect()
        backendService.saveAllUsersToBackend(users)
        backendService.saveAllProvidersToBackend(providers)
        backendService.getBackendSyncStatus()
    }

    fun getReviewsForProvider(providerId: Long): Flow<List<ReviewEntity>> =
        reviewDao.getReviewsForProvider(providerId)

    fun getBookingsForClient(clientId: Long): Flow<List<BookingRequestEntity>> =
        bookingDao.getBookingsForClient(clientId)

    fun getBookingsForProvider(providerId: Long): Flow<List<BookingRequestEntity>> =
        bookingDao.getBookingsForProvider(providerId)

    fun getAllBookings(): Flow<List<BookingRequestEntity>> = bookingDao.getAllBookings()

    fun setUserLocation(location: LocationArea) {
        _userLocation.value = location
    }

    suspend fun toggleSavedStatus(id: Long, currentStatus: Boolean) = withContext(Dispatchers.IO) {
        providerDao.updateSavedStatus(id, !currentStatus)
    }

    suspend fun updateAvailability(id: Long, isAvailable: Boolean) = withContext(Dispatchers.IO) {
        providerDao.updateAvailability(id, isAvailable)
        val prov = providerDao.getProviderByIdDirect(id)
        if (prov != null) {
            backendService.saveProviderToBackend(prov)
        }
    }

    suspend fun updateVerifiedStatus(id: Long, isVerified: Boolean) = withContext(Dispatchers.IO) {
        providerDao.updateVerifiedStatus(id, isVerified)
        val prov = providerDao.getProviderByIdDirect(id)
        if (prov != null) {
            backendService.saveProviderToBackend(prov)
        }
    }

    suspend fun deleteProvider(id: Long) = withContext(Dispatchers.IO) {
        providerDao.deleteProvider(id)
        backendService.deleteProviderFromBackend(id)
    }

    suspend fun deleteUser(id: Long) = withContext(Dispatchers.IO) {
        userDao.deleteUser(id)
        backendService.deleteUserFromBackend(id)
    }

    suspend fun updatePassword(userId: Long, newPassword: String) = withContext(Dispatchers.IO) {
        userDao.updatePassword(userId, newPassword)
        val current = _currentUser.value
        if (current?.id == userId) {
            _currentUser.value = current.copy(password = newPassword)
        }
    }

    suspend fun updateUserProfilePhoto(userId: Long, photoUri: String) = withContext(Dispatchers.IO) {
        val user = userDao.getUserDirect(userId)
        if (user != null) {
            val updatedUser = user.copy(profileDp = photoUri)
            userDao.updateUser(updatedUser)
            if (_currentUser.value?.id == userId) {
                _currentUser.value = updatedUser
            }

            // Also update provider profileDp if user is an artisan
            val provider = providerDao.getProviderByUserIdDirect(userId)
            if (provider != null) {
                val updatedProvider = provider.copy(profileDp = photoUri)
                providerDao.updateProvider(updatedProvider)
                backendService.saveProviderToBackend(updatedProvider)
            }

            // Sync update to Firebase Firestore
            val fbUser = FirebaseAuthManager.currentFirebaseUser
            if (fbUser != null) {
                FirebaseAuthManager.saveUserMetadataToFirestore(
                    uid = fbUser.uid,
                    email = updatedUser.email,
                    fullName = updatedUser.fullName,
                    role = updatedUser.role.name,
                    phoneNumber = updatedUser.phoneNumber,
                    city = updatedUser.city,
                    district = updatedUser.district,
                    profileDp = photoUri,
                    isApproved = updatedUser.isApproved
                )
            }
        }
    }

    suspend fun updatePortfolioPhotos(
        providerId: Long,
        photo1: String?,
        photo2: String?,
        photo3: String?
    ) = withContext(Dispatchers.IO) {
        providerDao.updatePortfolioPhotos(providerId, photo1, photo2, photo3)
        val prov = providerDao.getProviderByIdDirect(providerId)
        if (prov != null) {
            backendService.saveProviderToBackend(prov)
        }
    }

    suspend fun updateProviderProfile(provider: ServiceProviderEntity) = withContext(Dispatchers.IO) {
        providerDao.updateProvider(provider)
        backendService.saveProviderToBackend(provider)
    }

    suspend fun saveServiceProviderProfileDirect(provider: ServiceProviderEntity) = withContext(Dispatchers.IO) {
        providerDao.insertProvider(provider)
        backendService.saveProviderToBackend(provider)
    }

    /**
     * Admin login: strictly apexdigitalhub232@gmail.com and 08120942172 with Lyricaltymer1991*
     */
    suspend fun loginAdmin(identifier: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanId = identifier.trim().lowercase()
        val cleanPass = password.trim()

        val isAuthorizedAdmin = (cleanId == "apexdigitalhub232@gmail.com" || cleanId == "08120942172") && cleanPass == "Lyricaltymer1991*"

        if (!isAuthorizedAdmin) {
            return@withContext Result.failure(Exception("Access denied: Invalid Admin credentials."))
        }

        // Return or create Admin session
        val adminUser = UserEntity(
            id = 999999,
            email = "apexdigitalhub232@gmail.com",
            password = cleanPass,
            fullName = "Zucco Administrator",
            phoneNumber = "08120942172",
            role = UserRole.ADMIN,
            username = "admin_apex",
            city = "Lagos",
            district = "Ikeja"
        )
        _currentUser.value = adminUser
        markFirstInstallCompleted()
        Result.success(adminUser)
    }

    suspend fun login(identifier: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanId = identifier.trim().lowercase()
        val cleanPass = password.trim()

        // Check if admin is attempting normal sign in
        if ((cleanId == "apexdigitalhub232@gmail.com" || cleanId == "08120942172") && cleanPass == "Lyricaltymer1991*") {
            return@withContext loginAdmin(cleanId, cleanPass)
        }

        // Demo seed user fast-path (e.g. adeola@zucco.ng, client@zucco.ng, kemi@zucco.ng)
        val matchingSeed = InitialSeedData.seedUsers.firstOrNull {
            it.email.equals(cleanId, ignoreCase = true) || it.phoneNumber == cleanId
        }
        if (matchingSeed != null) {
            if (cleanPass == "password" || cleanPass == matchingSeed.password) {
                userDao.insertUser(matchingSeed)
                _currentUser.value = matchingSeed
                prefs.edit().putLong("logged_in_user_id", matchingSeed.id).apply()
                markFirstInstallCompleted()
                return@withContext Result.success(matchingSeed)
            } else {
                return@withContext Result.failure(Exception("Incorrect password. Please try again or tap 'Forgot Password'."))
            }
        }

        // Check if account exists in local database
        val existingLocalUser = userDao.findByEmail(cleanId) ?: userDao.findByIdentifier(cleanId)

        // 1. Attempt Firebase Auth sign-in with Email & Password
        if (cleanId.contains("@")) {
            val fbResult = FirebaseAuthManager.signInWithEmail(cleanId, cleanPass)
            if (fbResult.isSuccess) {
                val fbUser = fbResult.getOrNull()
                var user = existingLocalUser

                // Read live approval status directly from Firebase Firestore document /users/{uid}
                if (fbUser != null) {
                    val firestoreMap = FirebaseAuthManager.fetchUserMetadataFromFirestore(fbUser.uid)
                    if (firestoreMap != null) {
                        val isApprovedInFs = (firestoreMap["isApproved"] == true) || 
                                (firestoreMap["isVerified"] == true) || 
                                ((firestoreMap["artisanProfile"] as? Map<*, *>)?.get("isVerified") == true) ||
                                ((firestoreMap["artisanProfile"] as? Map<*, *>)?.get("isApproved") == true)
                        
                        if (isApprovedInFs && user != null) {
                            user = user.copy(isApproved = true)
                            userDao.updateUser(user)
                            val prov = providerDao.getProviderByUserIdDirect(user.id)
                            if (prov != null) {
                                providerDao.updateProvider(prov.copy(isVerified = true))
                            }
                        }
                    }
                }

                if (user == null && fbUser != null) {
                    val newUser = UserEntity(
                        email = cleanId,
                        password = cleanPass,
                        fullName = fbUser.displayName ?: cleanId.substringBefore("@").replace(".", " ").capitalize(),
                        phoneNumber = fbUser.phoneNumber.orEmpty(),
                        role = UserRole.CLIENT,
                        city = "Ota",
                        district = "Sango Ota",
                        isApproved = true
                    )
                    val newId = userDao.insertUser(newUser)
                    user = newUser.copy(id = newId)
                }
                if (user != null) {
                    _currentUser.value = user
                    prefs.edit().putLong("logged_in_user_id", user.id).apply()
                    markFirstInstallCompleted()
                    return@withContext Result.success(user)
                }
            } else {
                val ex = fbResult.exceptionOrNull()
                if (ex is com.example.data.security.UnverifiedEmailException) {
                    return@withContext Result.failure(ex)
                }

                val errMsg = ex?.message.orEmpty()
                // If Firebase reports wrong password or invalid credential, return password error immediately
                if (errMsg.contains("password", ignoreCase = true) || errMsg.contains("credential", ignoreCase = true)) {
                    return@withContext Result.failure(Exception("Incorrect password. Please try again or tap 'Forgot Password'."))
                }

                // If account exists locally, failure is due to password mismatch
                if (existingLocalUser != null) {
                    return@withContext Result.failure(Exception("Incorrect password. Please try again or tap 'Forgot Password'."))
                }

                // Only if account is not found anywhere
                if (errMsg.contains("No account found", ignoreCase = true)) {
                    return@withContext Result.failure(Exception("No account found with this email. Please switch to the Register tab to create one."))
                }

                return@withContext Result.failure(Exception(errMsg.ifBlank { "Sign in failed. Please check your credentials." }))
            }
        }

        // 2. Lookup user in local Room database
        if (existingLocalUser != null) {
            if (existingLocalUser.password == cleanPass) {
                if (!existingLocalUser.isApproved && existingLocalUser.role != UserRole.ADMIN) {
                    return@withContext Result.failure(Exception("Account pending approval: An administrator must review and approve your account before you can log in."))
                }
                _currentUser.value = existingLocalUser
                prefs.edit().putLong("logged_in_user_id", existingLocalUser.id).apply()
                markFirstInstallCompleted()
                return@withContext Result.success(existingLocalUser)
            } else {
                return@withContext Result.failure(Exception("Incorrect password. Please try again or tap 'Forgot Password'."))
            }
        }

        Result.failure(Exception("No account found with this email. Please switch to the Register tab to create one."))
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        FirebaseAuthManager.sendPasswordResetEmail(email)
    }

    suspend fun resendVerificationEmail(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        FirebaseAuthManager.resendVerificationEmail(email, password)
    }

    suspend fun registerClient(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        city: String,
        district: String,
        profileDp: String? = null
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val cleanPhone = phone.trim()
        val cleanPassword = password.trim()

        val existingUser = userDao.findByEmail(cleanEmail)
        if (existingUser != null) {
            if (existingUser.password == cleanPassword) {
                _currentUser.value = existingUser
                prefs.edit().putLong("logged_in_user_id", existingUser.id).apply()
                return@withContext Result.success(existingUser)
            } else {
                val fbTest = FirebaseAuthManager.signInWithEmail(cleanEmail, cleanPassword)
                if (fbTest.isFailure) {
                    return@withContext Result.failure(Exception("An account with email $cleanEmail already exists. Please enter your existing account password."))
                }
            }
        }

        // Create account in Firebase Auth & store user profile in Firestore
        val fbResult = FirebaseAuthManager.registerWithEmail(
            email = cleanEmail,
            password = cleanPassword,
            fullName = fullName.trim(),
            role = UserRole.CLIENT,
            phoneNumber = cleanPhone,
            city = city,
            district = district,
            profileDp = profileDp,
            isApproved = true
        )
        if (fbResult.isFailure && existingUser == null) {
            val err = fbResult.exceptionOrNull()?.message ?: "Registration failed"
            return@withContext Result.failure(Exception(err))
        }

        val userEntityToSave = existingUser?.copy(
            fullName = fullName.trim().ifBlank { existingUser.fullName },
            phoneNumber = cleanPhone.ifBlank { existingUser.phoneNumber },
            city = city,
            district = district,
            profileDp = profileDp ?: existingUser.profileDp
        ) ?: UserEntity(
            email = cleanEmail,
            password = cleanPassword,
            fullName = fullName.trim(),
            phoneNumber = cleanPhone,
            role = UserRole.CLIENT,
            city = city,
            district = district,
            profileDp = profileDp,
            isApproved = true
        )

        val id = if (existingUser != null) {
            userDao.updateUser(userEntityToSave)
            existingUser.id
        } else {
            userDao.insertUser(userEntityToSave)
        }

        val created = userEntityToSave.copy(id = id)
        _currentUser.value = created
        prefs.edit().putLong("logged_in_user_id", created.id).apply()
        backendService.saveUserToBackend(created)
        markFirstInstallCompleted()
        Result.success(created)
    }

    suspend fun registerServiceProvider(
        fullName: String,
        email: String,
        phone: String,
        whatsapp: String = phone,
        password: String,
        username: String,
        businessName: String,
        category: String,
        city: String,
        district: String,
        address: String,
        description: String,
        pricing: String,
        experienceYears: Int,
        businessRegNo: String?,
        businessEmail: String?,
        openingHours: String,
        servicesOffered: String?,
        areasCovered: String? = null,
        googleMapLink: String? = null,
        profileDp: String? = null,
        photo1: String?,
        photo2: String?,
        photo3: String?
    ): Result<Pair<UserEntity, ServiceProviderEntity>> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val cleanPhone = phone.trim()
        val cleanWhatsapp = whatsapp.trim().replace("+", "").replace("-", "").replace(" ", "")
        val cleanUsername = username.trim().lowercase().replace(" ", "_")
        val cleanPassword = password.trim()

        val existingUser = userDao.findByEmail(cleanEmail)

        // 1. Check duplicate username across other accounts
        val existingUsernameUser = userDao.findByUsername(cleanUsername)
        val existingUsernameProvider = providerDao.findByUsername(cleanUsername)
        if (existingUsernameUser != null && existingUsernameUser.id != existingUser?.id) {
            return@withContext Result.failure(Exception("The username '@$cleanUsername' is already taken. Please pick a different unique username for your business."))
        }
        if (existingUsernameProvider != null && existingUsernameProvider.userId != existingUser?.id) {
            return@withContext Result.failure(Exception("The username '@$cleanUsername' is already taken. Please pick a different unique username for your business."))
        }

        if (existingUser != null && existingUser.password != cleanPassword) {
            val fbTest = FirebaseAuthManager.signInWithEmail(cleanEmail, cleanPassword)
            if (fbTest.isFailure) {
                return@withContext Result.failure(Exception("An account with email $cleanEmail already exists. Incorrect password for existing account."))
            }
        }

        // Every artisan who registers must first be approved by an administrator
        val isApproved = existingUser?.isApproved ?: false
        val isVerified = false

        // Create or update account in Firebase Auth & store user profile in Firestore
        val fbResult = FirebaseAuthManager.registerWithEmail(
            email = cleanEmail,
            password = cleanPassword,
            fullName = fullName.trim(),
            role = UserRole.PROVIDER,
            phoneNumber = cleanPhone,
            city = city,
            district = district,
            profileDp = profileDp,
            isApproved = isApproved
        )
        if (fbResult.isFailure && existingUser == null) {
            val err = fbResult.exceptionOrNull()?.message ?: "Registration failed"
            return@withContext Result.failure(Exception(err))
        }

        val userEntityToSave = if (existingUser != null) {
            existingUser.copy(
                fullName = fullName.trim().ifBlank { existingUser.fullName },
                phoneNumber = cleanPhone.ifBlank { existingUser.phoneNumber },
                role = UserRole.PROVIDER,
                username = cleanUsername,
                city = city,
                district = district,
                isApproved = isApproved
            )
        } else {
            UserEntity(
                email = cleanEmail,
                password = cleanPassword,
                fullName = fullName.trim(),
                phoneNumber = cleanPhone,
                role = UserRole.PROVIDER,
                username = cleanUsername,
                city = city,
                district = district,
                isApproved = false
            )
        }

        val userId = if (existingUser != null) {
            userDao.updateUser(userEntityToSave)
            existingUser.id
        } else {
            userDao.insertUser(userEntityToSave)
        }
        val createdUser = userEntityToSave.copy(id = userId)

        // Find approx coordinates for city/district
        val locationMatch = LocationCatalog.predefinedLocations.find {
            it.name.contains(district, ignoreCase = true) || district.contains(it.name, ignoreCase = true)
        } ?: if (city.equals("Ota", ignoreCase = true)) LocationCatalog.predefinedLocations[0] else LocationCatalog.predefinedLocations[6]

        val existingProvider = providerDao.getProviderByUserIdDirect(userId)

        val newProvider = ServiceProviderEntity(
            id = existingProvider?.id ?: 0L,
            userId = userId,
            username = cleanUsername,
            businessName = businessName.trim(),
            providerName = fullName.trim(),
            serviceCategory = category,
            city = city,
            district = district,
            address = address.trim(),
            phoneNumber = cleanPhone,
            whatsappNumber = cleanWhatsapp.ifBlank { cleanPhone },
            description = description.trim(),
            pricingInfo = pricing.trim(),
            yearsOfExperience = kotlin.math.max(1, kotlin.math.abs(experienceYears)),
            businessRegNo = businessRegNo?.trim()?.ifBlank { null },
            businessEmail = businessEmail?.trim()?.ifBlank { null },
            openingHours = openingHours.trim().ifBlank { "Mon - Sat: 8:00 AM - 6:00 PM" },
            servicesOffered = servicesOffered?.trim()?.ifBlank { null },
            areasCovered = areasCovered?.trim()?.ifBlank { null },
            googleMapLink = googleMapLink?.trim()?.ifBlank { null },
            rating = existingProvider?.rating ?: 5.0,
            reviewCount = existingProvider?.reviewCount ?: 1,
            isVerified = existingProvider?.isVerified ?: false,
            isAvailable = true,
            profileDp = profileDp ?: existingProvider?.profileDp,
            photo1 = photo1 ?: existingProvider?.photo1,
            photo2 = photo2 ?: existingProvider?.photo2,
            photo3 = photo3 ?: existingProvider?.photo3,
            latitude = locationMatch.latitude,
            longitude = locationMatch.longitude,
            syncedToCloudBackend = true
        )

        val providerId = providerDao.insertProvider(newProvider)
        val createdProvider = newProvider.copy(id = providerId)

        _currentUser.value = createdUser
        prefs.edit().putLong("logged_in_user_id", createdUser.id).apply()
        backendService.saveUserToBackend(createdUser)
        backendService.saveProviderToBackend(createdProvider)
        markFirstInstallCompleted()
        Result.success(Pair(createdUser, createdProvider))
    }

    suspend fun toggleProviderVerification(providerId: Long, isVerified: Boolean) = withContext(Dispatchers.IO) {
        providerDao.updateVerifiedStatus(providerId, isVerified)
        val provider = providerDao.getProviderByIdDirect(providerId)
        if (provider != null) {
            val updated = provider.copy(isVerified = isVerified)
            backendService.saveProviderToBackend(updated)
        }
    }

    suspend fun adminCreateUser(
        fullName: String,
        email: String,
        phone: String,
        role: UserRole,
        username: String?,
        city: String,
        district: String,
        isApproved: Boolean = true
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim()
        val cleanPhone = phone.trim()

        val existingEmail = userDao.findByEmail(cleanEmail)
        if (existingEmail != null) {
            return@withContext Result.failure(Exception("Email '$cleanEmail' already exists."))
        }
        val existingPhone = userDao.findByPhone(cleanPhone)
        if (existingPhone != null) {
            return@withContext Result.failure(Exception("Phone '$cleanPhone' already exists."))
        }

        val newUser = UserEntity(
            email = cleanEmail,
            fullName = fullName.trim(),
            phoneNumber = cleanPhone,
            role = role,
            username = username?.trim()?.lowercase(),
            city = city,
            district = district,
            isApproved = isApproved
        )
        val id = userDao.insertUser(newUser)
        val created = newUser.copy(id = id)
        backendService.saveUserToBackend(created)
        Result.success(created)
    }

    suspend fun setUserApproval(userId: Long, isApproved: Boolean) = withContext(Dispatchers.IO) {
        userDao.updateUserApproval(userId, isApproved)
        val user = userDao.getUserDirect(userId)
        if (user != null) {
            backendService.saveUserToBackend(user)
        }
    }

    suspend fun createBooking(
        provider: ServiceProviderEntity,
        issueDescription: String,
        scheduledDate: String,
        urgency: String
    ): Result<Long> = withContext(Dispatchers.IO) {
        val user = _currentUser.value
        val clientId = user?.id ?: 1L
        val clientName = user?.fullName ?: "Guest Customer"
        val clientPhone = user?.phoneNumber ?: "08000000000"
        val clientLoc = user?.district ?: _userLocation.value.name

        val booking = BookingRequestEntity(
            clientId = clientId,
            clientName = clientName,
            clientPhone = clientPhone,
            clientLocation = clientLoc,
            providerId = provider.id,
            providerBusinessName = provider.businessName,
            serviceCategory = provider.serviceCategory,
            issueDescription = issueDescription,
            scheduledDate = scheduledDate,
            urgency = urgency,
            status = BookingStatus.PENDING
        )
        val id = bookingDao.insertBooking(booking)
        Result.success(id)
    }

    suspend fun updateBookingStatus(id: Long, status: BookingStatus) = withContext(Dispatchers.IO) {
        bookingDao.updateBookingStatus(id, status)
    }

    suspend fun addReview(providerId: Long, rating: Float, comment: String): Result<Long> = withContext(Dispatchers.IO) {
        val user = _currentUser.value
        val name = user?.fullName ?: "Satisfied Client"
        val review = ReviewEntity(
            providerId = providerId,
            clientName = name,
            rating = rating,
            comment = comment,
            date = "Just now"
        )
        val id = reviewDao.insertReview(review)
        val allReviews = reviewDao.getReviewsForProviderDirect(providerId)
        if (allReviews.isNotEmpty()) {
            val avg = allReviews.map { it.rating.toDouble() }.average()
            val roundedAvg = kotlin.math.round(avg * 10.0) / 10.0
            providerDao.updateRating(providerId, roundedAvg, allReviews.size)
        }
        Result.success(id)
    }

    fun logout() {
        FirebaseAuthManager.signOut()
        _currentUser.value = null
        prefs.edit().remove("logged_in_user_id").apply()
    }

    fun switchUser(user: UserEntity) {
        _currentUser.value = user
        prefs.edit().putLong("logged_in_user_id", user.id).apply()
        markFirstInstallCompleted()
    }

    companion object {
        @Volatile
        private var INSTANCE: ZuccoRepository? = null

        fun getInstance(context: Context): ZuccoRepository {
            return INSTANCE ?: synchronized(this) {
                val db = ZuccoDatabase.getDatabase(context)
                val instance = ZuccoRepository(
                    context = context,
                    userDao = db.userDao(),
                    providerDao = db.serviceProviderDao(),
                    bookingDao = db.bookingDao(),
                    reviewDao = db.reviewDao()
                )
                INSTANCE = instance
                instance
            }
        }
    }
}
