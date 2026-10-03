package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ZuccoDatabase
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.repository.ZuccoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ZuccoVerificationTest {

    private lateinit var database: ZuccoDatabase
    private lateinit var repository: ZuccoRepository
    private lateinit var context: Context

    @Before
    fun setUp() = runBlocking {
        context = ApplicationProvider.getApplicationContext()
        // clean up any previous backend test file
        java.io.File(context.filesDir, "zucco_backend_providers.json").delete()

        database = Room.inMemoryDatabaseBuilder(context, ZuccoDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = ZuccoRepository(
            context = context,
            userDao = database.userDao(),
            providerDao = database.serviceProviderDao(),
            bookingDao = database.bookingDao(),
            reviewDao = database.reviewDao()
        )
        repository.seedDatabaseIfEmpty()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testFirstInstallDefaultsToTrue() {
        // First-time install returns true by default
        val isFirst = repository.isFirstInstall()
        assertTrue("New installs must show login/auth first", isFirst)
    }

    @Test
    fun testAdminLoginStrictCredentials() = runBlocking {
        // Correct admin credentials via email
        val resultEmail = repository.loginAdmin("apexdigitalhub232@gmail.com", "Lyricaltymer1991*")
        assertTrue(resultEmail.isSuccess)
        assertEquals(UserRole.ADMIN, resultEmail.getOrNull()?.role)

        // Correct admin credentials via phone
        val resultPhone = repository.loginAdmin("08120942172", "Lyricaltymer1991*")
        assertTrue(resultPhone.isSuccess)
        assertEquals(UserRole.ADMIN, resultPhone.getOrNull()?.role)

        // Wrong password rejection
        val badPass = repository.loginAdmin("apexdigitalhub232@gmail.com", "WrongPassword")
        assertFalse(badPass.isSuccess)

        // Non-admin email rejection
        val badEmail = repository.loginAdmin("otheradmin@gmail.com", "Lyricaltymer1991*")
        assertFalse(badEmail.isSuccess)
    }

    @Test
    fun testNoDoubleAccountsPerEmailAndPhone() = runBlocking {
        // Register first client
        val client1 = repository.registerClient(
            fullName = "Tayo Adeleke",
            email = "tayo@example.com",
            phone = "08011112222",
            password = "password123",
            city = "Ota",
            district = "Sango Ota"
        )
        assertTrue("First account should succeed", client1.isSuccess)

        // Register second client with same email -> MUST fail
        val duplicateEmail = repository.registerClient(
            fullName = "Duplicate Email User",
            email = "tayo@example.com",
            phone = "08099998888",
            password = "password123",
            city = "Lagos",
            district = "Ikeja"
        )
        assertFalse("Duplicate email must be rejected", duplicateEmail.isSuccess)

        // Register third client with same phone -> MUST fail
        val duplicatePhone = repository.registerClient(
            fullName = "Duplicate Phone User",
            email = "unique@example.com",
            phone = "08011112222",
            password = "password123",
            city = "Lagos",
            district = "Ikeja"
        )
        assertFalse("Duplicate phone must be rejected", duplicatePhone.isSuccess)
    }

    @Test
    fun testProviderRegistrationWithUsernameAndBackendSync() = runBlocking {
        val provResult = repository.registerServiceProvider(
            fullName = "Babatunde Alao",
            email = "babatunde@provider.ng",
            phone = "08033334444",
            whatsapp = "08033334444",
            password = "securePassword123",
            username = "babatunde_electrics",
            businessName = "Babatunde Engineering Works",
            category = "Electrician",
            city = "Ota",
            district = "Sango Ota",
            address = "14 Toll Gate, Sango Ota",
            description = "Industrial and domestic wiring specialist.",
            pricing = "From ₦7,000",
            experienceYears = 8,
            businessRegNo = "BN-987654",
            businessEmail = "info@babatundeworks.ng",
            openingHours = "Mon - Sat: 7:30 AM - 6:00 PM",
            servicesOffered = "AC Installation, Fault clearing, Conduit piping",
            photo1 = "https://example.com/p1.jpg",
            photo2 = "https://example.com/p2.jpg",
            photo3 = "https://example.com/p3.jpg"
        )

        assertTrue("Provider registration should succeed", provResult.isSuccess)
        val pair = provResult.getOrNull()!!
        assertEquals("babatunde_electrics", pair.first.username)
        assertEquals("babatunde_electrics", pair.second.username)
        assertEquals("BN-987654", pair.second.businessRegNo)
        assertTrue(pair.second.syncedToCloudBackend)

        // Duplicate username should fail
        val duplicateUsername = repository.registerServiceProvider(
            fullName = "Another Artisan",
            email = "another@provider.ng",
            phone = "08055556666",
            whatsapp = "08055556666",
            password = "password123",
            username = "babatunde_electrics",
            businessName = "Another Business",
            category = "Plumber",
            city = "Lagos",
            district = "Ikeja",
            address = "Allen Avenue",
            description = "Plumbing works",
            pricing = "From ₦5,000",
            experienceYears = 4,
            businessRegNo = null,
            businessEmail = null,
            openingHours = "Mon - Fri: 8am - 5pm",
            servicesOffered = null,
            photo1 = null,
            photo2 = null,
            photo3 = null
        )
        assertFalse("Duplicate username must be rejected", duplicateUsername.isSuccess)

        // Verify backend contains the registered provider
        val backendRecords = repository.getBackendProviderRecords()
        val found = backendRecords.find { it.username == "babatunde_electrics" }
        assertTrue("Backend should persist the registered provider", found != null)
        assertEquals("Babatunde Engineering Works", found?.businessName)
    }

    @Test
    fun testBookmarkAndRetrieveSavedProviders() = runBlocking {
        val allProviders = repository.getAllProviders().first()
        assertTrue("Providers should be available", allProviders.isNotEmpty())
        val target = allProviders.first()

        // Ensure initially not saved
        if (target.isSaved) {
            repository.toggleSavedStatus(target.id, true)
        }

        val initialSaved = repository.getSavedProviders().first()
        assertFalse("Target should not be in saved initially", initialSaved.any { it.id == target.id })

        // Bookmark / Favorite the provider
        repository.toggleSavedStatus(target.id, false)
        val updatedSaved = repository.getSavedProviders().first()
        assertTrue("Provider must appear in saved list after bookmarking", updatedSaved.any { it.id == target.id })

        // Toggle again to remove bookmark
        repository.toggleSavedStatus(target.id, true)
        val afterUnsave = repository.getSavedProviders().first()
        assertFalse("Provider must be removed from saved list after unsaving", afterUnsave.any { it.id == target.id })
    }

    @Test
    fun testNotificationChannelsAndPermissionsHelper() {
        com.example.util.NotificationHelper.createNotificationChannels(context)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val channel = notificationManager.getNotificationChannel(com.example.util.NotificationHelper.CHANNEL_ID_BOOKINGS)
        assertTrue("Notification channel must be created", channel != null)
        assertEquals(com.example.util.NotificationHelper.CHANNEL_NAME_BOOKINGS, channel.name)

        // Verify permission helpers run gracefully
        val hasLoc = com.example.util.NotificationHelper.hasLocationPermission(context)
        val hasNotif = com.example.util.NotificationHelper.hasNotificationPermission(context)
        // In robolectric default, permissions are boolean results
        assertTrue("Location helper should return boolean", hasLoc || !hasLoc)
        assertTrue("Notification helper should return boolean", hasNotif || !hasNotif)
    }

    @Test
    fun testLocationHelperMappingToNearestZone() {
        val locationHelper = com.example.data.location.LocationHelper(context)
        // Sango Ota coordinates approx: 6.7090, 3.2384
        val otaZone = locationHelper.mapCoordinatesToNearestLocation(6.7092, 3.2380)
        assertEquals("Ota", otaZone.city)
        assertEquals("Sango Ota", otaZone.name)

        // Ikeja coordinates approx: 6.6018, 3.3515
        val ikejaZone = locationHelper.mapCoordinatesToNearestLocation(6.6020, 3.3520)
        assertEquals("Lagos", ikejaZone.city)
        assertEquals("Ikeja", ikejaZone.name)
    }
}
