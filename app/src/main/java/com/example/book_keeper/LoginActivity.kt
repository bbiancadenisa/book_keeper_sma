package com.example.book_keeper

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.snapshots.Snapshot
import androidx.datastore.preferences.protobuf.Value
import com.example.book_keeper.databinding.ActivityLoginBinding
import com.example.book_keeper.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("This might take a second")
        progressDialog.setCanceledOnTouchOutside(false)

        //go back
        binding.registerBackBtn.setOnClickListener {
            onBackPressed()
        }

        // go to register
        binding.noAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        //validate user inputs
        binding.loginBtn.setOnClickListener {
            validateData()
        }
    }

    private var email = ""
    private var password = ""

    private fun validateData() {
        email = binding.email2.text.toString().trim()
        password = binding.password2.text.toString().trim()

        // Check if email/password are empty
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
        } else loginUser()
    }

    private fun loginUser() {
        progressDialog.setMessage("Logging In")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            checkUser()
        }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Logging in failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        progressDialog.setMessage("Checking User")

        val firebaseUser = firebaseAuth.currentUser!!
        val ref = FirebaseDatabase.getInstance().getReference("Users")

        ref.child(firebaseUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressDialog.dismiss()
                print(firebaseUser)
                val userType = snapshot.child("userType").value
                Log.d("ADebugTag", userType.toString());

                // Start different activities based on userType
                if (userType == "user") {
                    startActivity(Intent(this@LoginActivity, HomeUserActivity::class.java))
                    finish()
                } else if (userType == "admin") {
                    startActivity(Intent(this@LoginActivity, HomeAdminActivity::class.java))
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
               // will follow
            }
        })

    }
}