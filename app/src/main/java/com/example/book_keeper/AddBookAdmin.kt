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

        // get the list of book categories in order to display them in the dialog box
        loadBookCategories()

        //inform the user that the process is starting
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("This might take a second")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle navigating back
        binding.backAddButton.setOnClickListener {
            onBackPressed()
        }

        //handle click on add book
        binding.addBookButton.setOnClickListener {
            validateData()
        }

        //handle the chosing of category process
        binding.categoryTv.setOnClickListener {
            categoryPickDialog()
        }
    }

    // variables that describe a book itm
    private var title = ""
    private var author = ""
    private var description = ""
    private var category = ""

    //the function used for retrieving categories
    private fun loadBookCategories() {
        Log.d(TAG, "loadBookCategories : Loading Book Categories")
        categoryArrayList = ArrayList()

        //get reference from database
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
                Log.d(TAG, "onCancelled: ${error.message}") // Log the error message for debugging
            }
        })
    }

    //checking for empty fields, and displaying toast if they are found

    private fun validateData() {
        //getting the values
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
            //the actual process of adding the books
            addBok()
        }
    }


    // when creating a book, we also store the name and the id of the category it belongs to
    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""

    private fun categoryPickDialog() {
        if (categoryArrayList !== null && categoryArrayList.isNotEmpty()) {
            Log.d(TAG, "categoryPickDialog : Showing Book Categories")
            val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
            for (i in categoryArrayList.indices) {
                categoriesArray[i] = categoryArrayList[i].category
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Pick a category")
                .setItems(categoriesArray) { dialog, which ->
                    //handle category click
                    selectedCategoryTitle = categoryArrayList[which].category
                    selectedCategoryId = categoryArrayList[which].id
                    binding.categoryTv.text = selectedCategoryTitle

                    Log.d(TAG, "categoryPickDialog : Selected Category ID: $selectedCategoryId")
                    Log.d(
                        TAG,
                        "categoryPickDialog : Selected Category Title: $selectedCategoryTitle"
                    )
                }.show()
        }
    }

    private fun addBok() {
        progressDialog.setMessage("Uploading Book")
        progressDialog.show()
        val timestamp = System.currentTimeMillis().toString()
        var uid = firebaseAuth.uid

        //Getting and Setting the Data
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["title"] = "$title"
        hashMap["author"] = "$author"
        hashMap["description"] = "$description"
        hashMap["categoryId"] = "$selectedCategoryId"
        hashMap["category"] = "$selectedCategoryTitle"

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