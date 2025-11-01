package com.wst.gamelogger_assignment3.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.wst.gamelogger_assignment3.R
import com.wst.gamelogger_assignment3.adapter.GameAdapter
import com.wst.gamelogger_assignment3.data.GameDatabase
import com.wst.gamelogger_assignment3.repository.GameRepository
import com.wst.gamelogger_assignment3.viewmodel.GameViewModel
import com.wst.gamelogger_assignment3.viewmodel.GameViewModelFactory
import kotlinx.coroutines.launch

class FragmentCompletedGames : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var adapter: GameAdapter

    private val viewModel: GameViewModel by viewModels {
        val repo = GameRepository(GameDatabase.getDatabase(requireContext()).gameDao())
        GameViewModelFactory(repo)
    }

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View? =
        inflater.inflate(R.layout.fragment_completed_games, c, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("FragmentCompletedGames", "onViewCreated started")
        recycler = view.findViewById(R.id.recyclerViewCompleted)
        emptyText = view.findViewById(R.id.text_empty_completed)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = GameAdapter(
            games = emptyList(),
            onDelete = { game ->
                viewModel.deleteGame(game)
                Snackbar.make(requireView(), "Game deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        viewModel.insertGame(game)
                        Toast.makeText(requireContext(), "Game restored", Toast.LENGTH_SHORT).show()
                    }.show()
            },
            onItemClick = { game ->
                Log.d("FragmentCompletedGames", "Item clicked: ${game.title}")
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, FragmentGameDetails.newInstance(game.id))
                    .addToBackStack(null)
                    .commit()
            },
            onToggleComplete = { game, checked ->
                viewModel.toggleCompleted(game, checked)
                if (checked) {
                    Toast.makeText(requireContext(), "Game marked as completed", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Game marked as active", Toast.LENGTH_SHORT).show()
                }
            }
        )
        recycler.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d("FragmentCompletedGames", "Collecting completedGames flow")
                viewModel.completedGames.collect { list ->
                    Log.d("FragmentCompletedGames", "Has ${list.size} completed games")
                    adapter.update(list)
                    emptyText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            } catch (e: Exception) {
                Log.e("FragmentCompletedGames", "Error collecting completedGames", e)
                Snackbar.make(requireView(), "Error loading list", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}