package com.wst.gamelogger_assignment3.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wst.gamelogger_assignment3.R
import com.wst.gamelogger_assignment3.adapter.GameAdapter
import com.wst.gamelogger_assignment3.data.GameDatabase
import com.wst.gamelogger_assignment3.repository.GameRepository
import com.wst.gamelogger_assignment3.viewmodel.GameViewModel
import com.wst.gamelogger_assignment3.viewmodel.GameViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

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
        adapter = GameAdapter(emptyList(), onDelete = { game ->
            viewModel.deleteGame(game)
        }, onClick = { game ->
            // open details fragment (pass id)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentGameDetails.newInstance(game.id))
                .addToBackStack(null)
                .commit()
        })
        recycler.adapter = adapter

        addButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentAddGame())
                .addToBackStack(null)
                .commit()
        }

        // collect StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allGames.collect { list ->
                adapter.update(list)
            }
        }
    }
}