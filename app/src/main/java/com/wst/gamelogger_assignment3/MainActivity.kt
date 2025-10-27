package com.wst.gamelogger_assignment3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wst.gamelogger_assignment3.ui.fragments.FragmentGameList

class MainActivity : AppCompatActivity() {
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setContentView(R.layout.activity_main)

        if (s == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentGameList())
                .commit()
        }
    }
}