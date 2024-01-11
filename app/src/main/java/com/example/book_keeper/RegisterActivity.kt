package com.example.book_keeper

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.book_keeper.databinding.ActivityMainBinding
import com.example.book_keeper.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("This might take a second")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.registerBackBtn.setOnClickListener {
            onBackPressed()
        }

        binding.registerBtn1.setOnClickListener {
            validateData()
        }
    }

    private var name = ""
    private var email = ""
    private var password = ""

    private fun validateData() {
        name = binding.name2.text.toString().trim()
        email = binding.email2.text.toString().trim()
        password = binding.password2.text.toString().trim()
        val confirm_password = binding.confirmPassword2.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
        } else if (email.isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
        } else if (confirm_password.isEmpty()) {
            Toast.makeText(this, "Please confirm password", Toast.LENGTH_SHORT).show()
        } else if (confirm_password != password) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
        } else {
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        progressDialog.setMessage("Creating account")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            updateUserInfo()
        }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Account creation process failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo() {
        progressDialog.setMessage("Saving user info")
        val timestamp = System.currentTimeMillis()
        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["password"] = password
        hashMap["profileImage"] = ""
        hashMap["userType"] = "user"
//        hashMap[""] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!).
        setValue(hashMap)
            .addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(
                this,
                "Account created successfully",
                Toast.LENGTH_SHORT

            ).show()
            startActivity(
                Intent(this@RegisterActivity, HomeUserActivity::class.java)
            )
            finish()
        }.addOnFailureListener { e ->
            progressDialog.dismiss()
            Toast.makeText(this, "Account creation process failed ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }
}