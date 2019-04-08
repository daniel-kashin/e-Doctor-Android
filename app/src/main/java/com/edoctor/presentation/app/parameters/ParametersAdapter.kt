package com.edoctor.presentation.app.parameters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.edoctor.R
import com.edoctor.data.entity.remote.model.record.*
import com.edoctor.presentation.app.parameters.ParametersAdapter.ViewHolder
import com.edoctor.utils.dispatchUpdatesTo
import com.edoctor.utils.lazyFind
import com.edoctor.utils.unixTimeToJavaTime
import java.text.SimpleDateFormat

class ParametersAdapter : RecyclerView.Adapter<ViewHolder>() {

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
                .dispatchUpdatesTo(this, "ParametersAdapter")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_parameter, parent, false)

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

        private val name by rootView.lazyFind<TextView>(R.id.name)
        private val date by rootView.lazyFind<TextView>(R.id.date)
        private val value by rootView.lazyFind<TextView>(R.id.value)

        @SuppressLint("SimpleDateFormat")
        fun bind(bodyParameterModel: BodyParameterModel) = bodyParameterModel.let {
            val (nameText, valueText) = rootView.context.run {
                when (it) {
                    is HeightModel -> {
                        getString(R.string.height) to getString(R.string.cm_param, it.centimeters)
                    }
                    is WeightModel -> {
                        getString(R.string.weight) to getString(R.string.kg_param, it.kilograms)
                    }
                    is BloodPressureModel -> {
                        getString(R.string.blood_pressure) to getString(
                            R.string.mmHg_params,
                            it.systolicMmHg,
                            it.diastolicMmHg
                        )
                    }
                    is BloodSugarModel -> {
                        getString(R.string.blood_sugar) to getString(R.string.mmol_per_liter_param, it.mmolPerLiter)
                    }
                    is TemperatureModel -> {
                        getString(R.string.temperature) to getString(R.string.celcius_param, it.celsiusDegrees)
                    }
                    is BloodOxygenModel -> {
                        getString(R.string.blood_oxygen) to getString(R.string.percent_param, it.percents)
                    }
                    is CustomBodyParameterModel -> {
                        it.name to "${it.value} ${it.unit}"
                    }
                }
            }

            name.text = nameText
            value.text = valueText
            date.text = SimpleDateFormat("dd MMM, HH:mm").format(it.timestamp.unixTimeToJavaTime())

            rootView.setOnClickListener {
                onParameterClickListener(bodyParameterModel)
            }
        }

    }

}