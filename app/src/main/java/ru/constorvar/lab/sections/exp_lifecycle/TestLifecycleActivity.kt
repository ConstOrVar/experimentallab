package ru.constorvar.lab.sections.exp_lifecycle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.constorvar.lab.R

internal class TestLifecycleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_lifecycle)
    }

}