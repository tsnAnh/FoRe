package dev.tsnanh.fore.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dev.tsnanh.fore.State
import dev.tsnanh.fore.databinding.FragmentHomeBinding
import dev.tsnanh.fore.ui.home.adapter.PostAdapter
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    private lateinit var postAdapter: PostAdapter
    private val homeViewModel: HomeViewModel by viewModel()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        with(binding) {
            progressBar.isVisible = false
            with(toolbar) {
                setTitle("Home")
                setOnActionClick {
                    findNavController()
                        .navigate(HomeFragmentDirections.actionNavigationHomeToNavigationAddFood())
                }
            }
            postAdapter = PostAdapter {
                homeViewModel.onNavigateToDetail(it.postId)
            }
            with(posts) {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(requireContext(), 2)
                adapter = postAdapter
            }
        }
    }

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            homeViewModel.posts.collect {
                when (it) {
                    is State.Loading -> binding.progressBar.isVisible = true
                    is State.Error -> println("Error: ${it.error}")
                    is State.Success -> {
                        binding.progressBar.isVisible = false
                        postAdapter.submitList(it.data)
                    }
                }
            }
        }
        homeViewModel.navigateToDetail.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavigationHomeToNavigationDetail(it)
                )
                homeViewModel.onNavigatedToDetail()
            }
        }
    }
}