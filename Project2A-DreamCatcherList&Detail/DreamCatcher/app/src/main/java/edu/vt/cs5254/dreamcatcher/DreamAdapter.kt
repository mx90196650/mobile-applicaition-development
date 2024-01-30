package edu.vt.cs5254.dreamcatcher

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.vt.cs5254.dreamcatcher.databinding.ListItemDreamBinding

class DreamHolder(private val binding: ListItemDreamBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(dream: Dream) {
        binding.listItemTitle.text = dream.title
        val rCount = dream.entries.count { it.kind == DreamEntryKind.REFLECTION }
        binding.listItemReflectionCount.text =
            binding.root.context.getString(R.string.reflection_count, rCount)
        if(dream.isFulfilled) {
            binding.listItemImage.visibility = View.VISIBLE
            binding.listItemImage.setImageResource(R.drawable.dream_fulfilled_icon)
        } else if (dream.isDeferred) {
            binding.listItemImage.visibility = View.VISIBLE
            binding.listItemImage.setImageResource(R.drawable.dream_deferred_icon)
        } else {
            binding.listItemImage.visibility = View.GONE
        }
    }
}

class DreamAdapter(private val dreams: List<Dream>) : RecyclerView.Adapter<DreamHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DreamHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemDreamBinding.inflate(inflater, parent, false)
        return DreamHolder(binding)
    }

    override fun onBindViewHolder(holder: DreamHolder, position: Int) {
        holder.bind(dreams[position])
    }

    override fun getItemCount(): Int {
        return dreams.size
    }

}
