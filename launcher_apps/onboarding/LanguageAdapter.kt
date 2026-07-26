package com.teamos.launcher.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teamos.launcher.R

class LanguageAdapter(
    private val items: List<LanguageItem>,
    private val onSelect: (LanguageItem) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.VH>() {

    private var selectedPosition = -1

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val nativeName: TextView = view.findViewById(R.id.lang_native)
        val englishName: TextView = view.findViewById(R.id.lang_english)
        val check: ImageView = view.findViewById(R.id.lang_check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_language, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.nativeName.text = item.nativeName
        holder.englishName.text = item.englishName
        holder.check.visibility = if (position == selectedPosition) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            val old = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(old)
            notifyItemChanged(selectedPosition)
            onSelect(item)
        }
    }

    override fun getItemCount() = items.size
}
