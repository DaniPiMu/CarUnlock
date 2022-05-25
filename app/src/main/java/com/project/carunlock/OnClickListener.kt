package com.project.carunlock

import com.project.carunlock.model.Question
import com.project.carunlock.model.Tema

interface OnClickListener {
    fun onClick(tema: Tema)
    fun onClickQuestion(question: Question)
}