package com.mspark.myimage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.mspark.myimage.databinding.FragmentSearchBinding
import com.mspark.myimage.util.Constants.Fragment.SEARCH
import com.mspark.myimage.viewmodel.MainViewModel
import com.mspark.myimage.viewmodel.ViewModelFactory

class SearchFragment: Fragment() {
    private lateinit var binding: FragmentSearchBinding

    private val imageAdapter by lazy {
        ImageAdapter(SEARCH)
    }

    private val viewModel by lazy {
        ViewModelProvider(
            activity ?: requireActivity(),
            ViewModelFactory(context ?: requireContext())
        ).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FragmentSearchBinding.inflate(inflater).apply {
            binding = this
            binding.lifecycleOwner = viewLifecycleOwner
            binding.view = viewModel
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setImageAdapter()

        setObserver()

        setEditText()
    }

    private fun setImageAdapter() {
        binding.searchRecyclerView.itemAnimator = null
        binding.searchRecyclerView.adapter = imageAdapter

        imageAdapter.apply {
            onClickLike = { position ->

                viewModel.onClickLikeOnSearch(position)
            }
        }

        binding.searchRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1)) {
                    Log.d("@@ SearchFragment", "setImageAdapter, onScrollStateChanged, canScrollVertically: false")
                    viewModel.getMoreImage()
                }
            }
        })
    }

    private fun setObserver() {
        viewModel.imageList.observe(viewLifecycleOwner) {
            lifecycleScope.launchWhenCreated {
                Log.d("@@ SearchFragment", "setObserver, imageList: ${it.size}")
                imageAdapter.submitList(it)
            }
        }
    }

    private fun setEditText() {
        binding.searchEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.searchNewQuery(binding.searchEdit.text.toString())
                hideKeyboard()
            }
            true
        }
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEdit.windowToken, 0)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }
}