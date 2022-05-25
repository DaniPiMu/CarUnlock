package com.project.carunlock.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.project.carunlock.R
import com.project.carunlock.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(layoutInflater)

        binding.password1Text.doOnTextChanged { text, start, before, count ->
            if (text!!.length < 6){
                binding.password1Layout.error = "Minimo 6 caracteres!"
            }else{
                binding.password1Layout.error = null
            }
        }

        binding.password2Text.doOnTextChanged { text, start, before, count ->
            if (text!!.length < 6){
                binding.password2Layout.error = "Minimo 6 caracteres!"
            }else{
                binding.password2Layout.error = null
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.registerButton.setOnClickListener {
            if (binding.password1Text.text.toString() == binding.password2Text.text.toString()){
                createUser()
            }else{
                binding.password1Text.setText("")
                binding.password2Text.setText("")
                Toast.makeText(requireContext(), "CONTRASEÑAS NO COINCIDEN !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun createUser(){
        val email = binding.emailText.text.toString()
        val password = binding.password1Text.text.toString()
        FirebaseAuth.getInstance().
        createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    replaceFragment(LoginScreenFragment())
                }
                else{
                    showError(it.exception.toString())
                    showError("Error al registrar l'usuari")
                }
            }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView2, fragment)
        transaction.commit()
    }

    private fun showError(error:String){
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }
}