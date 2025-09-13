package com.example.mymemory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(
    private val cards: List<Card>,
    private val listener: CardClick
) : RecyclerView.Adapter<CardAdapter.VH>() {

    interface CardClick { fun onCardClicked(position: Int) }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgCard)
        init {
            v.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) listener.onCardClicked(pos)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = cards[position]
        holder.img.setImageResource(if (c.isFaceUp || c.isMatched) c.frontRes else R.drawable.ic_card_back)
        holder.itemView.alpha = if (c.isMatched) 0.4f else 1f
    }

    override fun getItemCount() = cards.size
}
