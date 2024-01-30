package edu.vt.cs5254.dreamcatcher

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import edu.vt.cs5254.dreamcatcher.databinding.ListItemDreamEntryBinding

class DreamEntryHolder(private val binding: ListItemDreamEntryBinding) : RecyclerView.ViewHolder(binding.root) {

    lateinit var boundEntry: DreamEntry
        private set

    fun bind(dreamEntry: DreamEntry) {
        binding.dreamEntryButton.displayEntry(dreamEntry)
        binding.dreamEntryButton.isEnabled = false
    }

    private fun Button.displayEntry(entry: DreamEntry) {
        boundEntry = entry
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



class DreamEntryAdapter(private val entries: List<DreamEntry>) : RecyclerView.Adapter<DreamEntryHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DreamEntryHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemDreamEntryBinding.inflate(inflater, parent, false)
        return DreamEntryHolder(binding)
    }

    override fun onBindViewHolder(holder: DreamEntryHolder, position: Int) {
        holder.bind(entries[position])
    }

    override fun getItemCount(): Int {
        return entries.size
    }

}