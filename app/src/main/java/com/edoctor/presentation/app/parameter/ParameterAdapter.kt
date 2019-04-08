package com.edoctor.presentation.app.parameter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.edoctor.R
import com.edoctor.data.entity.remote.model.record.*
import com.edoctor.presentation.app.parameter.ParameterAdapter.ViewHolder
import com.edoctor.utils.dispatchUpdatesTo
import com.edoctor.utils.lazyFind
import com.edoctor.utils.unixTimeToJavaTime
import java.text.SimpleDateFormat

class ParameterAdapter : RecyclerView.Adapter<ViewHolder>() {

    var onParameterClickListener: ((BodyParameterModel) -> Unit)? = null

    var parameters: List<BodyParameterModel> = emptyList()
        set(value) {
            val oldItems = field

            field = value

            DiffUtil
                .calculateDiff(
                    object : DiffUtil.Callback() {
                        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                            oldItems[oldItemPosition] == value[newItemPosition]

                        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = true
                        override fun getOldListSize(): Int = oldItems.size
                        override fun getNewListSize(): Int = value.size
                    },
                    true
                )
                .dispatchUpdatesTo(this, "ParameterAdapter")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_parameter_without_name, parent, false)

        return ViewHolder(view) {
            onParameterClickListener?.invoke(it)
        }
    }

    override fun getItemCount(): Int = parameters.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(parameters[position])
    }

    class ViewHolder(
        private val rootView: View,
        private val onParameterClickListener: (BodyParameterModel) -> Unit
    ) : RecyclerView.ViewHolder(rootView) {

        private val date by rootView.lazyFind<TextView>(R.id.date)
        private val value by rootView.lazyFind<TextView>(R.id.value)

        @SuppressLint("SimpleDateFormat")
        fun bind(bodyParameterModel: BodyParameterModel) = bodyParameterModel.let {
            val valueText = rootView.context.run {
                when (it) {
                    is HeightModel -> {
                        getString(R.string.cm_param, it.centimeters)
                    }
                    is WeightModel -> {
                        getString(R.string.kg_param, it.kilograms)
                    }
                    is BloodPressureModel -> {
                        getString(
                            R.string.mmHg_params,
                            it.systolicMmHg,
                            it.diastolicMmHg
                        )
                    }
                    is BloodSugarModel -> {
                        getString(R.string.mmol_per_liter_param, it.mmolPerLiter)
                    }
                    is TemperatureModel -> {
                        getString(R.string.celcius_param, it.celsiusDegrees)
                    }
                    is BloodOxygenModel -> {
                        getString(R.string.percent_param, it.percents)
                    }
                    is CustomBodyParameterModel -> {
                        "${it.value} ${it.unit}"
                    }
                }
            }

            value.text = valueText
            date.text = SimpleDateFormat("dd MMM, HH:mm").format(it.timestamp.unixTimeToJavaTime())

            rootView.setOnClickListener {
                onParameterClickListener(bodyParameterModel)
            }
        }

    }

}