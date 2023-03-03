package com.krystianrymonlipinski.testexercise

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krystianrymonlipinski.testexercise.databinding.AdapterNumberInfoBinding
import com.krystianrymonlipinski.testexercise.retrofit.model.NumberData

class NumberInfoAdapter : RecyclerView.Adapter<NumberInfoAdapter.NumberInfoViewHolder>() {

    private val numbersInfo = listOf<NumberData>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberInfoViewHolder {
        val _binding = AdapterNumberInfoBinding.inflate(LayoutInflater.from(parent.context))
        return NumberInfoViewHolder(_binding)
    }

    override fun onBindViewHolder(holder: NumberInfoViewHolder, position: Int) {
        val info = numbersInfo[position]
        holder.bind(info)
    }

    override fun getItemCount() = numbersInfo.size


    inner class NumberInfoViewHolder(_binding: AdapterNumberInfoBinding) : RecyclerView.ViewHolder(_binding.root) {

        fun bind(info: NumberData) {

        }
    }


}