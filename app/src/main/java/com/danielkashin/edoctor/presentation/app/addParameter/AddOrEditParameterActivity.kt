package com.danielkashin.edoctor.presentation.app.addParameter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import com.danielkashin.edoctor.R
import com.danielkashin.edoctor.data.entity.presentation.BodyParameterType
import com.danielkashin.edoctor.data.entity.remote.model.record.*
import com.danielkashin.edoctor.data.entity.presentation.BodyParameterType.Custom.Companion.NEW
import com.danielkashin.edoctor.data.mapper.BodyParameterMapper.toType
import com.danielkashin.edoctor.utils.*
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*
import java.util.UUID.randomUUID

class AddOrEditParameterActivity : AppCompatActivity() {

    companion object {
        const val PARAMETER_TYPE_PARAM = "parameter_type"
        const val PARAMETER_PARAM = "parameter"
        const val READ_ONLY_PARAM = "read_only"
        const val IS_REMOVED_PARAM = "is_removed"
    }

    private val toolbar by lazyFind<Toolbar>(R.id.toolbar)
    private val toolbarPrimaryText by lazyFind<TextView>(R.id.toolbar_primary_text)
    private val iconShare by lazyFind<ImageView>(R.id.icon_share)

    private val dateEditText by lazyFind<AppCompatEditText>(R.id.date)
    private val timeEditText by lazyFind<AppCompatEditText>(R.id.time)
    private val nameEditText by lazyFind<AppCompatEditText>(R.id.name)
    private val unitEditText by lazyFind<AppCompatEditText>(R.id.unit)
    private val firstValueEditText by lazyFind<AppCompatEditText>(R.id.first_value)
    private val secondValueEditText by lazyFind<AppCompatEditText>(R.id.second_value)
    private val firstValueLayout by lazyFind<TextInputLayout>(R.id.first_value_layout)
    private val secondValueLayout by lazyFind<TextInputLayout>(R.id.second_value_layout)
    private val secondValueDelimiter by lazyFind<View>(R.id.second_value_delimiter)
    private val saveButton by lazyFind<Button>(R.id.save_button)
    private val deleteButton by lazyFind<Button>(R.id.delete_button)

    private var calendar: Calendar = Calendar.getInstance()
    private val timestamp: Long get() = calendar.time.let { calendar.time }.time.javaTimeToUnixTime()
    private val maxTimestamp = calendar.timeInMillis

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_parameter)

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
        }
        toolbarPrimaryText.text = getString(R.string.parameter)

        iconShare.setOnClickListener {
            val valuesText = if (secondValueLayout.isVisible) {
                "${firstValueEditText.hint}: ${firstValueEditText.text} ${unitEditText.text}\n" +
                        "${secondValueEditText.hint}: ${secondValueEditText.text} ${unitEditText.text}"
            } else {
                "${firstValueEditText.text} ${unitEditText.text}"
            }

            val text = "${nameEditText.text}\n" +
                    "${dateEditText.text} ${timeEditText.text}\n" +
                    valuesText

            ShareUtils.shareText(
                text,
                getString(R.string.parameter),
                getString(R.string.share_using),
                this
            )
        }

        dateEditText.isFocusable = false
        dateEditText.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(
                        year,
                        month,
                        dayOfMonth,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        0
                    )
                    dateEditText.setText(SimpleDateFormat("dd.MM.yyyy").format(calendar.time.let { calendar.time }))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = maxTimestamp
                show()
            }
        }

        timeEditText.isFocusable = false
        timeEditText.setOnClickListener {
            TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    calendar.set(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        hourOfDay,
                        minute,
                        0
                    )
                    timeEditText.setText(SimpleDateFormat("HH:mm").format(calendar.time.let { calendar.time }))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        val parameter = intent.getSerializableExtra(PARAMETER_PARAM) as? BodyParameterModel
        val parameterType =
            (intent.getSerializableExtra(PARAMETER_TYPE_PARAM) as? BodyParameterType) ?: toType(parameter!!)
        val readOnly = intent.getBooleanExtra(READ_ONLY_PARAM, true)

        if (readOnly) {
            saveButton.hide()
            deleteButton.hide()
            dateEditText.isEnabled = false
            timeEditText.isEnabled = false
            nameEditText.isEnabled = false
            unitEditText.isEnabled = false
            firstValueEditText.isEnabled = false
            secondValueEditText.isEnabled = false
            firstValueLayout.isEnabled = false
            secondValueLayout.isEnabled = false
            secondValueDelimiter.isEnabled = false
        }

        if (parameter == null) {
            dateEditText.setText(SimpleDateFormat("dd.MM.yyyy").format(calendar.time.let { calendar.time }))
            timeEditText.setText(SimpleDateFormat("HH:mm").format(calendar.time.let { calendar.time }))

            deleteButton.hide()
        } else {
            parameter.timestamp.unixTimeToJavaTime().let {
                dateEditText.setText(SimpleDateFormat("dd.MM.yyyy").format(it))
                timeEditText.setText(SimpleDateFormat("HH:mm").format(it))
                calendar.time = Date(it)
            }

            deleteButton.setOnClickListener {
                finishWithRemoveParameter(parameter)
            }
        }

        secondValueLayout.hide()
        secondValueDelimiter.hide()

        when (parameterType) {
            is BodyParameterType.Height -> {
                nameEditText.setText(getString(R.string.height))
                unitEditText.setText(getText(R.string.cm))
                nameEditText.isFocusable = false
                unitEditText.isFocusable = false
                saveButton.setOnClickListener {
                    val centimeters = firstValueEditText.positiveDoubleOrNull()?.takeIf { it > 0 }
                    if (centimeters == null) {
                        toast(R.string.fields_wrong_content)
                    } else {
                        finishWithBodyParameter(
                            if (parameter == null) {
                                HeightModel(randomUUID().toString(), timestamp, centimeters)
                            } else {
                                (parameter as HeightModel).copy(
                                    timestamp = timestamp,
                                    centimeters = centimeters
                                )
                            }
                        )
                    }
                }
                firstValueEditText.setText((parameter as? HeightModel)?.centimeters?.toString())
            }
            is BodyParameterType.Weight -> {
                nameEditText.setText(getString(R.string.weight))
                unitEditText.setText(getText(R.string.kg))
                nameEditText.isFocusable = false
                unitEditText.isFocusable = false
                saveButton.setOnClickListener {
                    val kilograms = firstValueEditText.positiveDoubleOrNull()?.takeIf { it > 0 }
                    if (kilograms == null) {
                        toast(R.string.fields_wrong_content)
                    } else {
                        finishWithBodyParameter(
                            if (parameter == null) {
                                WeightModel(randomUUID().toString(), timestamp, kilograms)
                            } else {
                                (parameter as WeightModel).copy(timestamp = timestamp, kilograms = kilograms)
                            }
                        )
                    }
                }
                firstValueEditText.setText((parameter as? WeightModel)?.kilograms?.toString())
            }
            is BodyParameterType.BloodOxygen -> {
                nameEditText.setText(getString(R.string.blood_oxygen))
                unitEditText.setText(getText(R.string.percent))
                nameEditText.isFocusable = false
                unitEditText.isFocusable = false
                saveButton.setOnClickListener {
                    val percents = firstValueEditText.positiveIntOrNull()?.takeIf { it <= 100 }
                    if (percents == null) {
                        toast(R.string.fields_wrong_content)
                    } else {
                        finishWithBodyParameter(
                            if (parameter == null) {
                                BloodOxygenModel(randomUUID().toString(), timestamp, percents)
                            } else {
                                (parameter as BloodOxygenModel).copy(
                                    timestamp = timestamp,
                                    percents = percents
                                )
                            }
                        )
                    }
                }
                firstValueEditText.setText((parameter as? BloodOxygenModel)?.percents?.toString())
            }
            is BodyParameterType.BloodPressure -> {
                nameEditText.setText(getString(R.string.blood_pressure))
                unitEditText.setText(getText(R.string.mmHg))
                nameEditText.isFocusable = false
                unitEditText.isFocusable = false
                firstValueLayout.hint = getString(R.string.systolic_value)
                secondValueLayout.hint = getString(R.string.diastolic_value)
                secondValueLayout.show()
                secondValueDelimiter.show()

                firstValueEditText.setText((parameter as? BloodPressureModel)?.systolicMmHg?.toString())
                secondValueEditText.setText((parameter as? BloodPressureModel)?.diastolicMmHg?.toString())

                saveButton.setOnClickListener {
                    val first = firstValueEditText.positiveIntOrNull()
                    val second = secondValueEditText.positiveIntOrNull()
                    if (first == null || second == null) {
                        toast(R.string.fields_wrong_content)
                    } else {
                        finishWithBodyParameter(
                            if (parameter == null) {
                                BloodPressureModel(randomUUID().toString(), timestamp, first, second)
                            } else {
                                (parameter as BloodPressureModel).copy(
                                    timestamp = timestamp,
                                    systolicMmHg = first, diastolicMmHg = second
                                )
                            }
                        )
                    }
                }
            }
            is BodyParameterType.BloodSugar -> {
                nameEditText.setText(getString(R.string.blood_sugar))
                unitEditText.setText(getText(R.string.mmol_per_liter))
                nameEditText.isFocusable = false
                unitEditText.isFocusable = false
                firstValueEditText.setText((parameter as? BloodSugarModel)?.mmolPerLiter?.toString())
                saveButton.setOnClickListener {
                    val mmolPerLiter = firstValueEditText.positiveDoubleOrNull()
                    if (mmolPerLiter == null) {
                        toast(R.string.fields_wrong_content)
                    } else {
                        finishWithBodyParameter(
                            if (parameter == null) {
                                BloodSugarModel(randomUUID().toString(), timestamp, mmolPerLiter)
                            } else {
                                (parameter as BloodSugarModel).copy(timestamp = timestamp, mmolPerLiter = mmolPerLiter)
                            }
                        )
                    }
                }
            }
            is BodyParameterType.Temperature -> {
                nameEditText.setText(getString(R.string.temperature))
                unitEditText.setText(getText(R.string.celcius))
                nameEditText.isFocusable = false
                unitEditText.isFocusable = false
                firstValueEditText.setText((parameter as? TemperatureModel)?.celsiusDegrees?.toString())
                saveButton.setOnClickListener {
                    val celsius = firstValueEditText.positiveDoubleOrNull()
                    if (celsius == null) {
                        toast(R.string.fields_wrong_content)
                    } else {
                        finishWithBodyParameter(
                            if (parameter == null) {
                                TemperatureModel(randomUUID().toString(), timestamp, celsius)
                            } else {
                                (parameter as TemperatureModel).copy(timestamp = timestamp, celsiusDegrees = celsius)
                            }
                        )
                    }
                }
            }
            is BodyParameterType.Custom -> {
                if (parameterType != NEW) {
                    nameEditText.setText(parameterType.name)
                    unitEditText.setText(parameterType.unit)
                    nameEditText.isFocusable = false
                    unitEditText.isFocusable = false
                    firstValueEditText.setText((parameter as? CustomBodyParameterModel)?.value?.toString())
                } else {
                    nameEditText.isFocusable = true
                    unitEditText.isFocusable = true
                }

                secondValueLayout.hide()
                secondValueDelimiter.hide()

                saveButton.setOnClickListener {
                    val value = firstValueEditText?.text?.toString()?.toDoubleOrNull()
                    val name = nameEditText?.text?.toString()?.takeIfNotBlank()
                    val unit = unitEditText?.text?.toString()?.takeIfNotBlank()
                    if (value == null || name == null || unit == null) {
                        toast(R.string.fields_wrong_content)
                    } else {
                        finishWithBodyParameter(
                            if (parameter == null) {
                                CustomBodyParameterModel(
                                    randomUUID().toString(),
                                    timestamp,
                                    name,
                                    unit,
                                    value
                                )
                            } else {
                                (parameter as CustomBodyParameterModel).copy(timestamp = timestamp, value = value)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun EditText.positiveIntOrNull(): Int? {
        return text?.toString()?.toIntOrNull()?.takeIf { it >= 0 }
    }

    private fun EditText.positiveDoubleOrNull(): Double? {
        return text?.toString()?.toDoubleOrNull()?.takeIf { it >= 0 }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun finishWithBodyParameter(bodyParameterModel: BodyParameterModel) {
        setResult(Activity.RESULT_OK, Intent().putExtra(PARAMETER_PARAM, bodyParameterModel))
        finish()
    }

    private fun finishWithRemoveParameter(bodyParameterModel: BodyParameterModel) {
        setResult(
            Activity.RESULT_OK,
            Intent()
                .putExtra(PARAMETER_PARAM, bodyParameterModel)
                .putExtra(IS_REMOVED_PARAM, true)
        )
        finish()
    }

    class IntentBuilder(context: Context) : CheckedIntentBuilder(context) {

        private var parameterType: BodyParameterType? = null
        private var parameter: BodyParameterModel? = null
        private var readOnly: Boolean? = null

        fun parameterType(parameterType: BodyParameterType) = apply { this.parameterType = parameterType }
        fun parameter(parameter: BodyParameterModel) = apply { this.parameter = parameter }
        fun readOnly(readOnly: Boolean) = apply { this.readOnly = readOnly }

        override fun areParamsValid() = (parameterType != null || parameter != null) && readOnly != null

        override fun get(): Intent =
            Intent(context, AddOrEditParameterActivity::class.java)
                .putExtra(PARAMETER_TYPE_PARAM, parameterType)
                .putExtra(PARAMETER_PARAM, parameter)
                .putExtra(READ_ONLY_PARAM, readOnly)

    }

}