package com.example.letitcook.data

import android.content.Context
import android.net.Uri
import com.example.letitcook.utils.ErrorParser
import com.example.letitcook.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import com.example.letitcook.utils.ImageUtils

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val email: String, val password: String, val profileImage: Uri?)
data class AuthResponse(val token: String?, val refreshToken: String?, val message: String?)

class AuthRepository(private val context: Context) {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    // Function to login
    suspend fun login(email: String, pass: String): Result {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            Result(success = true)
        } catch (e: Exception) {
            e.printStackTrace()

            val errorMessage = ErrorParser.parseHttpError(e)
//            Result(success = false, errorMessage = errorMessage) // highlighted to debug error
            Result(success = false, errorMessage = e.message ?: "Unknown login error")
        }
    }

    // Function to register a user
    suspend fun register(email: String, pass: String, name: String, imageUri: Uri?): Result {
        return try {
            // this creates the user in Firebase Auth
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            val user = authResult.user

            var downloadUrl: Uri? = null

            // If the user picked an image, upload it to the Storage
            if (imageUri != null && user != null) {
                //Create a reference: images/USER_ID.jpg
                val storageRef = storage.reference.child("profile_images/${user.uid}.jpg")

                // Convert URI -> Rotated Byte array (to prevent android bugs where the image is saved ROTATED)
                val data = ImageUtils.prepareImageForUpload(context, imageUri)


                // Upload the ByteArray (instead of putFie)
                storageRef.putBytes(data).await()

                // Get the download URL
                downloadUrl = storageRef.downloadUrl.await()
            }

            // Update the User's Profile (Name + Photo URL)
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name)
                .setPhotoUri(downloadUrl) // Will work even if null
                .build()

            // Updates the user with the name and photo.
            user?.updateProfile(profileUpdates)?.await()

            Result(success = true)
        } catch (e: Exception) {
            val errorMessage = ErrorParser.parseHttpError(e)
            Result(success = false, errorMessage = errorMessage)
        }
    }

    // Function to sign out the logged user
    fun logout() {
        firebaseAuth.signOut()
    }

    // Function to update user profile details
    suspend fun updateUserProfile(name: String, imageUri: Uri?): Result {
        return try {
            val user = firebaseAuth.currentUser ?: return Result(success = false, errorMessage = "User not found")

            var downloadUrl: Uri? = user.photoUrl // Default to existing URL

            // If a NEW image was picked, upload it
            if (imageUri != null) {
                val storageRef = storage.reference.child("profile_images/${user.uid}.jpg")
                val imageData = ImageUtils.prepareImageForUpload(context, imageUri)

                storageRef.putBytes(imageData).await()
                downloadUrl = storageRef.downloadUrl.await()
            }

            // Update Firebase Auth Profile
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(downloadUrl)
                .build()

            user.updateProfile(profileUpdates).await()

            // Force refresh so the UI sees the change immediately
            user.reload().await()

            Result(success = true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result(success = false, errorMessage = e.message ?: "Update failed")
        }
    }

    private fun isTokenExpired(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return true
            val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE))
            val json = JSONObject(payload)
            val exp = json.optLong("exp", 0)
            System.currentTimeMillis() / 1000 >= exp
        } catch (e: Exception) {
            true
        }
    }
}