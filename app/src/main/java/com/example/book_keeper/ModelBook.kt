package com.example.book_keeper

class ModelBook {
    var uid: String = ""
    var id: String = ""
    var title: String = ""
    var author: String = ""
    var description: String = ""
    var category: String = ""

    constructor()
    constructor(
        uid: String,
        id: String,
        title: String,
        author: String,
        description: String,
        category: String
    ) {
        this.uid = uid
        this.id = id
        this.title = title
        this.author = author
        this.description = description
        this.category = category
    }
}