package com.example.mymemory.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mymemory.model.Score

class ScoreAdapter(private val data: List<Score>) :
    RecyclerView.Adapter<ScoreAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val t1: TextView = v.findViewById(android.R.id.text1)
        val t2: TextView = v.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(p: ViewGroup, vt: Int): VH {
        val v = LayoutInflater.from(p.context)
            .inflate(android.R.layout.simple_list_item_2, p, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, i: Int) {
        val s = data[i]
        val secs = s.millis / 1000
        h.t1.text = "${s.name}  •  Movs: ${s.moves}  |  Tiempo: ${secs}s"
        h.t2.text = s.createdAt
    }

    override fun getItemCount() = data.size
}
