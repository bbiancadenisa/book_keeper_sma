package com.example.book_keeper

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.book_keeper.databinding.ActivityAddCategoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddCategoryBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)

        binding.backAddButton.setOnClickListener {
            onBackPressed()
        }

        binding.addCategoryBtn.setOnClickListener {
            validateData()
        }
    }


    private var category = ""

    private fun validateData() {
        category = binding.categoryName.text.toString().trim()
        if (category.isEmpty()) {
            Toast.makeText(this, "Please add a valid value", Toast.LENGTH_SHORT).show()
        } else {
            addCategory()
        }
    }

    private fun addCategory() {

        progressDialog.show()
        val id = "${
            category + category.length + category.subSequence(1, 2)}"

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = id
        hashMap["category"] = category
        hashMap["uid"] = "${firebaseAuth.uid}"


        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child("id").setValue(hashMap)
            .addOnSuccessListener { progressDialog.dismiss()
                Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                progressDialog.dismiss()
                Toast.makeText(this, "Please add a valid value", Toast.LENGTH_SHORT).show()
            }

    }
}