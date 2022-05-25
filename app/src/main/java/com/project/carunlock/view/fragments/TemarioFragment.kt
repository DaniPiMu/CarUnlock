package com.project.carunlock.view.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.carunlock.OnClickListener
import com.project.carunlock.R
import com.project.carunlock.view.TemaAdapter
import com.project.carunlock.databinding.FragmentTemarioBinding
import com.project.carunlock.model.Question
import com.project.carunlock.model.Tema
import com.project.carunlock.viewmodel.CarUnlockViewModel

class TemarioFragment : Fragment(), OnClickListener {

    private lateinit var temaAdapter: TemaAdapter
    private lateinit var binding: FragmentTemarioBinding

    private val model: CarUnlockViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentTemarioBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.temas.value?.let {
            setUpRecyclerView(it)
        }
        model.temas.observe(viewLifecycleOwner, Observer{
            if (model.temas ==null){
                Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
            }
            else{
                if (it != null){
                    setUpRecyclerView(it)
                }
            }
        })
    }

    private fun setUpRecyclerView(myData: List<Tema>) {
        temaAdapter = TemaAdapter(myData, this)
        binding.recyclerView.apply {
            setHasFixedSize(true) //Optimitza el rendiment de l’app
            layoutManager = LinearLayoutManager(context)
            adapter = temaAdapter
        }
    }

    override fun onClick(tema: Tema) {
        model.setTema(tema)
        replaceFragment(TemaInfoFragment())
    }

    override fun onClickQuestion(question: Question) {
        TODO("Not yet implemented")
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.commit()
    }

}