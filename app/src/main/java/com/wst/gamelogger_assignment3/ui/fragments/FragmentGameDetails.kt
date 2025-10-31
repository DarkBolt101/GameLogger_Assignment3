package com.wst.gamelogger_assignment3.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.wst.gamelogger_assignment3.Achievement
import com.wst.gamelogger_assignment3.Game
import com.wst.gamelogger_assignment3.R
import com.wst.gamelogger_assignment3.data.GameDatabase
import com.wst.gamelogger_assignment3.repository.GameRepository
import com.wst.gamelogger_assignment3.viewmodel.GameViewModel
import com.wst.gamelogger_assignment3.viewmodel.GameViewModelFactory
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

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

        // Combine both lists so this fragment can show any game
        viewLifecycleOwner.lifecycleScope.launch {
            combine(viewModel.activeGames, viewModel.completedGames) { active, completed ->
                active + completed
            }.collect { list ->
                val game = list.find { it.id == gameId } ?: return@collect
                bindGame(game)
            }
        }

        saveBtn.setOnClickListener {
            val currentGame = collectGameDataFromUI()
            viewModel.updateGame(currentGame)
            Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
        }

        cancelBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun collectGameDataFromUI(): Game {
        val ach = mutableListOf<Achievement>()
        for (i in 0 until achievementsContainer.childCount) {
            val viewRow = achievementsContainer.getChildAt(i)
            when (viewRow) {
                is CheckBox -> {
                    val name = viewRow.text.toString()
                    ach.add(Achievement(name, viewRow.isChecked))
                }
                is ViewGroup -> {
                    val cb = viewRow.getChildAt(0) as CheckBox
                    val et = viewRow.getChildAt(1) as EditText
                    ach.add(Achievement(et.text.toString(), cb.isChecked))
                }
            }
        }

        return Game(
            id = gameId,
            title = titleTv.text.toString(),
            platform = platformTv.text.toString(),
            genre = genreTv.text.toString(),
            overview = overviewTv.text.toString(),
            imageUrl = image.tag as? String,
            achievements = ach,
            notes = notesEdit.text.toString()
        )
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
            val cb = CheckBox(requireContext()).apply {
                text = ach.name
                isChecked = ach.completed
            }
            achievementsContainer.addView(cb)
        }
    }
}