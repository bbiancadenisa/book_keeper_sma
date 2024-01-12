package com.example.book_keeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.book_keeper.databinding.ActivityHomeAdminBinding
import com.example.book_keeper.databinding.ActivityViewBooksAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewBooksAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewBooksAdminBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var booksArrayList: ArrayList<ModelBook>

    //adapter
    private lateinit var adapterBooks: AdapterBooksAdmin
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewBooksAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // get the books from database
        loadBooks()

        binding.backViewButton.setOnClickListener {
            onBackPressed()
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
                adapterBooks = AdapterBooksAdmin(this@ViewBooksAdminActivity, booksArrayList)

                //set adapter
                binding.booksRv.adapter = adapterBooks

            }
            override fun onCancelled(error: DatabaseError) {
                // will follow
            }
        })

    }
}