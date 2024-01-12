package com.example.book_keeper

import android.widget.Filter

class FilterCategory : Filter {

    private val filterList: ArrayList<ModelCategory>
    // adapter to filter categories

    private var adapterCategory: AdapterCategory
    //constructor
    constructor(filterList: ArrayList<ModelCategory>, adapterCategory: AdapterCategory) : super() {
        this.filterList = filterList
        this.adapterCategory = adapterCategory
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()

        // value should not be null and not empty
        if (constraint != null && constraint.isNotEmpty()) {
            //serach value is nor null nor empty
            //change to upper case or lower case
            constraint = constraint.toString().uppercase()

            val filteredModels :ArrayList<ModelCategory> = ArrayList()
            for(i in 0 until filterList.size){
                //validate
                if(filterList[i].category.uppercase().contains(constraint)) {
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
        adapterCategory.categoryArrayList = results.values as ArrayList<ModelCategory>
        //notify changes
        adapterCategory.notifyDataSetChanged()
    }
}