package com.project.carunlock.viewmodel

import android.os.CountDownTimer
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.carunlock.R
import com.project.carunlock.model.Question
import com.project.carunlock.model.RandomNumber
import com.project.carunlock.model.Tema
import java.text.DecimalFormat
import java.text.NumberFormat

class CarUnlockViewModel: ViewModel() {
    val temas = MutableLiveData<MutableList<Tema>>().apply {
        this.value = mutableListOf<Tema>(
            Tema("Tema 0","https://firebasestorage.googleapis.com/v0/b/carunlock-96030.appspot.com/o/Tema0.pdf?alt=media&token=09ecea36-4014-4d52-a907-c350f0ca9eea","Definiciones generales", R.drawable.tema0),
            Tema("Tema 1","https://firebasestorage.googleapis.com/v0/b/carunlock-96030.appspot.com/o/temas%2FTema1.pdf?alt=media&token=100b877b-535a-4c8f-b3f4-93776aa3da25","Normas generales de comportamiento", R.drawable.tema1),
            Tema("Tema 2","https://firebasestorage.googleapis.com/v0/b/carunlock-96030.appspot.com/o/temas%2FTema2.pdf?alt=media&token=44a3564f-a27f-48fc-b08a-b0c846025675","Pesos, dimensiones, transporte de personas", R.drawable.tema2),
            Tema("Tema 3","https://firebasestorage.googleapis.com/v0/b/carunlock-96030.appspot.com/o/temas%2FTema3.pdf?alt=media&token=3fb06a0a-18e3-4d6a-89be-fb11bd24fd0e","Seguridad vial", R.drawable.tema3),
            Tema("Tema 4","https://firebasestorage.googleapis.com/v0/b/carunlock-96030.appspot.com/o/Tema0.pdf?alt=media&token=09ecea36-4014-4d52-a907-c350f0ca9eea","La vía", R.drawable.tema4),
            Tema("Tema 5","https://firebasestorage.googleapis.com/v0/b/carunlock-96030.appspot.com/o/temas%2FTema1.pdf?alt=media&token=100b877b-535a-4c8f-b3f4-93776aa3da25","Velocidad", R.drawable.tema5),
            Tema("Tema 6","https://firebasestorage.googleapis.com/v0/b/carunlock-96030.appspot.com/o/temas%2FTema2.pdf?alt=media&token=44a3564f-a27f-48fc-b08a-b0c846025675","Intersecciones", R.drawable.tema6),
            Tema("Tema 7","https://firebasestorage.googleapis.com/v0/b/carunlock-96030.appspot.com/o/temas%2FTema3.pdf?alt=media&token=3fb06a0a-18e3-4d6a-89be-fb11bd24fd0e","Maniobras", R.drawable.tema7),
            Tema("Tema 8","https://firebasestorage.googleapis.com/v0/b/carunlock-96030.appspot.com/o/Tema0.pdf?alt=media&token=09ecea36-4014-4d52-a907-c350f0ca9eea","Alumbrado", R.drawable.tema8),
            Tema("Tema 9","https://firebasestorage.googleapis.com/v0/b/carunlock-96030.appspot.com/o/temas%2FTema1.pdf?alt=media&token=100b877b-535a-4c8f-b3f4-93776aa3da25","Señales", R.drawable.tema9),
            Tema("Tema 10","https://firebasestorage.googleapis.com/v0/b/carunlock-96030.appspot.com/o/temas%2FTema2.pdf?alt=media&token=44a3564f-a27f-48fc-b08a-b0c846025675","Documentos, mandos y reglajes del vehículo", R.drawable.tema10),
            Tema("Tema 11","https://firebasestorage.googleapis.com/v0/b/carunlock-96030.appspot.com/o/temas%2FTema3.pdf?alt=media&token=3fb06a0a-18e3-4d6a-89be-fb11bd24fd0e","Accidentes", R.drawable.tema11),
            Tema("Tema 12","https://firebasestorage.googleapis.com/v0/b/carunlock-96030.appspot.com/o/temas%2FTema1.pdf?alt=media&token=100b877b-535a-4c8f-b3f4-93776aa3da25","Mecánica y mantenimiento", R.drawable.tema12),
            Tema("Tema 13","https://firebasestorage.googleapis.com/v0/b/carunlock-96030.appspot.com/o/Tema0.pdf?alt=media&token=09ecea36-4014-4d52-a907-c350f0ca9eea","Técnicas de conducción", R.drawable.tema13)
        )
    }


    fun showBottomNav() {
        constraint.value?.visibility = View.VISIBLE
        guideline.value?.setGuidelineEnd(60)
    }

    fun hideBottomNav() {
        constraint.value?.visibility = View.GONE
        guideline.value?.setGuidelineEnd(0)
    }

    fun setGuide(guide : Guideline) {
        guideline.value = guide
    }

    fun setCons(cons : ConstraintLayout) {
        constraint.value = cons
    }

    var constraint = MutableLiveData<ConstraintLayout>()
    var guideline = MutableLiveData<Guideline>()
    var currentTime = MutableLiveData<String>()
    val gameFinish = MutableLiveData<Boolean>()
    var currentSegs = MutableLiveData<Long>(0)
    val needMoreFailedQuestions = MutableLiveData<Boolean>()


    val timerr = object : CountDownTimer(1800000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val f: NumberFormat = DecimalFormat("00")
            val min = millisUntilFinished / 60000 % 60
            val sec = millisUntilFinished / 1000 % 60
            currentTime.value = f.format(min) + ":" + f.format(sec)
            currentSegs.value = sec+(min*60)
        }

        override fun onFinish() {
            gameFinish.value = true
            resetTimer()
        }
    }


    init {
        timerr.start()
    }

    fun resetTimer(){
        timerr.cancel()
        timerr.onTick(1800000)
        timerr.start()
    }


    var questions = MutableLiveData<MutableList<Question>>()
    var failedQuestions = MutableLiveData<MutableList<Question>>()
    var questionList = mutableListOf<Question>()
    var failedQuestionsList = mutableListOf<Question>()
    val db = Firebase.firestore

    var idsFaileds = MutableLiveData<MutableList<String>>()

    fun fetchFailedsQuestions(idList: MutableList<String>){
        println("LISTA IDSSSSS: "+idList)
        println("QUESTIONS LIST ANTES DEL FETCH: "+questionList)
        for (num in idList){
            var count = 0
            for (question in questionList){
                if (question.id == num.toInt()){
                    count++
                }
            }
            if (count==0){
                fetchData(num.toInt(),idList.size)
            }
        }
    }

    private fun fetchInDBFailedQuestions(id: String, size: Int) {
        db.collection("questions")
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener { documents ->
                println("Size of the document "+documents.size())
                for (document in documents) {
                    println("DATA VALUES TO STRING: "+document.data.values.toString())
                    val myQuestion = document.toObject(Question::class.java)
                    println("++++++++++++++++++ "+myQuestion)
                    failedQuestionsList.add(myQuestion)
                    println("FAILEDQUESTIONLIST VALUE: "+failedQuestionsList)
                    println("454545454545455445454545454554545454545 "+failedQuestionsList.size)
                    if (failedQuestionsList.size == size){
                        failedQuestions.value = failedQuestionsList
                    }
                }
            }
    }

    private fun fetchData(num: Int,size: Int) {
        println("777777777777777777777777777777 "+num.toString())
        db.collection("questions")
            .whereEqualTo("id", num)
            .get()
            .addOnSuccessListener { documents ->
                println("Size of the document "+documents.size())
                for (document in documents) {
                    println(document.data.values.toString())
                    val myQuestion = document.toObject(Question::class.java)
                    println("++++++++++++++++++ "+myQuestion)
                    questionList.add(myQuestion)
                    println("454545454545455445454545454554545454545 "+questionList.size+" ----- "+size)
                    if (questionList.size == size) {
                        questions.value = questionList
                        println(questions.value!! +" oasigfsapihgpoisahpghsaoighpsahgpsapoghsaphgpsahgosaih")
                    }
                }
            }
    }

    fun getIdsFailedQuestions() {
        val user = FirebaseAuth.getInstance().currentUser
        var idsFailedQuestions = mutableListOf<String>()
            db.collection("stats")
            .document(user!!.email!!)
            .get()
            .addOnSuccessListener {
                println("DB INFO: "+it.data?.get("idsFailedQuestions") )
                idsFailedQuestions = it.data?.get("idsFailedQuestions") as MutableList<String>
                idsFaileds.value = idsFailedQuestions
            }
        println("USUARIO: "+user.email)
        println("LISTA IDS FALLADOS OBTENIDOS: "+idsFailedQuestions)
//        idsFaileds.value = idsFailedQuestions
    }

    fun clearData() {
        questionList.clear()
//        questions.value = mutableListOf()
    }

    fun generateThirtyQuestions(isFailuresTest: Boolean): Int {
        val r = RandomNumber(1, 61)
        var numQuestions = 0
        if (isFailuresTest) {
//            getIdsFailedQuestions()
            println("oiysdaiuyfiydsgydslkihlslibyhdslbhdslihblidshb" + idsFaileds.value!!.size)
            val random = RandomNumber(1, idsFaileds.value!!.size)
            for (i in 1..30) {
                fetchData(idsFaileds.value!![random.nextInt()-1].toInt(), 30)
            }
            return numQuestions
        } else {
            for (i in 1..30) {
                fetchData(r.nextInt(), 30)
            }
            return numQuestions
        }
    }

    var selectedTema = MutableLiveData<Tema>()

    fun setTema(tema: Tema) {
        selectedTema.value = tema
    }

}