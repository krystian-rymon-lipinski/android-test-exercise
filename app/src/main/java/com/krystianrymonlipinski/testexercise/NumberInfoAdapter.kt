package com.krystianrymonlipinski.testexercise

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krystianrymonlipinski.testexercise.databinding.AdapterNumberInfoBinding

class NumberInfoAdapter(
    private val onNumberClickedListener: OnNumberClickedListener
) : RecyclerView.Adapter<NumberInfoAdapter.NumberInfoViewHolder>() {

    private var numbersData = listOf<NumberData>()

    private var currentlyActiveCard: NumberCardView? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberInfoViewHolder {
        val _binding = AdapterNumberInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NumberInfoViewHolder(_binding).apply {
            _binding.root.setOnClickListener { withProperAdapterIndex(adapterPosition) {
                currentlyActiveCard?.setIsSelected(isSelected = false)
                this.setIsSelected(isSelected = true)
                onNumberClickedListener.onNumberClicked(adapterPosition)
            } }
        }
    }

    override fun onBindViewHolder(holder: NumberInfoViewHolder, position: Int) {
        val info = numbersData[position]
        holder.bind(info)
    }

    override fun getItemCount() = numbersData.size

    fun updateNumbersInfo(newList: List<NumberData>) {
        numbersData = newList
        notifyDataSetChanged()
    }

    private fun withProperAdapterIndex(index: Int, action: () -> Unit) {
        if (index != RecyclerView.NO_POSITION) action.invoke()
    }

    interface OnNumberClickedListener {
        fun onNumberClicked(index: Int)
    }


    inner class NumberInfoViewHolder(
        private val _binding: AdapterNumberInfoBinding
    ) : RecyclerView.ViewHolder(_binding.root) {

        fun bind(info: NumberData) {
            _binding.apply {
                tvNumberName.text = info.name
                ivNumberImage.setImageBitmap(info.image)
            }
        }

        fun setIsSelected(isSelected: Boolean) {
            _binding.root.setIsSelected(isSelected)
            currentlyActiveCard = _binding.root
        }

    }


}