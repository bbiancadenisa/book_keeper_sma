package com.example.book_keeper

class ModelCategory {
    var id:String = ""
    var category:String = ""
    var uid:String = ""

    constructor()
    constructor(id: String, category: String, uid: String) {
        this.id = id
        this.category = category
        this.uid = uid
    }
}