package com.example.letitcook.data

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.example.letitcook.network.NetworkModule
import com.example.letitcook.utils.ErrorParser
import com.example.letitcook.utils.Result
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.create

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val email: String, val password: String, val profileImage: Uri?)
data class AuthResponse(val token: String?, val refreshToken: String?, val message: String?)

class AuthRepository(private val context: Context) {

//    private val api = NetworkModule.retrofit.create<AuthApiService>()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("LetItCookPrefs", Context.MODE_PRIVATE)

    fun isUserLoggedIn(): Boolean {
        val accessToken = sharedPreferences.getString("accessToken", null)
        return accessToken != null && !isTokenExpired(accessToken)
    }

//    suspend fun login(email: String, password: String): Result {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = api.login(LoginRequest(email, password))
//                saveTokens(response.token, response.refreshToken)
//                Result(success = true)
//            } catch (e: Exception) {
//                val errorMessage = ErrorParser.parseHttpError(e)
//                Result(success = false, errorMessage = errorMessage)
//            }
//        }
//    }

//    suspend fun register(email: String, password: String, profileImageUri: Uri?): Result {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = api.register(RegisterRequest(email, password, profileImageUri))
//                Result(success = true)
//            } catch (e: Exception) {
//                val errorMessage = ErrorParser.parseHttpError(e)
//                Result(success = false, errorMessage = errorMessage)
//            }
//        }
//    }

    // Function to login
    suspend fun login(email: String, pass: String): Result {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            Result(success = true)
        } catch (e: Exception) {

            // Log the error to your Logcat so you can see it
            e.printStackTrace()

            val errorMessage = ErrorParser.parseHttpError(e)
//            Result(success = false, errorMessage = errorMessage) // highlighted to debug error
            Result(success = false, errorMessage = e.message ?: "Unknown login error")
        }
    }

    // Function to register a user
    suspend fun register(email: String, pass: String): Result {
        return try {
            // This talks directly to Google, no localhost needed
            firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            Result(success = true)
        } catch (e: Exception) {
            val errorMessage = ErrorParser.parseHttpError(e)
            Result(success = false, errorMessage = errorMessage)
        }
    }

//    suspend fun logout(): Result {
//        return withContext(Dispatchers.IO) {
//            try {
//                api.logout()
//                clearTokens()
//                Result(success = true)
//            } catch (e: Exception) {
//                val errorMessage = ErrorParser.parseHttpError(e)
//                Result(success = false, errorMessage = errorMessage)
//            }
//        }
//    }
//
//    suspend fun refreshToken(): Result {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = api.refreshToken()
//                saveTokens(response.token, response.refreshToken)
//                Result(success = true)
//            } catch (e: Exception) {
//                val errorMessage = ErrorParser.parseHttpError(e)
//                Result(success = false, errorMessage = errorMessage)
//            }
//        }
//    }

    private fun saveTokens(accessToken: String?, refreshToken: String?) {
        sharedPreferences.edit().apply {
            putString("accessToken", accessToken)
            putString("refreshToken", refreshToken)
            apply()
        }
    }

    private fun clearTokens() {
        sharedPreferences.edit().clear().apply()
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