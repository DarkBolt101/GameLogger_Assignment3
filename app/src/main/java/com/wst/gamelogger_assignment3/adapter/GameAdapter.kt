package com.wst.gamelogger_assignment3.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.wst.gamelogger_assignment3.Game
import com.wst.gamelogger_assignment3.R

class GameAdapter(
    private var games: List<Game>,
    private val onDelete: (Game) -> Unit,
    private val onClick: (Game) -> Unit
) : RecyclerView.Adapter<GameAdapter.Holder>() {

    class Holder(v: View) : RecyclerView.ViewHolder(v) {
        val image: ImageView = v.findViewById(R.id.image_game_cover)
        val title: TextView = v.findViewById(R.id.text_game_title)
        val platform: TextView = v.findViewById(R.id.text_game_platform)
        val genre: TextView = v.findViewById(R.id.text_game_genre)
        val overview: TextView = v.findViewById(R.id.text_game_overview)
        val delete: Button = v.findViewById(R.id.button_delete_game)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val g = games[position]
        holder.title.text = g.title
        holder.platform.text = g.platform
        holder.genre.text = g.genre
        holder.overview.text = g.overview ?: ""
        holder.image.load(g.imageUrl ?: R.drawable.ic_launcher_background)
        holder.delete.setOnClickListener { onDelete(g) }
        holder.itemView.setOnClickListener { onClick(g) }
    }

    override fun getItemCount(): Int = games.size

    fun update(list: List<Game>) {
        this.games = list
        notifyDataSetChanged()
    }
}