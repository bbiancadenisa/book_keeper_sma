package com.example.book_keeper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.book_keeper.databinding.ActivityHomeAdminBinding
import com.example.book_keeper.databinding.ActivityHomeUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeUserBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var booksArrayList: ArrayList<ModelBook>
    private lateinit var adapterBooks: AdapterBooksAdmin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
        loadBooks()

        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.viewFavorites.setOnClickListener {
            startActivity(Intent(this, ViewFavouritesActivity::class.java))
            finish()
        }
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            val email = firebaseUser.email
            binding.toolbarEmail.text = email
        }
    }

    private fun loadBooks() {
        //init arraylist
        booksArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                booksArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue((ModelBook::class.java))
                    booksArrayList.add(model!!)
                }
                adapterBooks = AdapterBooksAdmin(this@HomeUserActivity, booksArrayList)

                //set adapter
                binding.booksRv.adapter = adapterBooks

            }
            override fun onCancelled(error: DatabaseError) {
                //will follow
            }
        })

    }
}