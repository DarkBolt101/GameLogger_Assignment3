package com.wst.gamelogger_assignment3

import android.content.Context
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wst.gamelogger_assignment3.ui.fragments.FragmentCompletedGames
import com.wst.gamelogger_assignment3.ui.fragments.FragmentGameList

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var themeSwitch: Switch
    private lateinit var titleText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply last saved theme first
        applySavedTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_nav)
        themeSwitch = findViewById(R.id.switch_theme)
        titleText = findViewById(R.id.toolbar_title)

        // init fragment only once
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentGameList())
                .commit()
            titleText.text = "Game Logger"
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_active -> {
                    titleText.text = "Game Logger"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, FragmentGameList())
                        .commit()
                    true
                }
                R.id.nav_completed -> {
                    titleText.text = "Completed Games"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, FragmentCompletedGames())
                        .commit()
                    true
                }
                else -> false
            }
        }

        // Set switch state from prefs and toggle
        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        themeSwitch.isChecked = prefs.getBoolean("dark_mode", false)
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            // No need to recreate aggressively; AppCompat handles it.
        }
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val dark = prefs.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (dark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}