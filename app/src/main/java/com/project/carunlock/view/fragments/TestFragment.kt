package com.project.carunlock.view.fragments

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.carunlock.R
import com.project.carunlock.databinding.FragmentTestBinding
import com.project.carunlock.model.UserStats
import com.project.carunlock.viewmodel.CarUnlockViewModel

class TestFragment : Fragment(), View.OnClickListener {

    private lateinit var binding : FragmentTestBinding
    private val model: CarUnlockViewModel by activityViewModels()
    val db = Firebase.firestore
    private var num = 0
    private var isClicked = false
    val user = FirebaseAuth.getInstance().currentUser

    //variables firebase
    var preguntasAcertadas:Int = 0
    var preguntasFalladas:Int = 0
    var preguntasTotales = 30
    var testAprobados = 0
    var testSuspendidos = 0
    var testTotales = 0
    val colorVerde = Color.rgb(157,241,120)
    val colorRojo = Color.rgb(241,79,79)
    var tiempoActualSegs:Long = 0
    var tiempoTotalSegs = 0
    var listIdsFailedQuestions : MutableList<String> = mutableListOf()
    val erroresTema = arrayOf<Long>(0,0,0,0,0,0,0,0,0,0,0,0,0,0)
    val themesTitles = arrayOf("dg","ngc","pdtp","sv","vi","ve","in","ma","al","se","dmrv","ac","mm","tc")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTestBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.hideBottomNav()
        model.resetTimer()


        model.questions.observe(viewLifecycleOwner, Observer {
            println("QUESTIONS LIST SIZE: "+model.questions.value!!.size)
            if (model.questions.value!!.size > 27){
                chargeQuestion()
            }
        })

        val timerText = binding.timer
        model.currentTime.observe(viewLifecycleOwner, Observer {
            timerText.text = it.toString()
            tiempoActualSegs = 1800 - model.currentSegs.value!!
        })

        model.gameFinish.observe(viewLifecycleOwner, Observer {
            if (it){
                tiempoTotalSegs += 1800
                val string = "${model.currentTime} 30MIN"
                timerText.text = string
            }
        })

        binding.goBack.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Perdida de datos")
            builder.setMessage("Si abandonas este test no se guardaran las estadísticas del mismo. Seguro que quieres salir?")

            builder.setPositiveButton("Si, salir") { dialog, which ->
                replaceFragment(StudyFragment())
            }
            builder.setNegativeButton("No, seguir con el test") { dialog, which ->

            }
            builder.show()
            model.showBottomNav()
        }

        binding.nextQuestion.setOnClickListener {
            if (num == 30) {
                tiempoTotalSegs += tiempoActualSegs.toInt()
                model.clearData()
                updateStatsData()
                //pasamos fallos
                println("BONIATO: "+preguntasFalladas)
                setFragmentResult("falladas", bundleOf("falladas" to preguntasFalladas))
                replaceFragment(ResultFragment())
                model.showBottomNav()
            } else {
                if (isClicked){
                    isClicked = false
                    resetColors()
                    chargeQuestion()
                }else{
                    Toast.makeText(requireContext(), "Selecciona una opción.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.testAnswer1Text.setOnClickListener(this)
        binding.testAnswer2Text.setOnClickListener(this)
        binding.testAnswer3Text.setOnClickListener(this)
    }

    private fun updateStatsData() {
        println("EMAIIIIIIIIIIIIIIIIIIIILLLLLLLLLLLLLLLL: "+user!!.email)
        val docRef = user.email?.let { model.db.collection("stats").document(it) }
        docRef!!.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    val userStats:UserStats = getUserStats(document) as UserStats
                    val userErrores = userStats.erroresTema.toMutableList()
                    println("DANIIIIIIIIIIIIIIIIIIII GUEIIIIIIIIIIIIIIIIIIIII: "+userStats)
                    userStats.preguntasAcertadas+=preguntasAcertadas
                    userStats.preguntasFalladas+=preguntasFalladas
                    userStats.preguntasTotales+=30
                    userStats.testTotales+=1
                    userStats.tiempoTotal +=tiempoTotalSegs
                    for(id in listIdsFailedQuestions){
                        if (!userStats.idsFailedQuestions.contains(id)){
                            userStats.idsFailedQuestions.add(id)
                        }
                    }
//                    userStats.idsFailedQuestions += listIdsFailedQuestions
                    if (preguntasFalladas<=3){
                        userStats.testAprobados+=1
                    }else{
                        userStats.testSuspendidos+=1
                    }
                    for(i in 0..13){
                        println("COUNTERRRRRRRRRRRRRRRRR:"+i)
                        println("jijijjjijijiijijijijijijijijijijijijijijijijijijijiij"+userStats.erroresTema)
                        println("DATOSSSSSSSSSSSS ERRORES TEMA: "+erroresTema.toString()+ "TEMA CONCRETO: "+erroresTema[i])
                        userErrores[i] = userStats.erroresTema[i] + erroresTema[i]
                        println("USERERRORESSSSSSSSSSSSSSSS: "+userErrores)
                    }
                    userStats.erroresTema = userErrores
                    updateStatsInDB(userStats)
                    println("DESPUESSSSSSSSSSSSSSSSSSjijijjjijijiijijijijijijijijijijijijijijijijijijijiij"+userStats.erroresTema)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun updateStatsInDB(userStats: UserStats) {
        db.collection("stats").document(user!!.email!!)
            .update(
                "erroresTema", userStats.erroresTema,
                "preguntasAcertadas", userStats.preguntasAcertadas,
                "preguntasFalladas", userStats.preguntasFalladas,
                "preguntasTotales", userStats.preguntasTotales,
                "testAprobados", userStats.testAprobados,
                "testSuspendidos", userStats.testSuspendidos,
                "testTotales",userStats.testTotales,
                "tiempoTotal", userStats.tiempoTotal,
                "idsFailedQuestions", userStats.idsFailedQuestions)
            .addOnSuccessListener { Log.d(TAG, "Marker successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
        println("UPDATE LISTAAAA PREGUNTAS FALLADAS IDDSSSSSSSSSS: "+userStats.idsFailedQuestions)
    }

    private fun getUserStats(document: DocumentSnapshot): Any {
        return UserStats(document.get("erroresTema") as MutableList<Long>,
            document.get("preguntasAcertadas") as Long,
            document.get("preguntasFalladas") as Long,
            document.get("preguntasTotales") as Long,
            document.get("testAprobados") as Long,
            document.get("testSuspendidos") as Long,
            document.get("testTotales") as Long,
            document.get("tiempoTotal") as Long,
            document.get("idsFailedQuestions") as MutableList<String>)
    }

    fun chargeQuestion() {
        println("QUESTIONS LIST ON FRAGMENT: "+model.questions.value)
        if (model.questions.value?.get(num)?.image.equals("")) {
            binding.testImage.setImageResource(R.drawable.no_image)
        } else {
            binding.testImage.load(model.questions.value?.get(num)?.image)
        }
        binding.testQuestion.text = model.questions.value?.get(num)?.title
        binding.testAnswer1Text.text = model.questions.value?.get(num)?.answer1
        binding.testAnswer2Text.text = model.questions.value?.get(num)?.answer2
        if (model.questions.value?.get(num)?.answer3.equals("")) {
            binding.testAnswer3.visibility = View.GONE
        } else {
            binding.testAnswer3.visibility = View.VISIBLE
            binding.testAnswer3Text.text = model.questions.value?.get(num)?.answer3
        }
        val qc = "Pregunta "+(num+1)+" de 30"
        binding.testCounter.text = qc
        num++
    }

    override fun onClick(v: View?) {
        val bind = getConstraintByNum(v)
        if (!isClicked){
            val textView: TextView = v as TextView
            if (textView.text.equals(model.questions.value?.get(num-1)?.answerC)) {
                bind.setBackgroundColor(colorVerde)
                preguntasAcertadas+=1
            } else {
                colorCorrectAnswer()
                preguntasFalladas+=1
                for (i in 0..13){
                    if(model.questions.value?.get(num-1)?.category == themesTitles[i]){
                        erroresTema[i] = erroresTema[i]+1
                        println("545454545454545454545454545454545454545454545454 : "+erroresTema[i])
                    }
                }
                bind.setBackgroundColor(colorRojo)
                listIdsFailedQuestions.add(model.questions.value?.get(num-1)?.id.toString())
                println("LISTAAAA PREGUNTAS FALLADAS IDDSSSSSSSSSS: "+listIdsFailedQuestions)
            }
            isClicked = true
        }
    }

    private fun getConstraintByNum(view: View?): ConstraintLayout {
        var bind : ConstraintLayout = binding.testAnswer1
        if (view.toString().contains("test_answer1_text")){
            bind = binding.testAnswer1
        }else if (view.toString().contains("test_answer2_text")){
            bind = binding.testAnswer2
        }else if (view.toString().contains("test_answer3_text")){
            bind = binding.testAnswer3
        }
        return bind
    }

    private fun colorCorrectAnswer() {
            when {
            binding.testAnswer1Text.text.toString() == model.questions.value?.get(num-1)?.answerC.toString() -> {
                binding.testAnswer1.setBackgroundColor(colorVerde)
            }
            binding.testAnswer2Text.text.toString() == model.questions.value?.get(num-1)?.answerC.toString() -> {
                binding.testAnswer2.setBackgroundColor(colorVerde)
            }
            binding.testAnswer3Text.text.toString() == model.questions.value?.get(num-1)?.answerC.toString() -> {
                binding.testAnswer3.setBackgroundColor(colorVerde)
            }
        }
    }

    private fun resetColors() {
        binding.testAnswer1.setBackgroundColor(Color.WHITE)
        binding.testAnswer2.setBackgroundColor(Color.WHITE)
        binding.testAnswer3.setBackgroundColor(Color.WHITE)
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.commit()
    }

}