package com.mspark.myimage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mspark.myimage.databinding.ActivityMainBinding
import com.mspark.myimage.repository.MainRepositoryImpl
import com.mspark.myimage.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel by lazy {
        MainViewModel(MainRepositoryImpl.getRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainNavigationView.run {
            setOnItemSelectedListener {
                when(it.itemId) {
                    R.id.searchTab -> {
                        moveFragment(SearchFragment.newInstance())
                        true
                    }
                    R.id.myImageTab -> {
                        moveFragment(MyImageFragment.newInstance())
                        true
                    }
                    else -> false
                }
            }

            selectedItemId = R.id.searchTab
        }
    }

    private fun moveFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, fragment)
            .commit()
    }
}