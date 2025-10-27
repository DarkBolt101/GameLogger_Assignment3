package com.wst.gamelogger_assignment3.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.wst.gamelogger_assignment3.Achievement
import com.wst.gamelogger_assignment3.Game
import com.wst.gamelogger_assignment3.R
import com.wst.gamelogger_assignment3.data.GameDatabase
import com.wst.gamelogger_assignment3.repository.GameRepository
import com.wst.gamelogger_assignment3.viewmodel.GameViewModel
import com.wst.gamelogger_assignment3.viewmodel.GameViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

class FragmentGameDetails : Fragment() {

    private var gameId: Int = -1
    private lateinit var image: ImageView
    private lateinit var titleTv: TextView
    private lateinit var platformTv: TextView
    private lateinit var genreTv: TextView
    private lateinit var overviewTv: TextView
    private lateinit var notesEdit: EditText
    private lateinit var achievementsContainer: LinearLayout
    private lateinit var saveBtn: Button
    private lateinit var cancelBtn: Button

    private val viewModel: GameViewModel by viewModels {
        val repo = GameRepository(GameDatabase.getDatabase(requireContext()).gameDao())
        GameViewModelFactory(repo)
    }

    companion object {
        private const val ARG_ID = "game_id"
        fun newInstance(id: Int) = FragmentGameDetails().apply {
            arguments = Bundle().apply { putInt(ARG_ID, id) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameId = arguments?.getInt(ARG_ID) ?: -1
    }

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View? =
        inflater.inflate(R.layout.fragment_game_details, c, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        image = view.findViewById(R.id.image_game_cover)
        titleTv = view.findViewById(R.id.text_game_title)
        platformTv = view.findViewById(R.id.text_game_platform)
        genreTv = view.findViewById(R.id.text_game_genre)
        overviewTv = view.findViewById(R.id.text_game_overview)
        notesEdit = view.findViewById(R.id.edit_game_notes)
        achievementsContainer = view.findViewById(R.id.achievements_container)
        saveBtn = view.findViewById(R.id.button_save)
        cancelBtn = view.findViewById(R.id.button_cancel)

        // observe the list and display the selected game
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allGames.collect { list ->
                val game = list.find { it.id == gameId } ?: return@collect
                bindGame(game)
            }
        }

        saveBtn.setOnClickListener {
            // read achievements / notes, create updated Game copy and update
            val currentGame = run {
                // read displayed fields, but simplest: re-create from UI
                val title = titleTv.text.toString()
                val platform = platformTv.text.toString()
                val genre = genreTv.text.toString()
                val overview = overviewTv.text.toString()
                val notes = notesEdit.text.toString()
                val ach = mutableListOf<Achievement>()
                for (i in 0 until achievementsContainer.childCount) {
                    val viewRow = achievementsContainer.getChildAt(i)
                    when (viewRow) {
                        is CheckBox -> {
                            val name = viewRow.text.toString()
                            ach.add(Achievement(name, viewRow.isChecked))
                        }
                        is ViewGroup -> {
                            // row with [CheckBox, EditText, Remove]
                            val cb = viewRow.getChildAt(0) as CheckBox
                            val et = viewRow.getChildAt(1) as EditText
                            ach.add(Achievement(et.text.toString(), cb.isChecked))
                        }
                    }
                }
                Game(id = gameId, title = title, platform = platform, genre = genre, overview = overview, imageUrl = image.tag as? String, achievements = ach, notes = notes)
            }
            viewModel.updateGame(currentGame)
            Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
            // remain on screen; if you want to pop back, call popBackStack()
        }

        cancelBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun bindGame(game: Game) {
        image.load(game.imageUrl ?: R.drawable.ic_launcher_background)
        image.tag = game.imageUrl
        titleTv.text = game.title
        platformTv.text = game.platform
        genreTv.text = game.genre
        overviewTv.text = game.overview ?: ""
        notesEdit.setText(game.notes ?: "")

        achievementsContainer.removeAllViews()
        game.achievements.forEach { ach ->
            // create editable row: CheckBox + EditText + Remove
            val cb = CheckBox(requireContext())
            cb.isChecked = ach.completed
            cb.text = ach.name
            // simpler: show as checkbox lines; user can toggle (and we store name as text)
            achievementsContainer.addView(cb)
        }
    }
}