package com.mspark.myimage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mspark.myimage.databinding.ActivityMainBinding
import com.mspark.myimage.repository.MainRepositoryImpl
import com.mspark.myimage.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var showingFragment: Fragment? = null

    private val searchFragment by lazy {
        SearchFragment.newInstance()
    }

    private val myImageFragment by lazy {
        MyImageFragment.newInstance()
    }

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
                        moveFragment(searchFragment)
                        true
                    }
                    R.id.myImageTab -> {
                        moveFragment(myImageFragment)
                        true
                    }
                    else -> false
                }
            }

            selectedItemId = R.id.searchTab
        }
    }

    private fun moveFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        if (showingFragment != fragment) {

            val foundFragment = supportFragmentManager.findFragmentByTag(fragment.javaClass.name)
            if (foundFragment == null) {
                transaction.add(R.id.mainContainer, fragment, fragment.javaClass.name)
            }

            showingFragment?.let {
                transaction.hide(it)
            }

            transaction.show(fragment)

            transaction.commitNowAllowingStateLoss()
            showingFragment = fragment

        }

    }
}