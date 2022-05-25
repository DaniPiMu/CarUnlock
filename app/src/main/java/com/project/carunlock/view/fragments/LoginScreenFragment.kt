package com.project.carunlock.view.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.project.carunlock.R
import com.project.carunlock.databinding.FragmentLoginScreenBinding
import com.project.carunlock.view.MainActivity
import com.project.carunlock.viewmodel.CarUnlockViewModel

class LoginScreenFragment : Fragment() {

    private lateinit var binding: FragmentLoginScreenBinding
    private val db = FirebaseFirestore.getInstance()
    private val model:CarUnlockViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginScreenBinding.inflate(layoutInflater)

        binding.googleButton.setOnClickListener {
            iniciarSesionGoogle()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginButton.setOnClickListener {
            println("DATOS LOGIN: "+binding.emailText.text.toString()+"  "+binding.passwordText.text.toString())
            if (binding.emailText.text!!.length>0 && binding.passwordText.text!!.length>0){
                loginUser()
            }else{
                showError("INTRODUCE USUARIO Y CONTRASEÑA !!")
            }
        }
        binding.registrateButton.setOnClickListener {
            replaceFragment(RegisterFragment())
        }
    }


    fun loginUser(){
        FirebaseAuth.getInstance().
        signInWithEmailAndPassword(binding.emailText.text.toString(), binding.passwordText.text.toString())
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val emailLogged = it.result?.user?.email
                    goToHome(emailLogged!!)
                }
                else{
                    showError("Error al iniciar sesion!")
                }
            }
    }

    private fun goToHome(emailLogged: String) {
        val usuario = this.db.collection("stats").document(emailLogged);
        usuario.get().addOnSuccessListener {
            if (!it.exists()) {
                println("No existe el usuario");
                createUserDB(emailLogged!!)}
        }
        val intent = Intent(context,MainActivity::class.java)
        intent.putExtra("emailLogged",emailLogged)
        startActivity(intent)
        println("HOLAAAAAAA emailLogged: "+emailLogged)
    }

    private fun createUserDB(emailLogged: String) {
        db.collection("stats").document(emailLogged).set(
            hashMapOf(
                "erroresTema" to listOf<Long>(0,0,0,0,0,0,0,0,0,0,0,0,0,0),
                "preguntasAcertadas" to 0,
                "preguntasFalladas" to 0,
                "preguntasTotales" to 0,
                "testAprobados" to 0,
                "testSuspendidos" to 0,
                "testTotales" to 0,
                "tiempoTotal" to 0,
                "idsFailedQuestions" to listOf<String>())
        )
    }

    private fun showError(error:String){
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }
    private fun iniciarSesionGoogle() {
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleClient = GoogleSignIn.getClient(requireContext(), googleConf)
        val signInIntent = googleClient.signInIntent
        googleClient.signOut()
        startActivityForResult(signInIntent, 100)
    }
    private fun showAlert() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error de inicio de sesion")
        builder.setPositiveButton("aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                println("google")
                                val emailLogged = it.result?.user?.email
                                goToHome(emailLogged!!)
                            } else {
                                showAlert()
                            }
                        }
                }
            } catch (e: ApiException) {
                showAlert()
            }
        }
    }

        private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView2, fragment)
        transaction.commit()
    }
}