package com.project.carunlock.model

data class Question(
    var answer1 : String = "",
    var answer2 : String = "",
    var answer3 : String = "",
    var answerC : String = "",
    var category : String = "",
    var image : String = "",
    var title : String = "",
    var id : Int = 0
)
