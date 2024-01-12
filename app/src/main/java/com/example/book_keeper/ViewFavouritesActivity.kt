package com.example.book_keeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.book_keeper.databinding.ActivityHomeUserBinding
import com.example.book_keeper.databinding.ActivityViewFavouritesBinding
import com.google.firebase.auth.FirebaseAuth

class ViewFavouritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewFavouritesBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnFavoriteView.setOnClickListener {
            onBackPressed()
        }
    }
}