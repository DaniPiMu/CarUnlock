package com.project.carunlock.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.project.carunlock.R
import com.project.carunlock.databinding.FragmentStudyBinding
import com.project.carunlock.viewmodel.CarUnlockViewModel

class StudyFragment : Fragment() {

    private lateinit var binding: FragmentStudyBinding
    private val model: CarUnlockViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStudyBinding.inflate(layoutInflater)
        model.showBottomNav()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.firstButton.setOnClickListener {
            model.generateThirtyQuestions(false)
            replaceFragment(TestFragment())
            model.hideBottomNav()
        }
        binding.secondButton.setOnClickListener {
            replaceFragment(TemarioFragment())
        }

        binding.thirdButton.setOnClickListener {
            replaceFragment(FailuresRegisterFragment())
        }

        println(this.id)

    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.commit()
    }

}