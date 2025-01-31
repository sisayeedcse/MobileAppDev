package com.example.firebasecompose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// Data Model
data class User(val name: String = "", val id: String = "", val email: String = "")

@Composable
fun UserFormScreen() {
    val database = Firebase.database.reference
    var name by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val usersList = remember { mutableStateListOf<User>() }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = id, onValueChange = { id = it }, label = { Text("ID") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = {
                isLoading = true
                val user = User(name, id, email)
                database.child("users").child(id).setValue(user).addOnCompleteListener {
                    isLoading = false
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                    name = ""
                    id = ""
                    email = ""
                }
            }) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Save Info")
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        usersList.clear()
                        for (userSnapshot in snapshot.children) {
                            val user = userSnapshot.getValue(User::class.java)
                            if (user != null) {
                                usersList.add(user)
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }) {
                Text("Show Info")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(usersList) { user ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Name: ${user.name}")
                        Text("ID: ${user.id}")
                        Text("Email: ${user.email}")
                    }
                }
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { UserFormScreen() }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFormScreen() {
    UserFormScreen()
}
