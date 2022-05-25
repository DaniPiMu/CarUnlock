package com.project.carunlock.view.fragments

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.carunlock.R
import com.project.carunlock.databinding.FragmentFailuresRegisterBinding
import com.project.carunlock.databinding.FragmentTestBinding
import com.project.carunlock.viewmodel.CarUnlockViewModel


class FailuresRegisterFragment : Fragment() {

    private lateinit var binding : FragmentFailuresRegisterBinding
    private val model: CarUnlockViewModel by activityViewModels()
    val db = Firebase.firestore
    val handler = Handler()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFailuresRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.questionsButton.setOnClickListener {
//            model.getIdsFailedQuestions()
            replaceFragment(FailedQuestionsFragment())
//            if (model.idsFaileds.value!!.size >= 1){
//                replaceFragment(FailedQuestionsFragment())
//            }else{
//                Toast.makeText(requireContext(), "No tienes ninguna pregunta fallada.", Toast.LENGTH_LONG).show()
//            }
        }

        binding.questionsTestButton.setOnClickListener {
            model.getIdsFailedQuestions()
            handler.postDelayed({
                if (model.idsFaileds.value!!.size >= 30){
                    model.generateThirtyQuestions(true)
                    handler.postDelayed({
                        println("QUESTIONS MOOOOOODEL: "+model.questions.value)
                        replaceFragment(TestFragment())
                    },500)
                }else{
                    Toast.makeText(requireContext(), "Realiza mas test hasta tener un minimo de 30 fallos en total!", Toast.LENGTH_LONG).show()
                }
            }, 500)
        }

//        var needMoreFailedQuestions:Boolean = false
//        model.needMoreFailedQuestions.observe(viewLifecycleOwner, Observer {
//            needMoreFailedQuestions = it
//        })
//        if (needMoreFailedQuestions){
//            Toast.makeText(requireContext(), "Realiza mas test hasta tener un minimo de 30 fallos en total!", Toast.LENGTH_LONG).show()
//        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.commit()
    }

}