package com.example.book_keeper

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.book_keeper.databinding.BookRowBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterBooksAdmin : RecyclerView.Adapter<AdapterBooksAdmin.HolderBooksAdmin> {


    private val context: Context
    public var bookArrayList: ArrayList<ModelBook>
    private var filterList: ArrayList<ModelBook>

    private var filter: FilterBooks? = null

    private lateinit var binding: BookRowBinding

    constructor(context: Context, bookList: ArrayList<ModelBook>) {
        this.context = context
        this.bookArrayList = bookList
        this.filterList = bookArrayList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderBooksAdmin {
        binding = BookRowBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderBooksAdmin(binding.root)
    }

    override fun getItemCount(): Int {
        return bookArrayList.size
    }

    override fun onBindViewHolder(holder: HolderBooksAdmin, position: Int) {
        val model = bookArrayList[position]
        val bookId = model.id
        val category = model.category
        val title = model.title
        val description = model.description
        val author = model.author

        holder.titleView.text = title
        holder.authorView.text = author
        holder.descriptionView.text = description
        holder.categoryView.text = category
        holder.deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete this category?")
                .setPositiveButton("Confirm") { a, d ->
                    Toast.makeText(
                        context,
                        "Deleting",
                        Toast.LENGTH_SHORT
                    ).show()
                    deleteBook(model, holder)
                }
                .setNegativeButton("Cancel") { a, d ->
                    a.dismiss()
                }
                .show()
        }


    }

    private fun deleteBook(model: ModelBook, holder: AdapterBooksAdmin.HolderBooksAdmin) {
        val id = model.id
        val ref = FirebaseDatabase.getInstance()
            .getReference("Books")
        ref.child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Book deleted",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "Book could not be deleted",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    inner class HolderBooksAdmin(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookView = binding.bookView
        val titleView = binding.bookTitle
        val authorView = binding.bookAuthor
        val categoryView = binding.bookCategory
        val descriptionView = binding.bookDescription
        val deleteButton = binding.deleteBtn

    }

    fun getFilter(): Filter {
        if (filter == null) {
            filter = FilterBooks(filterList, this)
        }
        return filter as FilterCategory
    }
}