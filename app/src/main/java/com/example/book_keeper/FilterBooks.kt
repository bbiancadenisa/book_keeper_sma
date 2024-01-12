package com.example.book_keeper

import android.widget.Filter

class FilterBooks : Filter {

    private val filterList: ArrayList<ModelBook>
    // adapter to filter categories
    private var adapterBooksAdmin: AdapterBooksAdmin

    //constructor
    constructor(filterList: ArrayList<ModelBook>, adapterBooksAdmin: AdapterBooksAdmin) : super() {
        this.filterList = filterList
        this.adapterBooksAdmin = adapterBooksAdmin
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()

        // value should not be null and not empty
        if (constraint != null && constraint.isNotEmpty()) {
            //search value is nor null nor empty
            //change to upper case or lower case
            constraint = constraint.toString().uppercase()
            val filteredModels :ArrayList<ModelBook> = ArrayList()
            for(i in 0 until filterList.size){
                //validate
                if(filterList[i].title.uppercase().contains(constraint)) {
                    // add to filtered list
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            //search value is null or empty
            results.count = filterList.size
            results.values= filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        // apply filter changes
        adapterBooksAdmin.bookArrayList = results.values as ArrayList<ModelBook>
        //notify changes
        adapterBooksAdmin.notifyDataSetChanged()
    }
}