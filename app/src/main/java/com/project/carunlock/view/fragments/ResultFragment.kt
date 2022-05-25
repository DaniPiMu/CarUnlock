package com.project.carunlock.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import com.project.carunlock.R
import com.project.carunlock.databinding.FragmentResultBinding
import com.project.carunlock.viewmodel.CarUnlockViewModel

class ResultFragment : Fragment() {

    private lateinit var binding: FragmentResultBinding
    private val model: CarUnlockViewModel by activityViewModels()
    private var preguntasFalladas = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
     // Inflate the layout for this fragment
    binding = FragmentResultBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("falladas"){key,bundle ->
            println("PATATA: "+bundle.getInt("falladas"))
            setTestResult(bundle.getInt("falladas"))
        }

    //Aqui tenemos 3 escenarios
    // test aprobado - 0-3 fallos


    }

    private fun setTestResult(fallos: Int) {

        when (fallos){
            in 0..3 ->  {
                binding.testImage.setImageResource(R.drawable.semaforo_verde)
                binding.resultadoText.text = "¡Aprobadisimo, sigue asi!"
            }
            4, 5 ->{
                binding.testImage.setImageResource(R.drawable.semaforo_amarillo)
                binding.resultadoText.text = "¡Uy! Por los pelos... Hagamos otro"
            }
            else -> {
                binding.testImage.setImageResource(R.drawable.semaforo_rojo)
                binding.resultadoText.text = "¡No pasa nada! No pares"

            }
        }

        //continuar y ir al Fragment de study
        binding.buttonContinue.setOnClickListener {
            replaceFragment(StudyFragment())
        }
        //hacer otro test
        binding.buttonRestart.setOnClickListener {
            model.generateThirtyQuestions(false)
            replaceFragment(TestFragment())
            model.hideBottomNav()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.commit()
    }
}