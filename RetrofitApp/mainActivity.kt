package com.example.retrofitapi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Data Model
data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
)

// Retrofit API Interface
interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<User>
}

// Retrofit Instance
object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

// MainActivity with Jetpack Compose UI
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserListScreen()
        }
    }
}

// UI: Display List of Users
@Composable
fun UserListScreen() {
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            users = RetrofitClient.apiService.getUsers()
        } catch (e: Exception) {
            Log.e("API_ERROR", "Failed to fetch users: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.padding(20.dp))
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(users) { user ->
                UserCard(user)
            }
        }
    }
}

// UI: Display User Card
@Composable
fun UserCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "ID: ${user.id}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Name: ${user.name}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Username: ${user.username}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
