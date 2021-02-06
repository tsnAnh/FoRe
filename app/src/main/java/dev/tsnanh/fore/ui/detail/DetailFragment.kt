package dev.tsnanh.fore.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import dev.tsnanh.fore.State
import dev.tsnanh.fore.customview.MainToolbar
import dev.tsnanh.fore.databinding.FragmentDetailBinding
import dev.tsnanh.fore.ui.detail.adapter.PhotoAdapter
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailFragment : Fragment() {

    companion object {
        fun newInstance() = DetailFragment()
    }

    private val viewModel: DetailViewModel by viewModel()
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var photosAdapter: PhotoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photosAdapter = PhotoAdapter()

        with(binding) {
            with(mainToolbar2) {
                this.isLeftButtonEnabled = true
                this.onNavigateUp = MainToolbar.OnActionClick { findNavController().navigateUp() }
            }
            with(phos) {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                val helper = PagerSnapHelper()
                helper.attachToRecyclerView(this)
                adapter = photosAdapter
            }
        }
        observe()
    }

    private fun observe() {
        val postId = DetailFragmentArgs.fromBundle(requireArguments()).postId
        lifecycleScope.launchWhenStarted {
            viewModel.getPost(postId).collect { state ->
                when (state) {
                    is State.Loading -> Unit
                    is State.Error -> println(state.error)
                    is State.Success -> {
                        with(binding) {
                            photosAdapter.submitList(state.data.photos.sortedBy { it.index }
                                .map { it.data }
                                .zip(state.data.foodTags.sortedBy { it.index }.map { it.data }))
                            tit.text = state.data.postTitle.also {
                                mainToolbar2.setTitle(it)
                            }
                            des.text = state.data.postDescription
                            add.text = "Address: ${state.data.address}"
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}