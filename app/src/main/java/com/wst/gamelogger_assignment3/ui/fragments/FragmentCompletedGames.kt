package com.wst.gamelogger_assignment3.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        recycler = view.findViewById(R.id.recyclerViewCompleted)
        emptyText = view.findViewById(R.id.text_empty_completed)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = GameAdapter(
            games = emptyList(),
            onDelete = { game -> viewModel.deleteGame(game) },
            onItemClick = { game ->
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, FragmentGameDetails.newInstance(game.id))
                    .addToBackStack(null)
                    .commit()
            },
            onToggleComplete = { game, checked ->
                // If unchecked here, it moves back to Active list.
                viewModel.toggleCompleted(game, checked)
            }
        )
        recycler.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.completedGames.collect { list ->
                adapter.update(list)
                emptyText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }
}