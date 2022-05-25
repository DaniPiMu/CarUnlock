package com.project.carunlock.view

import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.carunlock.OnClickListener
import com.project.carunlock.R
import com.project.carunlock.databinding.ItemTemaBinding
import com.project.carunlock.model.Tema


class TemaAdapter(private val temas: List<Tema>, private val listener: OnClickListener):
    RecyclerView.Adapter<TemaAdapter.ViewHolder>() {
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemTemaBinding.bind(view)

        fun setListener(tema: Tema) {
            binding.root.setOnClickListener {
                listener.onClick(tema)
            }
        }
    }

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_tema, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tema = temas[position]
        with(holder) {
            setListener(tema)
            binding.temaName.text = tema.name
            binding.temaDescription.text = tema.description
            binding.temaIcon.setImageResource(tema.icon)
        }
    }

    override fun getItemCount(): Int {
        return temas.size
    }
}