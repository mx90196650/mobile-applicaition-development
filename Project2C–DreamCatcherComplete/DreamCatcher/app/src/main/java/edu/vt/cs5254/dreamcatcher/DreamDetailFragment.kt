package edu.vt.cs5254.dreamcatcher

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.MenuProvider
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.vt.cs5254.dreamcatcher.databinding.FragmentDreamDetailBinding
import kotlinx.coroutines.launch
import java.io.File

class DreamDetailFragment : Fragment() {

    private val args: DreamDetailFragmentArgs by navArgs()

    private val vm: DreamDetailViewModel by viewModels {
        DreamDetailViewModelFactory(args.dreamId)
    }

    private var _binding: FragmentDreamDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "FragmentDreamDetailBinding is null"
        }

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        binding.dreamPhoto.tag = null
        vm.dream.value?.let {
            updatePhoto(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDreamDetailBinding.inflate(inflater, container, false)

        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.fragment_dream_detail, menu)

                    val captureImageIntent = takePhoto.contract.createIntent(
                        requireContext(),
                        Uri.EMPTY // NOTE: The "null" used in BNRG is obsolete now
                    )
                    menu.findItem(R.id.take_photo_menu).isVisible = canResolveIntent(captureImageIntent)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                    return when (menuItem.itemId) {
                        R.id.share_dream_menu -> {
                            vm.dream.value?.let {
                                val shareDreamIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, createDreamShare(it))
//                                    putExtra(
//                                        Intent.EXTRA_SUBJECT,
//                                        getString(R.string.crime_report_subject)
//                                    )
                                }
//
                                val chooserIntent = Intent.createChooser(
                                    shareDreamIntent,
                                    getString(R.string.share_dream_via)
                                )
                                startActivity(chooserIntent)
                            }
                            true
                        }

                        R.id.take_photo_menu -> {
                            vm.dream.value?.let {
                                val photoFile = File(
                                    requireContext().applicationContext.filesDir,
                                    it.photoFileName
                                )
                                val photoUri = FileProvider.getUriForFile(
                                    requireContext(),
                                    "edu.vt.cs5254.dreamcatcher.fileprovider",
                                    photoFile
                                )
                                takePhoto.launch(photoUri)
                            }
                            true
                        }
                        else -> false
                    }
                }
            },
            viewLifecycleOwner
        )

        getItemTouchHelper().attachToRecyclerView(binding.dreamEntryRecycler)

        binding.dreamEntryRecycler.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.dream.collect { dream ->
                    dream?.let { updateView(dream) }
                }
            }
        }

        binding.fulfilledCheckbox.setOnClickListener {
            toggleFulfilledCheckedBox()
        }

        binding.deferredCheckbox.setOnClickListener {
            toggleDeferredCheckedBox()
        }

        binding.titleText.doOnTextChanged { text, _, _, _ ->
            vm.updateDream { oldDream ->
                oldDream.copy(title = text.toString()).apply { entries = oldDream.entries }
            }
        }

        binding.addReflectionButton.setOnClickListener {
            findNavController().navigate(
                DreamDetailFragmentDirections.addReflection()
            )
        }

        binding.dreamPhoto.setOnClickListener {
            vm.dream.value?.let {
                findNavController().navigate(
                    DreamDetailFragmentDirections.showPhotoDetail(it.photoFileName)
                )
            }
        }

        setFragmentResultListener(
            ReflectionDialogFragment.REQUEST_KEY
        ) { _, bundle ->
            val entryText = bundle.getString(ReflectionDialogFragment.BUNDLE_KEY) ?: ""
            addReflection(entryText)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateView(dream: Dream) {

        binding.dreamEntryRecycler.adapter = DreamEntryAdapter(dream.entries)

        val formattedDate = DateFormat.format("yyyy-MM-dd 'at' hh:mm:ss a", dream.lastUpdated)
        binding.lastUpdatedText.text =
            getString(R.string.last_updated, formattedDate)

        if (binding.titleText.text.toString() != dream.title) {
            binding.titleText.setText(dream.title)
        }

        binding.deferredCheckbox.isChecked = dream.isDeferred
        binding.fulfilledCheckbox.isChecked = dream.isFulfilled
        binding.deferredCheckbox.isEnabled = !dream.isFulfilled
        binding.fulfilledCheckbox.isEnabled = !dream.isDeferred

        with (binding.addReflectionButton) {
            if (dream.isFulfilled) {
                hide()
            } else {
                show()
            }
        }

        updatePhoto(dream)
    }

    private fun updatePhoto(dream: Dream) {
        with(binding.dreamPhoto) {
            if (tag != dream.photoFileName) {
                val photoFile =
                    File(requireContext().applicationContext.filesDir, dream.photoFileName)
                if (photoFile.exists()) {
                    doOnLayout { measuredView ->
                        val scaledBM = getScaledBitmap(
                            photoFile.path,
                            measuredView.width,
                            measuredView.height
                        )
                        setImageBitmap(scaledBM)
                        tag = dream.photoFileName
                        isEnabled = true
                    }
                } else {
                    setImageBitmap(null)
                    tag = null
                    isEnabled = false
                }
            }
        }
    }

    private fun toggleFulfilledCheckedBox() {
        vm.updateDream { oldDream ->
            if (oldDream.isFulfilled) {
                oldDream.copy().apply { entries = oldDream.entries.filterNot { it.kind == DreamEntryKind.FULFILLED } }
            } else {
                oldDream.copy().apply {
                    entries = oldDream.entries + DreamEntry(
                        kind = DreamEntryKind.FULFILLED,
                        dreamId = oldDream.id
                    )
                }
            }
        }
    }

    private fun toggleDeferredCheckedBox() {
        vm.updateDream { oldDream ->
            if (oldDream.isDeferred) {
                oldDream.copy().apply { entries = oldDream.entries.filterNot { it.kind == DreamEntryKind.DEFERRED } }
            } else {
                oldDream.copy().apply {
                    entries = oldDream.entries + DreamEntry(
                        kind = DreamEntryKind.DEFERRED,
                        text = "Deferred",
                        dreamId = oldDream.id
                    )
                }
            }
        }
    }

    private fun addReflection(entryText: String) {
        vm.updateDream { oldDream ->
            oldDream.copy().apply {
                entries = oldDream.entries + DreamEntry(
                    kind = DreamEntryKind.REFLECTION,
                    text = entryText,
                    dreamId = oldDream.id
                )
            }
        }
    }

    private fun canResolveIntent(intent: Intent): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        val resolvedActivity: ResolveInfo? =
            packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        return resolvedActivity != null
    }

    private fun createDreamShare(dream: Dream): String {
        val dateString = DateFormat.format("yyyy-MM-dd 'at' hh:mm:ss a", dream.lastUpdated)
        val lastUpdated = getString(R.string.last_updated, dateString)
        var reflections = ""
        if (dream.entries.any { it.kind == DreamEntryKind.REFLECTION }) {
            reflections += "Reflections:"
        dream.entries.filter { it.kind == DreamEntryKind.REFLECTION }
            .forEach { reflections += "\n * " + it.text }
        }

        var status: String = ""
        if (dream.isDeferred) {
            status = getString(R.string.dream_share_status, "Deferred")
        } else if (dream.isFulfilled) {
            status = getString(R.string.dream_share_status, "Fulfilled")
        }
        return getString(R.string.dream_share, dream.title, lastUpdated, reflections, status)
    }

    private fun getItemTouchHelper(): ItemTouchHelper {
        return ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean  = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val dreamEntryHolder = viewHolder as DreamEntryHolder
                    val dreamEntry = dreamEntryHolder.boundEntry
                    vm.updateDream { oldDream ->
                        oldDream.copy().apply { entries = oldDream.entries.filterNot { it == dreamEntry } }
                    }
                }
            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dreamEntryHolder = viewHolder as DreamEntryHolder
                val dreamEntry = dreamEntryHolder.boundEntry
                return if (dreamEntry.kind == DreamEntryKind.REFLECTION) {
                    ItemTouchHelper.LEFT
                } else {
                    0
                }
            }
        })
    }
}