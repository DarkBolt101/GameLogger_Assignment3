package com.wst.gamelogger_assignment3.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
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

class FragmentGameList : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var adapter: GameAdapter

    private val viewModel: GameViewModel by viewModels {
        val repo = GameRepository(GameDatabase.getDatabase(requireContext()).gameDao())
        GameViewModelFactory(repo)
    }

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View? =
        inflater.inflate(R.layout.fragment_game_list, c, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler = view.findViewById(R.id.recyclerViewGames)
        addButton = view.findViewById(R.id.button_add_game)

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

        addButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentAddGame())
                .addToBackStack(null)
                .commit()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.activeGames.collect { list ->
                adapter.update(list)
            }
        }
    }
}