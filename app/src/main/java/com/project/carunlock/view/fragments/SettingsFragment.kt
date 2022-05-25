package com.project.carunlock.view.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.project.carunlock.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        botonRateApp()
        botonNotificaciones()
        botonAboutUs()
    }

    private fun botonNotificaciones() {
        binding.notificaciones.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Notificaciones diarias")
            builder.setMessage("Quieres recibir notificaciones diarias para ayudarte a hacer tests?")

            builder.setPositiveButton("Claro que si",DialogInterface.OnClickListener { dialogInterface, i ->
                FirebaseMessaging.getInstance().subscribeToTopic("notificaciones")
                Toast.makeText(context, "Se han activado las notificaciones!",Toast.LENGTH_SHORT).show()})

            builder.setNegativeButton("Desactivar notificaciones ",DialogInterface.OnClickListener { dialogInterface, i ->
                FirebaseMessaging.getInstance().unsubscribeFromTopic("notificaciones")
                Toast.makeText(context, "Se han desactivado las notificaciones!",Toast.LENGTH_SHORT).show()})

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    private fun botonAboutUs() {
        binding.ayuda.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Sobre nosotros")
            builder.setMessage("Somos estudiantes del Institut Tecnologic de Barcelona del grupo de DAMi y este es nuestro proyecto de final de Grado Superior.")
            builder.setPositiveButton("aceptar", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    private fun botonRateApp() {
        binding.rateApp.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("market://details?id=com.project.carunlock"))
            startActivity(intent)
        })
    }



}