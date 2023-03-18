package com.mspark.myimage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mspark.myimage.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainNavigationView.run {
            setOnItemSelectedListener {
                when(it.itemId) {
                    R.id.searchTab -> {
                        supportFragmentManager.beginTransaction()
                            .replace(com.mspark.myimage.R.id.mainContainer, MyImageFragment())
                            .commit()
                        true
                    }
                    R.id.myImageTab -> {
                        supportFragmentManager.beginTransaction()
                            .replace(com.mspark.myimage.R.id.mainContainer, SearchFragment())
                            .commit()
                        true
                    }
                    else -> false
                }

            }

            selectedItemId = R.id.searchTab
        }
    }
}