package com.example.book_keeper

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.book_keeper.databinding.ActivityAddBookAdminBinding
//import com.example.book_keeper.databinding.ActivityAddBookBinding
import com.example.book_keeper.databinding.ActivityHomeAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddBookAdmin : AppCompatActivity() {

    private lateinit var binding: ActivityAddBookAdminBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private lateinit var categoryArrayList: ArrayList<ModelCategory>

    private val TAG = "BOOK_ADD_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        loadBookCategories()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("This might take a second")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backAddButton.setOnClickListener {
            onBackPressed()
        }

        binding.addBookButton.setOnClickListener {
            validateData()
        }

        binding.categoryTv.setOnClickListener {
            categoryPickDialog()
        }
    }

    private var title = ""
    private var author = ""
    private var description = ""
    private var category = ""

    private fun validateData() {
        title = binding.bookTitle.text.toString().trim()
        author = binding.bookAuthor.text.toString().trim()
        description = binding.bookDescription.text.toString().trim()
        category = binding.categoryTv.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this, "Please add a valid title", Toast.LENGTH_SHORT).show()
        } else if (author.isEmpty()) {
            Toast.makeText(this, "Please add a valid author", Toast.LENGTH_SHORT).show()
        } else if (description.isEmpty()) {
            Toast.makeText(this, "Please add a valid description", Toast.LENGTH_SHORT).show()
        } else if (category.isEmpty()) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
        } else {
            addBok()
        }
    }

    private fun loadBookCategories() {
        Log.d(TAG, "loadBookCategories : Loading Book Categories")

        categoryArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelCategory::class.java)
                    categoryArrayList.add(model!!)
                    Log.d(TAG, "onDataChange: ${model.category}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
//                Toast.makeText(this, "Failed to load categories", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "onCancelled: ${error.message}") // Log the error message for debugging
            }
        })
    }

    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""

    private fun categoryPickDialog() {
        if(categoryArrayList !== null && categoryArrayList.isNotEmpty()) {
            Log.d(TAG, "categoryPickDialog : Showing Book Categories")
            val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
            for (i in categoryArrayList.indices) {
                categoriesArray[i] = categoryArrayList[i].category
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Pick a category")
                .setItems(categoriesArray) { dialog, which ->
                    //handle item click
                    //get clicked category
                    selectedCategoryTitle = categoryArrayList[which].category
                    selectedCategoryId = categoryArrayList[which].id

                    binding.categoryTv.text = selectedCategoryTitle



                    Log.d(TAG, "categoryPickDialog : Selected Category ID: $selectedCategoryId")
                    Log.d(
                        TAG,
                        "categoryPickDialog : Selected Category Title: $selectedCategoryTitle"
                    )

                }
                .show()
        }
    }

    private fun addBok() {
        progressDialog.setMessage("Uploading Book")
        progressDialog.show()
        val timestamp = System.currentTimeMillis().toString()

        var uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["title"] = "$title"
        hashMap["author"] = "$author"
        hashMap["description"] = "$description"
        hashMap["categoryId"] = "$selectedCategoryId"

        val ref = FirebaseDatabase.getInstance().getReference(("Books"))
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Book added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Book could not be added", Toast.LENGTH_SHORT).show()

            }

    }


}