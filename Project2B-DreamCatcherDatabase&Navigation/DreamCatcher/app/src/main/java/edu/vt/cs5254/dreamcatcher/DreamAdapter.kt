package edu.vt.cs5254.dreamcatcher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.vt.cs5254.dreamcatcher.databinding.ListItemDreamBinding
import java.util.*

class DreamHolder(private val binding: ListItemDreamBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(dream: Dream, onDreamClicked: (dreamId: UUID) -> Unit) {
        binding.listItemTitle.text = dream.title
        val rCount = dream.entries.count { it.kind == DreamEntryKind.REFLECTION }
        binding.listItemReflectionCount.text =
            binding.root.context.getString(R.string.reflection_count, rCount)

        with (binding.listItemImage) {
            if(dream.isFulfilled) {
                visibility = View.VISIBLE
                setImageResource(R.drawable.dream_fulfilled_icon)
            } else if (dream.isDeferred) {
                visibility = View.VISIBLE
                setImageResource(R.drawable.dream_deferred_icon)
            } else {
                visibility = View.GONE
            }
        }

        binding.root.setOnClickListener {
            onDreamClicked(dream.id)
        }
    }
}

class DreamAdapter(private val dreams: List<Dream>, private val onDreamClicked: (dreamId: UUID) -> Unit) : RecyclerView.Adapter<DreamHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DreamHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemDreamBinding.inflate(inflater, parent, false)
        return DreamHolder(binding)
    }

    override fun onBindViewHolder(holder: DreamHolder, position: Int) {
        holder.bind(dreams[position], onDreamClicked)
    }

    override fun getItemCount(): Int {
        return dreams.size
    }

}
