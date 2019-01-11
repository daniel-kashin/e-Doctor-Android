package com.edoctor.presentation.architecture.activity

/**
abstract class BaseToolbarActivity<P : Presenter<VS, EV>, VS : Presenter.ViewState, EV : Presenter.Event>(
        TAG: String?,
        saveRenderedViewState: Boolean = false
) : BaseActivity<P, VS, EV>(TAG, saveRenderedViewState) {

    val toolbar by lazyFind<Toolbar>(R.id.toolbar)

    protected var toolbarTitle: CharSequence?
        get() = supportActionBar!!.title
        set(value) {
            supportActionBar!!.title = value
        }

    protected fun setToolbarTitle(@StringRes titleRes: Int) {
        supportActionBar!!.setTitle(titleRes)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        if (isHomeAsUpEnabled()) supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        toolbarMenus.forEach { menuInflater.inflate(it, menu) }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home && isHomeAsUpEnabled()) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    protected open val toolbarMenus = intArrayOf()

    protected open fun isHomeAsUpEnabled(): Boolean = true
}
        */