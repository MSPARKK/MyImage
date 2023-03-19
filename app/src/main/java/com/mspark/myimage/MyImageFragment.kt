package com.mspark.myimage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mspark.myimage.databinding.FragmentMyImageBinding
import com.mspark.myimage.util.Constants.Fragment.MY_IMAGE
import com.mspark.myimage.viewmodel.MainViewModel
import com.mspark.myimage.viewmodel.ViewModelFactory

class MyImageFragment: Fragment() {
    private lateinit var binding: FragmentMyImageBinding

    private val imageAdapter by lazy {
        ImageAdapter(MY_IMAGE)
    }

    private val viewModel by lazy {
        ViewModelProvider(
            activity ?: requireActivity(),
            ViewModelFactory(context ?: requireContext())
        ).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FragmentMyImageBinding.inflate(inflater).apply {
            binding = this
            binding.lifecycleOwner = viewLifecycleOwner
            binding.view = viewModel
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setImageAdapter()

        setObserver()

        viewModel.getMyImage()
    }

    private fun setImageAdapter() {
        binding.myImageRecyclerView.itemAnimator = null
        binding.myImageRecyclerView.adapter = imageAdapter

        imageAdapter.apply {
            onClickLike = { position ->

//                viewModel.onClickLike(position)
            }
        }
    }

    private fun setObserver() {
        viewModel.myImageList.observe(viewLifecycleOwner) {
            lifecycleScope.launchWhenCreated {
                Log.d("@@ MyImageFragment", "setObserver, imageList: ${it.size}")
                imageAdapter.submitList(it)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyImageFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }
}