package dev.tsnanh.fore.ui.addfood

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import dev.tsnanh.fore.Post
import dev.tsnanh.fore.R
import dev.tsnanh.fore.State
import dev.tsnanh.fore.customview.MainToolbar
import dev.tsnanh.fore.databinding.FragmentAddFoodBinding
import dev.tsnanh.fore.fud
import dev.tsnanh.fore.ml.FoodModel
import dev.tsnanh.fore.ui.addfood.adapter.FoodPhotosAdapter
import dev.tsnanh.fore.util.extension.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.model.Model
import java.util.*

class AddFoodFragment : Fragment() {

    companion object {
        fun newInstance() = AddFoodFragment()
    }

    private var job: Job? = null
    private lateinit var model: FoodModel
    private var _binding: FragmentAddFoodBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddFoodViewModel by viewModel()
    private lateinit var photosAdapter: FoodPhotosAdapter
    private val getContents =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            it?.let {
                viewModel.addAllPhotos(it)
            }
        }
    private val permission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

        }
    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        model.close()
        job?.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddFoodBinding.inflate(inflater, container, false)
        locationProvider = FusedLocationProviderClient(requireContext())
        loadingDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(R.layout.layout_progress)
            .setCancelable(false)
            .create()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        loadingDialog.hide()
        with(binding) {
            with(mainToolbar) {
                isLeftButtonEnabled = true
                isSecondaryButtonEnabled = false
                onNavigateUp = MainToolbar.OnActionClick {
                    findNavController().navigateUp()
                }
                onSecondaryClick = MainToolbar.OnActionClick {
                    getContents.launch("image/*")
                }
                setTitle("Add Food")
                setOnActionClick {
                    lifecycleScope.launch {
                        if (ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            permission.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        } else {
                            val position = locationProvider.lastLocation.await()
                            val post = Post(
                                "",
                                binding.postTitle.text.toString(),
                                binding.postDesc.text.toString(),
                                emptyList(),
                                position.latitude.toFloat(),
                                position.longitude.toFloat(),
                                photosAdapter.currentList.mapIndexed { i, e -> i fud e.first},
                                binding.address.text.toString(),
                                Date(),
                                0L
                            )
                            if (viewModel.photos.value.isNotEmpty()) {
                                addPost(post, viewModel.photos.value)
                            } else {
                                Snackbar
                                    .make(binding.root,
                                        "Please choose at least one photo!",
                                        Snackbar.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }
            with(photo) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                PagerSnapHelper().also {
                    it.attachToRecyclerView(this)
                }
            }
            with(expire) {
                setOnClickListener {
                    MaterialDatePicker.Builder.datePicker().build().show(childFragmentManager, "datePicker")
                }
            }
        }
    }

    private suspend fun addPost(post: Post, photos: List<Uri>) {
        viewModel.addPost(post, photos)
            .collect { state ->
                when (state) {
                    is State.Loading -> loadingDialog.show()
                    is State.Error -> println(state.error)
                    is State.Success -> withContext(Dispatchers.Main) {
                        loadingDialog.dismiss()
                        findNavController().navigateUp()
                    }
                }
            }
    }

    private fun observe() {
        job = lifecycleScope.launchWhenStarted {
            loadingDialog.show()
            withContext(Dispatchers.IO) {
                model = FoodModel.newInstance(
                    requireContext(),
                    Model.Options.Builder().setDevice(Model.Device.GPU).setNumThreads(1).build()
                )
                withContext(Dispatchers.Main) {
                    binding.mainToolbar.isSecondaryButtonEnabled = true
                    photosAdapter = FoodPhotosAdapter(model)
                    binding.photo.adapter = photosAdapter
                    loadingDialog.hide()
                }
            }
            viewModel.photos.collect { list ->
                photosAdapter.submitList(list.map { uri ->
                    val probability =
                        model.process(TensorImage.fromBitmap(uri.toBitmap(binding.root.context))).probabilityAsCategoryList
                    val predictedTitle = probability.maxByOrNull { it.score }?.label ?: "Ko biet"
                    predictedTitle to uri
                })
            }
        }
    }
}