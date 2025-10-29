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
import com.wst.gamelogger_assignment3.*
import com.wst.gamelogger_assignment3.api.RawgService
import com.wst.gamelogger_assignment3.data.GameDatabase
import com.wst.gamelogger_assignment3.repository.GameRepository
import com.wst.gamelogger_assignment3.viewmodel.GameViewModel
import com.wst.gamelogger_assignment3.viewmodel.GameViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentAddGame : Fragment() {

    private lateinit var titleEdit: EditText
    private lateinit var platformEdit: EditText
    private lateinit var overviewEdit: EditText
    private lateinit var notesEdit: EditText
    private lateinit var coverImage: ImageView
    private lateinit var achievementsContainer: LinearLayout
    private lateinit var addButton: Button
    private lateinit var cancelButton: Button
    private lateinit var addAchievementBtn: Button

    private val apiKey = "ae45e25d72b34e98b61b5c0dbae85885"

    private val viewModel: GameViewModel by viewModels {
        val repo = GameRepository(GameDatabase.getDatabase(requireContext()).gameDao())
        GameViewModelFactory(repo)
    }

    private val api = RawgService.create()

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View? =
        inflater.inflate(R.layout.fragment_add_game, c, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        titleEdit = view.findViewById(R.id.edit_game_title)
        platformEdit = view.findViewById(R.id.edit_game_platform)
        overviewEdit = view.findViewById(R.id.edit_game_overview)
        notesEdit = view.findViewById(R.id.edit_game_notes)
        coverImage = view.findViewById(R.id.image_cover)
        achievementsContainer = view.findViewById(R.id.achievements_container)
        addButton = view.findViewById(R.id.button_add)
        cancelButton = view.findViewById(R.id.button_cancel)
        addAchievementBtn = view.findViewById(R.id.button_add_achievement)

        // When title loses focus, try fetch data automatically
        titleEdit.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && titleEdit.text.toString().isNotBlank()) {
                fetchFromApi(titleEdit.text.toString())
            }
        }

        addAchievementBtn.setOnClickListener {
            val et = EditText(requireContext()).apply { hint = "Achievement name" }
            val btn = Button(requireContext()).apply { text = "Remove" }
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                addView(CheckBox(requireContext()))
                addView(et, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
                addView(btn)
            }
            btn.setOnClickListener { achievementsContainer.removeView(row) }
            achievementsContainer.addView(row)
        }

        cancelButton.setOnClickListener { parentFragmentManager.popBackStack() }

        addButton.setOnClickListener {
            val title = titleEdit.text.toString().trim()
            val platform = platformEdit.text.toString().trim()
            val overview = overviewEdit.text.toString().trim()

            // Stricter validation
            if (title.isEmpty() || platform.isEmpty() || overview.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all required fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Gather achievements
            val achievements = mutableListOf<Achievement>()
            for (i in 0 until achievementsContainer.childCount) {
                val child = achievementsContainer.getChildAt(i)
                when (child) {
                    is ViewGroup -> {
                        val cb = child.getChildAt(0) as CheckBox
                        val et = child.getChildAt(1) as? EditText
                        val name = et?.text?.toString()?.takeIf { it.isNotBlank() } ?: continue
                        achievements.add(Achievement(name, cb.isChecked))
                    }
                    is CheckBox -> {
                        val name = child.text.toString().takeIf { it.isNotBlank() } ?: continue
                        achievements.add(Achievement(name, child.isChecked))
                    }
                }
            }

            val g = Game(
                title = title,
                platform = platform,
                genre = "", // filled later if fetched
                overview = overview,
                imageUrl = coverImage.tag as? String,
                achievements = achievements,
                notes = notesEdit.text.toString().takeIf { it.isNotBlank() },
                completed = false
            )

            lifecycleScope.launch {
                viewModel.insertGame(g)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Game saved successfully!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    // --- Helper UI and API logic ---
    private fun addAchievementCheckbox(name: String, completed: Boolean = false) {
        val cb = CheckBox(requireContext()).apply {
            text = name
            isChecked = completed
        }
        achievementsContainer.addView(cb)
    }

    private fun addAchievementRow(name: String, completed: Boolean) {
        val cb = CheckBox(requireContext())
        val et = EditText(requireContext()).apply { setText(name) }
        val remove = Button(requireContext()).apply { text = "Remove" }
        val row = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(cb)
            addView(et, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            addView(remove)
        }
        cb.isChecked = completed
        remove.setOnClickListener { achievementsContainer.removeView(row) }
        achievementsContainer.addView(row)
    }

    private fun clearAchievementsUI() {
        achievementsContainer.removeAllViews()
    }

    private fun fillAchievementsFromList(list: List<Achievement>) {
        clearAchievementsUI()
        list.forEach { addAchievementRow(it.name, it.completed) }
    }

    private fun fillSimpleCheckboxAchievements(names: List<String>) {
        clearAchievementsUI()
        names.forEach { addAchievementCheckbox(it, false) }
    }

    private fun setOverviewAndImage(desc: String?, imageUrl: String?) {
        if (!desc.isNullOrBlank()) overviewEdit.setText(desc)
        if (!imageUrl.isNullOrBlank()) {
            coverImage.load(imageUrl)
            coverImage.tag = imageUrl
        }
    }

    private fun fetchFromApi(title: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val search = api.searchGames(title, apiKey)
                val item = search.results.firstOrNull()
                if (item != null) {
                    val idOrSlug = item.slug ?: item.id?.toString() ?: item.name ?: ""
                    val details = try {
                        api.getGameDetails(idOrSlug, apiKey)
                    } catch (_: Throwable) { null }

                    val achNames = try {
                        val res = api.getAchievements(idOrSlug, apiKey)
                        res.results.mapNotNull { it.name }
                    } catch (_: Throwable) { emptyList<String>() }

                    withContext(Dispatchers.Main) {
                        details?.let {
                            titleEdit.setText(it.name ?: title)
                            overviewEdit.setText(it.description_raw ?: overviewEdit.text.toString())
                            val genres = it.genres?.joinToString(", ") { g -> g.name } ?: ""
                            coverImage.load(it.background_image)
                            coverImage.tag = it.background_image
                            if (achNames.isNotEmpty()) fillSimpleCheckboxAchievements(achNames)
                        } ?: run {
                            titleEdit.setText(item.name ?: title)
                            coverImage.load(item.background_image)
                            coverImage.tag = item.background_image
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "No match found", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}