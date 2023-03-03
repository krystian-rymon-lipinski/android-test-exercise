package com.krystianrymonlipinski.testexercise

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krystianrymonlipinski.testexercise.databinding.AdapterNumberInfoBinding
import com.krystianrymonlipinski.testexercise.retrofit.model.NumberData

class NumberInfoAdapter : RecyclerView.Adapter<NumberInfoAdapter.NumberInfoViewHolder>() {

    private var numbersInfo = listOf<NumberData>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberInfoViewHolder {
        val _binding = AdapterNumberInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NumberInfoViewHolder(_binding)
    }

    override fun onBindViewHolder(holder: NumberInfoViewHolder, position: Int) {
        val info = numbersInfo[position]
        holder.bind(info)
    }

    override fun getItemCount() = numbersInfo.size

    fun updateNumbersInfo(newList: List<NumberData>) {
        numbersInfo = newList
        notifyDataSetChanged()
    }


    inner class NumberInfoViewHolder(
        private val _binding: AdapterNumberInfoBinding
    ) : RecyclerView.ViewHolder(_binding.root) {

        fun bind(info: NumberData) {
            _binding.apply {
                tvNumberName.text = info.name
            }
        }

    }


}