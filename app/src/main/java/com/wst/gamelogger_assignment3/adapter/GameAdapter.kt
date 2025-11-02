package com.wst.gamelogger_assignment3.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.wst.gamelogger_assignment3.Game
import com.wst.gamelogger_assignment3.R

class GameAdapter(
    private var games: List<Game>,
    private val onDelete: (Game) -> Unit,
    private val onItemClick: (Game) -> Unit,
    private val onToggleComplete: (Game, Boolean) -> Unit
) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    // --- Coil Cache Toggle (Investigation)---
    // Set to TRUE to enable Coil caching (default behaviour)
    // Set to FALSE to disable Coil caching (for investigation purposes)
    private val ENABLE_COIL_CACHE = true // Set to FALSE to disable Coil caching (ONLY for investigation purposes)

    class GameViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image_game_cover)
        val title: TextView = view.findViewById(R.id.text_game_title)
        val platform: TextView = view.findViewById(R.id.text_game_platform)
        val genre: TextView = view.findViewById(R.id.text_game_genre)
        val overview: TextView = view.findViewById(R.id.text_game_overview)
        val completedCheck: CheckBox = view.findViewById(R.id.checkbox_completed)
        val deleteButton: Button = view.findViewById(R.id.button_delete_game)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]

        // --- Text Data ---
        holder.title.text = game.title
        holder.platform.text = game.platform
        holder.genre.text = game.genre
        holder.overview.text = game.overview ?: ""

        // --- Image Data (Loading Investigation) ---
        if (ENABLE_COIL_CACHE) {
            holder.image.load(game.imageUrl)
        } else {
            holder.image.load(game.imageUrl) {
                memoryCachePolicy(CachePolicy.DISABLED)
                diskCachePolicy(CachePolicy.DISABLED)
            }
        }

        // --- Completion Checkbox ---
        holder.completedCheck.setOnCheckedChangeListener(null)
        holder.completedCheck.isChecked = game.completed
        holder.completedCheck.setOnCheckedChangeListener { _, checked ->
            onToggleComplete(game, checked)
        }

        // --- Delete Button ---
        holder.deleteButton.setOnClickListener { onDelete(game) }
        holder.itemView.setOnClickListener { onItemClick(game) }
    }

    override fun getItemCount(): Int = games.size

    fun update(newGames: List<Game>) {
        games = newGames
        notifyDataSetChanged()
    }
}