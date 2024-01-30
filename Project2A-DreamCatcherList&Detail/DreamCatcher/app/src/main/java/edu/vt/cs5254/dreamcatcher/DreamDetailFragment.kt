package edu.vt.cs5254.dreamcatcher

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import edu.vt.cs5254.dreamcatcher.databinding.FragmentDreamDetailBinding

class DreamDetailFragment : Fragment() {

    private val vm: DreamDetailViewModel by viewModels()

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

        binding.fulfilledCheckbox.setOnClickListener {
            vm.toggleFulfilledCheckedBox()
            updateView()
        }

        binding.deferredCheckbox.setOnClickListener {
            vm.toggleDeferredCheckedBox()
            updateView()
        }

        binding.titleText.doOnTextChanged { text, _, _, _ ->
            vm.dream = vm.dream.copy(title = text.toString()).apply { entries = vm.dream.entries }
        }

        updateView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateView() {

        val formattedDate = DateFormat.format("yyyy-MM-dd 'at' hh:mm:ss a", vm.dream.lastUpdated)
        binding.lastUpdatedText.text =
            getString(R.string.last_updated, formattedDate)

        val buttons = listOf(
            binding.entry0Button,
            binding.entry1Button,
            binding.entry2Button,
            binding.entry3Button,
            binding.entry4Button
        )
        buttons.forEach { it!!.visibility = View.GONE }
        buttons.zip(vm.dream.entries) { button, entry ->
            button!!.displayEntry(entry)
        }

        if (vm.dream.title != binding.titleText.toString()) {
            binding.titleText.setText(vm.dream.title)
        }

        binding.deferredCheckbox.isChecked = vm.dream.isDeferred
        binding.fulfilledCheckbox.isChecked = vm.dream.isFulfilled
        binding.deferredCheckbox.isEnabled = !vm.dream.isFulfilled
        binding.fulfilledCheckbox.isEnabled = !vm.dream.isDeferred
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
}