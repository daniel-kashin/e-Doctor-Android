package com.edoctor.presentation.app.parameter

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edoctor.R
import com.edoctor.data.entity.remote.model.record.BodyParameterModel
import com.edoctor.data.entity.presentation.BodyParameterType
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.app.addParameter.AddOrEditParameterActivity
import com.edoctor.presentation.app.parameter.ParameterPresenter.Event
import com.edoctor.presentation.app.parameter.ParameterPresenter.ViewState
import com.edoctor.presentation.architecture.activity.BaseActivity
import com.edoctor.utils.CheckedIntentBuilder
import com.edoctor.utils.SimpleDividerItemDecoration
import com.edoctor.utils.lazyFind
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject

class ParameterActivity : BaseActivity<ParameterPresenter, ViewState, Event>("ParameterFragment") {

    companion object {
        const val PARAMETER_TYPE_PARAM = "parameter_type"

        const val PARAMETER_PARAM = "parameter"
        const val IS_REMOVED_PARAM = "is_removed"
        const val REQUEST_ADD_OR_EDIT_PARAMETER = 12300
    }

    @Inject
    override lateinit var presenter: ParameterPresenter

    override val layoutRes: Int = R.layout.activity_parameter

    private val toolbar by lazyFind<Toolbar>(R.id.toolbar)
    private val recyclerView by lazyFind<RecyclerView>(R.id.recycler_view)
    private val fab by lazyFind<FloatingActionButton>(R.id.fab)

    private lateinit var adapter: ParameterAdapter

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.medicalRecordsComponent.inject(this)
        val parameterType = intent.getSerializableExtra(PARAMETER_TYPE_PARAM) as BodyParameterType
        presenter.init(parameterType)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setBackgroundDrawable(ColorDrawable(Color.WHITE))

            val parameterType = presenter.parameterType
            title = when (parameterType) {
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
        }

        fab.setOnClickListener {
            AddOrEditParameterActivity.IntentBuilder(this)
                .parameterType(presenter.parameterType)
                .startForResult(REQUEST_ADD_OR_EDIT_PARAMETER)
        }

        adapter = ParameterAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(SimpleDividerItemDecoration(this))
    }

    override fun render(viewState: ViewState) {
        adapter.parameters = viewState.parameters
        adapter.onParameterClickListener = { parameter ->
            AddOrEditParameterActivity.IntentBuilder(this)
                .parameter(parameter)
                .startForResult(REQUEST_ADD_OR_EDIT_PARAMETER)
        }
    }

    override fun showEvent(event: Event) {
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
                        presenter.removeParameter(parameter)
                    } else {
                        presenter.addOrEditParameter(parameter)
                    }
                }
            }
        }
    }

    class IntentBuilder(fragment: Fragment) : CheckedIntentBuilder(fragment) {

        private var parameterType: BodyParameterType? = null

        fun parameterType(parameterType: BodyParameterType) = apply { this.parameterType = parameterType }

        override fun areParamsValid() = parameterType != null

        override fun get(): Intent = Intent(context, ParameterActivity::class.java)
            .putExtra(PARAMETER_TYPE_PARAM, parameterType)

    }

}