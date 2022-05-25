package com.project.carunlock.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import com.project.carunlock.R
import com.project.carunlock.view.TemaAdapter
import com.project.carunlock.databinding.FragmentTemaInfoBinding
import com.project.carunlock.viewmodel.CarUnlockViewModel
import java.io.File

class TemaInfoFragment : Fragment() {

    private lateinit var temaAdapter: TemaAdapter
    private lateinit var binding: FragmentTemaInfoBinding
    private val model: CarUnlockViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentTemaInfoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.selectedTema.observe(viewLifecycleOwner, Observer{
          loadFileFromUrl(it.url)
        })

        binding.goBack.setOnClickListener {
            replaceFragment(TemarioFragment())
        }
    }

    private fun loadFileFromUrl(url:String) {
        FileLoader.with(context)
            .load(url) //2nd parameter is optioal, pass true to force load from network
            .fromDirectory("test4", FileLoader.DIR_INTERNAL)
            .asFile(object : FileRequestListener<File?> {
                override fun onLoad(request: FileLoadRequest, response: FileResponse<File?>) {
                    val loadedFile = response.body
                    binding.pdfView.fromFile(loadedFile).load()
//                  FOR NIGHT MODE PUT: binding.pdfView.fromFile(loadedFile).nightMode(true).load()
                }

                override fun onError(request: FileLoadRequest, t: Throwable) {}
            })
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.commit()
    }
}