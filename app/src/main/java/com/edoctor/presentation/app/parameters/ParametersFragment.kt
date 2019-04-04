package com.edoctor.presentation.app.parameters

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edoctor.R
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.app.parameters.ParametersPresenter.Event
import com.edoctor.presentation.app.parameters.ParametersPresenter.ViewState
import com.edoctor.presentation.architecture.fragment.BaseFragment
import com.edoctor.utils.SimpleDividerItemDecoration
import javax.inject.Inject

class ParametersFragment : BaseFragment<ParametersPresenter, ViewState, Event>("ParametersFragment") {

    @Inject
    override lateinit var presenter: ParametersPresenter

    override val layoutRes: Int = R.layout.fragment_parameters

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ParametersAdapter

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.parametersComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)

        adapter = ParametersAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(SimpleDividerItemDecoration(view.context))
    }

    override fun render(viewState: ViewState) {
        adapter.parameters = viewState.bodyParameters
    }

    override fun showEvent(event: Event) {

    }

}