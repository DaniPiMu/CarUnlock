package com.project.carunlock.view.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import coil.load
import com.project.carunlock.R
import com.project.carunlock.databinding.FragmentFailedQuestionsBinding
import com.project.carunlock.model.Question
import com.project.carunlock.viewmodel.CarUnlockViewModel


class FailedQuestionsFragment : Fragment() {

    private lateinit var binding : FragmentFailedQuestionsBinding
    private val model: CarUnlockViewModel by activityViewModels()

    private var num = 0
    private var isNextQuestion = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFailedQuestionsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.getIdsFailedQuestions()

        model.idsFaileds.observe(viewLifecycleOwner, Observer {
            println("FRAGMENT LIST IDS: "+model.idsFaileds.value.toString())
            model.fetchFailedsQuestions(it)
//            println("FRAGMENT LIST QUESTIONS: "+model.questions.value.toString())
//            println("FRAGMENT LIST QUESTIONS 2: "+model.failedQuestionsList)
            model.questions.observe(viewLifecycleOwner, Observer {
                println("FRAGMENT LIST QUESTIONS: "+model.questions.value.toString())
                println("FRAGMENT LIST FAILED QUESTIONS: "+model.failedQuestions.value.toString())
                println("FRAGMENT LIST QUESTIONS 2: "+model.failedQuestionsList)
                if(model.questions.value!!.size > 27){
                    binding.previousQuestion.visibility = View.INVISIBLE
                    chargeQuestion(isNextQuestion)
                    colorCorrectAnswer()
                }
            })
        })

        binding.goBack.setOnClickListener{
            replaceFragment(FailuresRegisterFragment())
        }


        binding.nextQuestion.setOnClickListener {
            num++
            println("NUM PREGUNTA: "+num)
            isNextQuestion = true
            binding.previousQuestion.visibility = View.VISIBLE
            println("NUM PREG: "+(num+1)+" -----  TAMAÑO LISTA: "+(model.questions.value!!.size+1))
            if (num+1 == model.questions.value!!.size+1) {
                println("ENTRA AL IF")
                replaceFragment(StudyFragment())
                model.showBottomNav()
            } else {
                resetColors()
                chargeQuestion(isNextQuestion)
                colorCorrectAnswer()
            }
        }
        binding.previousQuestion.setOnClickListener{
            num--
            println("NUM PREGUNTA: "+num)
            isNextQuestion = false
            if (num == 0){
                binding.previousQuestion.visibility = View.INVISIBLE
                resetColors()
                chargeQuestion(isNextQuestion)
                colorCorrectAnswer()
            }else{
                binding.previousQuestion.visibility = View.VISIBLE
                resetColors()
                chargeQuestion(isNextQuestion)
                colorCorrectAnswer()
            }
        }
    }

    fun chargeQuestion(isNextQuestion: Boolean) {
        println("FAILED QUESTIONS LIST: "+model.questions.value.toString())
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
        val qc = "Pregunta "+(num+1)+" de "+model.idsFaileds.value!!.size
        binding.testCounter.text = qc
//        if (isNextQuestion){
//            num++
//        }else{
//            num--
//        }
    }

    private fun colorCorrectAnswer() {
        when {
            binding.testAnswer1Text.text.toString() == model.questions.value?.get(num)?.answerC.toString() -> {
                binding.testAnswer1Text.setBackgroundColor(Color.GREEN)
            }
            binding.testAnswer2Text.text.toString() == model.questions.value?.get(num)?.answerC.toString() -> {
                binding.testAnswer2Text.setBackgroundColor(Color.GREEN)
            }
            binding.testAnswer3Text.text.toString() == model.questions.value?.get(num)?.answerC.toString() -> {
                binding.testAnswer3Text.setBackgroundColor(Color.GREEN)
            }
        }
    }

    private fun resetColors() {
        binding.testAnswer1Text.setBackgroundColor(Color.WHITE)
        binding.testAnswer2Text.setBackgroundColor(Color.WHITE)
        binding.testAnswer3Text.setBackgroundColor(Color.WHITE)
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.commit()
    }

}