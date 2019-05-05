package com.edoctor.presentation.app.parameter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edoctor.R
import com.edoctor.data.entity.presentation.BodyParameterType
import com.edoctor.data.entity.remote.model.record.*
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.app.addParameter.AddOrEditParameterActivity
import com.edoctor.presentation.app.parameter.ParameterPresenter.Event
import com.edoctor.presentation.app.parameter.ParameterPresenter.ViewState
import com.edoctor.presentation.architecture.activity.BaseActivity
import com.edoctor.utils.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import javax.inject.Inject

class ParameterActivity : BaseActivity<ParameterPresenter, ViewState, Event>("ParameterFragment") {

    companion object {
        const val PARAMETER_TYPE_PARAM = "parameter_type"
        const val PATIENT_PARAM = "patient"
        const val CURRENT_USER_IS_PATIENT_PARAM = "current_user_is_patient"

        const val PARAMETER_PARAM = "parameter"
        const val IS_REMOVED_PARAM = "is_removed"
        const val REQUEST_ADD_OR_EDIT_PARAMETER = 12300
    }

    @Inject
    override lateinit var presenter: ParameterPresenter

    override val layoutRes: Int = R.layout.activity_parameter

    private var isLineChart by survivalProperty(false)

    private val toolbar by lazyFind<Toolbar>(R.id.toolbar)
    private val toolbarPrimaryText by lazyFind<TextView>(R.id.toolbar_primary_text)
    private val iconChart by lazyFind<ImageView>(R.id.icon_chart)
    private val recyclerView by lazyFind<RecyclerView>(R.id.recycler_view)
    private val lineChart by lazyFind<LineChart>(R.id.line_chart)
    private val fab by lazyFind<FloatingActionButton>(R.id.fab)

    private lateinit var adapter: ParameterAdapter

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.medicalRecordsComponent.inject(this)
        val parameterType = intent.getSerializableExtra(PARAMETER_TYPE_PARAM) as BodyParameterType
        val patient = intent.getSerializableExtra(PATIENT_PARAM) as PatientModel
        val currentUserIsPatient = intent.getBooleanExtra(CURRENT_USER_IS_PATIENT_PARAM, false)
        presenter.init(parameterType, patient, currentUserIsPatient)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setShowingMode(isLineChart)

        iconChart.setOnClickListener {
            isLineChart = !isLineChart
            setShowingMode(isLineChart)
        }

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
        }

        val parameterType = presenter.parameterType
        toolbarPrimaryText.text = when (parameterType) {
            is BodyParameterType.Height -> getString(R.string.height)
            is BodyParameterType.Weight -> getString(R.string.weight)
            is BodyParameterType.BloodPressure -> getString(R.string.blood_pressure)
            is BodyParameterType.BloodSugar -> getString(R.string.blood_sugar)
            is BodyParameterType.Temperature -> getString(R.string.temperature)
            is BodyParameterType.BloodOxygen -> getString(R.string.blood_oxygen)
            is BodyParameterType.Custom -> {
                if (parameterType == BodyParameterType.Custom.NEW) {
                    getString(R.string.new_parameter)
                } else {
                    "${parameterType.name} (${parameterType.unit})"
                }
            }
        }

        if (presenter.currentUserIsPatient) {
            fab.setOnClickListener {
                AddOrEditParameterActivity.IntentBuilder(this)
                    .parameterType(presenter.parameterType)
                    .readOnly(false)
                    .startForResult(REQUEST_ADD_OR_EDIT_PARAMETER)
            }
        } else {
            fab.hide()
        }

        adapter = ParameterAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        recyclerView.addItemDecoration(SimpleDividerItemDecoration(this))
    }

    private fun setShowingMode(isLineChart: Boolean) {
        if (isLineChart) {
            iconChart.setBackgroundResource(R.drawable.ic_list_black_24dp)
            lineChart.show()
            recyclerView.hide()
        } else {
            iconChart.setBackgroundResource(R.drawable.ic_multiline_chart_black_24dp)
            recyclerView.show()
            lineChart.hide()
        }
    }

    override fun render(viewState: ViewState) {
        val parameters = viewState.parameters

        adapter.parameters = parameters
        adapter.onParameterClickListener = { parameter ->
            AddOrEditParameterActivity.IntentBuilder(this)
                .parameter(parameter)
                .readOnly(!presenter.currentUserIsPatient)
                .startForResult(REQUEST_ADD_OR_EDIT_PARAMETER)
        }

        val unit = when (val firstParameter = parameters.getOrNull(0)) {
            is HeightModel -> getString(R.string.cm)
            is WeightModel -> getString(R.string.kg)
            is BloodPressureModel -> getString(R.string.mmHg)
            is BloodSugarModel -> getString(R.string.mmol_per_liter)
            is TemperatureModel -> getString(R.string.celcius)
            is BloodOxygenModel -> getString(R.string.percent_param)
            is CustomBodyParameterModel -> firstParameter.unit
            else -> null
        }

        lineChart.run {
            val entries = parameters.map { Entry(it.timestamp.toFloat(), it.value.toFloat()) }
            val entriesToParameters = parameters
                .mapIndexed { index, parameter -> entries[index] to parameter }
                .toMap()

            data = unit?.let {
                LineData(
                    listOf(
                        LineDataSet(entries, null).apply {
                            lineWidth = 2.5f
                            circleRadius = 4.5f
                            circleHoleRadius = 2.5f
                            color = ContextCompat.getColor(this@ParameterActivity, R.color.colorAccent)
                            setCircleColor(ContextCompat.getColor(this@ParameterActivity, R.color.colorAccent))
                            setDrawCircles(true)
                            setDrawCircleHole(true)
                        }
                    )
                ).apply {
                    setValueTextSize(0.0f)
                }
            }

            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(entry: Entry?, h: Highlight?) {
                    entry
                        ?.let { entriesToParameters[it] }
                        ?.let {
                            AddOrEditParameterActivity.IntentBuilder(this@ParameterActivity)
                                .parameter(it)
                                .readOnly(!presenter.currentUserIsPatient)
                                .startForResult(REQUEST_ADD_OR_EDIT_PARAMETER)
                        }
                }

                override fun onNothingSelected() = nothing()
            })

            isAutoScaleMinMaxEnabled = true
            description = null
            extraBottomOffset = 10f
            extraTopOffset = 0f
            legend.isEnabled = false

            setDrawGridBackground(false)
            setDrawBorders(false)

            axisLeft.setDrawAxisLine(false)
            axisLeft.textSize = 14f
            axisLeft.yOffset = 0f
            axisLeft.xOffset = 15f

            axisRight.setDrawGridLines(false)
            axisRight.isEnabled = false

            xAxis.setDrawAxisLine(false)
            xAxis.setDrawGridLines(false)
            xAxis.valueFormatter = object : ValueFormatter() {
                @SuppressLint("SimpleDateFormat")
                override fun getFormattedValue(value: Float): String {
                    return SimpleDateFormat("dd MMM, HH:mm").format(value.toLong().unixTimeToJavaTime())
                }
            }

            xAxis.setAvoidFirstLastClipping(true)
            xAxis.setLabelCount(3, true)
            xAxis.textSize = 14f
            xAxis.yOffset = 10f
            xAxis.xOffset = 0f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            notifyDataSetChanged()
            invalidate()
        }
    }

    override fun showEvent(event: Event) {
        when (event) {
            Event.ShowUnhandledErrorEvent -> toast(getString(R.string.unhandled_error_message))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_ADD_OR_EDIT_PARAMETER) {
                val parameter = data?.getSerializableExtra(PARAMETER_PARAM) as? BodyParameterModel
                val isRemoved = data?.getBooleanExtra(IS_REMOVED_PARAM, false) ?: false
                if (parameter != null) {
                    if (isRemoved) {
                        presenter.deleteParameter(parameter)
                    } else {
                        presenter.addOrEditParameter(parameter)
                    }
                }
            }
        }
    }

    class IntentBuilder(fragment: Fragment) : CheckedIntentBuilder(fragment) {

        private var parameterType: BodyParameterType? = null
        private var patient: PatientModel? = null
        private var currentUserIsPatient: Boolean? = null

        fun parameterType(parameterType: BodyParameterType) = apply { this.parameterType = parameterType }
        fun patient(patient: PatientModel) = apply { this.patient = patient }
        fun currentUserIsPatient(currentUserIsPatient: Boolean) = apply { this.currentUserIsPatient = currentUserIsPatient }

        override fun areParamsValid() = parameterType != null && patient != null && currentUserIsPatient != null

        override fun get(): Intent = Intent(context, ParameterActivity::class.java)
            .putExtra(PARAMETER_TYPE_PARAM, parameterType)
            .putExtra(PATIENT_PARAM, patient)
            .putExtra(CURRENT_USER_IS_PATIENT_PARAM, currentUserIsPatient)

    }

}