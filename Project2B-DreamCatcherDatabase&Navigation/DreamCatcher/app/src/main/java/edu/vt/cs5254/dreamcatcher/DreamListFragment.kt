package edu.vt.cs5254.dreamcatcher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import edu.vt.cs5254.dreamcatcher.databinding.FragmentDreamListBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DreamListFragment : Fragment() {

    private val vm: DreamListViewModel by viewModels()

    private var _binding: FragmentDreamListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "FragmentDreamListBinding is null"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDreamListBinding.inflate(inflater, container, false)
        binding.dreamRecyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.dreams.collect { dreams ->
                    binding.dreamRecyclerView.adapter = DreamAdapter(dreams) { dreamId ->
                        findNavController().navigate(
                            DreamListFragmentDirections.showDreamDetail(dreamId)
                        )
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