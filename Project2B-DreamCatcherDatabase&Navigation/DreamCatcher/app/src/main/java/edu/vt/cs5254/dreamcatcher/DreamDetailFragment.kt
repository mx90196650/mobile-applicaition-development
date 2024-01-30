package edu.vt.cs5254.dreamcatcher

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import edu.vt.cs5254.dreamcatcher.databinding.FragmentDreamDetailBinding
import kotlinx.coroutines.launch
import java.util.*

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDreamDetailBinding.inflate(inflater, container, false)
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
        val formattedDate = DateFormat.format("yyyy-MM-dd 'at' hh:mm:ss a", dream.lastUpdated)
        binding.lastUpdatedText.text =
            getString(R.string.last_updated, formattedDate)

        val buttons = listOf(
            binding.entry0Button,
            binding.entry1Button,
            binding.entry2Button,
            binding.entry3Button,
            binding.entry4Button
        )
        buttons.forEach { it.visibility = View.GONE }
        buttons.zip(dream.entries) { button, entry ->
            button.displayEntry(entry)
        }

        if (dream.title != binding.titleText.toString()) {
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
    }

    private fun Button.displayEntry(entry: DreamEntry) {
        visibility = View.VISIBLE
        text = entry.kind.toString()
        when(entry.kind) {
            DreamEntryKind.CONCEIVED -> {
                setBackgroundWithContrastingText("#E3F9A6")
            }
            DreamEntryKind.DEFERRED -> {
                setBackgroundWithContrastingText("#D3D3D3")
            }
            DreamEntryKind.FULFILLED -> {
                setBackgroundWithContrastingText("#FFE87C")
            }
            DreamEntryKind.REFLECTION -> {
                isAllCaps = false
                text = entry.text
                setBackgroundWithContrastingText("#F5DEB3")
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
}