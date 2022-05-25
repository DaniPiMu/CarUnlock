package com.project.carunlock.view.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.project.carunlock.R
import com.project.carunlock.databinding.FragmentStatsBinding
import com.project.carunlock.viewmodel.CarUnlockViewModel
import org.eazegraph.lib.models.PieModel

class StatsFragment : Fragment() {

    private lateinit var binding: FragmentStatsBinding
    private val model: CarUnlockViewModel by activityViewModels()
    val user = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var pieChart = binding.pie

        //quesito predeterminado
        putFailedAprovedStats(pieChart)
        binding.text1.background = ContextCompat.getDrawable(requireContext(),R.drawable.border_stats_clicked)

        //quesito test aprobados
        binding.text1.setOnClickListener {
            resetChar(pieChart)
            binding.text1.background = ContextCompat.getDrawable(requireContext(),R.drawable.border_stats_clicked)
            binding.text2.background = ContextCompat.getDrawable(requireContext(),R.drawable.border_stats_unclicked)
            binding.text3.background = ContextCompat.getDrawable(requireContext(),R.drawable.border_stats_unclicked)
            binding.tiempo.visibility = View.INVISIBLE
            //cogemos valor y hacemos calculos
            putFailedAprovedStats(pieChart)
        }

        //quesito errores por temas
        binding.text2.setOnClickListener {
            resetChar(pieChart)
            binding.text2.background = ContextCompat.getDrawable(requireContext(),R.drawable.border_stats_clicked)
            binding.text1.background = ContextCompat.getDrawable(requireContext(),R.drawable.border_stats_unclicked)
            binding.text3.background = ContextCompat.getDrawable(requireContext(),R.drawable.border_stats_unclicked)
            binding.tiempo.visibility = View.INVISIBLE
            model.db.collection("stats").document(user!!.email!!).get()
                .addOnSuccessListener {
                    val erroresPorTema = ((it.get("erroresTema") as MutableList<Long>))
                    var totalErroresTest: Long = 0
                    erroresPorTema.forEach {
                        totalErroresTest += it
                    }
                    println("Errores totales: " + totalErroresTest)

                    //los 13 quesitos

                    pieChart.addPieSlice(PieModel("Tema 0",(erroresPorTema[0].toFloat()),Color.parseColor("#afe3f5")))
                    pieChart.addPieSlice(PieModel("Tema 1",(erroresPorTema[1].toFloat()),Color.parseColor("#51252d")))
                    pieChart.addPieSlice(PieModel("Tema 2",(erroresPorTema[2].toFloat()),Color.parseColor("#137853")))
                    pieChart.addPieSlice(PieModel("Tema 3",(erroresPorTema[3].toFloat()),Color.parseColor("#bb934f")))
                    pieChart.addPieSlice(PieModel("Tema 4",(erroresPorTema[4].toFloat()),Color.parseColor("#0d17e1")))
                    pieChart.addPieSlice(PieModel("Tema 5",(erroresPorTema[5].toFloat()),Color.parseColor("#bf999b")))
                    pieChart.addPieSlice(PieModel("Tema 6",(erroresPorTema[6].toFloat()),Color.parseColor("#5d753e")))
                    pieChart.addPieSlice(PieModel("Tema 7",(erroresPorTema[7].toFloat()),Color.parseColor("#a3be88")))
                    pieChart.addPieSlice(PieModel("Tema 8",(erroresPorTema[8].toFloat()),Color.parseColor("#347ace")))
                    pieChart.addPieSlice(PieModel("Tema 9",(erroresPorTema[9].toFloat()),Color.parseColor("#8e5f3b")))
                    pieChart.addPieSlice(PieModel("Tema 10",(erroresPorTema[10].toFloat()),Color.parseColor("#527d8b")))
                    pieChart.addPieSlice(PieModel("Tema 11",(erroresPorTema[11].toFloat()),Color.parseColor("#0abf0c")))
                    pieChart.addPieSlice(PieModel("Tema 12",(erroresPorTema[12].toFloat()),Color.parseColor("#47118b")))
                    pieChart.addPieSlice(PieModel("Tema 13",(erroresPorTema[13].toFloat()),Color.parseColor("#d6b191")))

                }
        }

        //quesito media tiempo test
        binding.text3.setOnClickListener {
            binding.text3.background = ContextCompat.getDrawable(requireContext(),R.drawable.border_stats_clicked)
            model.db.collection("stats").document(user!!.email!!).get()
                .addOnSuccessListener {
                    binding.tiempo.visibility = View.VISIBLE
                    val tiempoTotal = it.get("tiempoTotal") as Long
                    println("tiempo total: " +tiempoTotal)
                    val tiempoTotalMinutos = (tiempoTotal/60).toDouble()
                    println("tiempo total minutos: " +tiempoTotalMinutos)
                    val testTotales = ((it.get("testTotales") as Long))
                    println("test totales: " +testTotales)
                    val media = tiempoTotalMinutos/testTotales
                    println("la media es: "+media + " minutos")
                    binding.tiempo.text = media.toInt().toString() +" min"
                }

        }
    }

    private fun resetChar(pieChart: org.eazegraph.lib.charts.PieChart) {
        pieChart.clearChart()
    }

    private fun putFailedAprovedStats(pieChart: org.eazegraph.lib.charts.PieChart) {
        model.db.collection("stats").document(user!!.email!!).get()
            .addOnSuccessListener {
                val testSuspendidos = ((it.get("testSuspendidos") as Long))
                val testAprobados = ((it.get("testAprobados") as Long))
                val testTotales = ((it.get("testTotales") as Long))

                pieChart.addPieSlice(PieModel("Aprobados",testAprobados.toFloat(),Color.parseColor("#1b8001")))
                pieChart.addPieSlice(PieModel("Suspendidos",testSuspendidos.toFloat(),Color.parseColor("#b50404")))
            }
    }
}