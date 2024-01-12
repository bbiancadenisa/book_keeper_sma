package com.example.book_keeper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.book_keeper.databinding.ActivityHomeAdminBinding
import com.example.book_keeper.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class HomeAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeAdminBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var categoryArrayList: ArrayList<ModelCategory>

    //adapter
    private lateinit var adapterCategory: AdapterCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
        loadCategories()

        // search
        binding.searchAdmin.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //will follow
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adapterCategory.filter.filter(s)
                } catch (e: Exception) { }
            }

            override fun afterTextChanged(p0: Editable?) {
                //will follow
            }
        })

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }

        binding.addCategory.setOnClickListener {
            startActivity(Intent(this, AddCategoryActivity::class.java))
        }

        binding.addBook.setOnClickListener {
            startActivity(Intent(this, AddBookAdmin::class.java))
        }

        binding.viewBooks.setOnClickListener {
            startActivity(Intent(this, ViewBooksAdminActivity::class.java))
        }

    }

    private fun checkUser() {

        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            val userName = firebaseUser.displayName
            binding.toolbarEmail.text = userName
        }
    }

    private fun loadCategories() {

        //init arraylist
        categoryArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue((ModelCategory::class.java))
                    categoryArrayList.add(model!!)
                }
                adapterCategory = AdapterCategory(this@HomeAdminActivity, categoryArrayList)

                //set adapter
                binding.categoriesRv.adapter = adapterCategory

            }
            override fun onCancelled(error: DatabaseError) {
               //will follow
            }
        })

    }
}