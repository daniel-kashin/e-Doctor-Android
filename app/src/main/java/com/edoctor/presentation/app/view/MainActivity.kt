package com.edoctor.presentation.app.view

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.edoctor.R
import com.edoctor.data.entity.remote.response.PatientResponse
import com.edoctor.data.mapper.UserMapper.unwrapResponse
import com.edoctor.presentation.app.account.AccountFragment
import com.edoctor.presentation.app.conversations.ConversationsFragment
import com.edoctor.presentation.app.findDoctor.FindDoctorFragment
import com.edoctor.presentation.app.medcard.MedcardFragment
import com.edoctor.utils.SessionExceptionHelper.onSessionException
import com.edoctor.utils.disposableDelegate
import com.edoctor.utils.lazyFind
import com.edoctor.utils.session
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val bottomNavigationView by lazyFind<BottomNavigationView>(R.id.bottom_navigation)

    private var showTabDisposable by disposableDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        session.runIfOpened {
            if (unwrapResponse(it.account) !is PatientResponse) {
                bottomNavigationView.menu.removeItem(R.id.action_medcard)
                bottomNavigationView.menu.removeItem(R.id.action_find_doctor)
            }
        } ?: run {
            onSessionException()
            return
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            openFragment(item.itemId)
            true
        }

        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            bottomNavigationView.selectedItemId = R.id.action_account
        }
    }

    override fun onStart() {
        super.onStart()
        if (!session.isOpen) {
            onSessionException()
        }
    }

    override fun onDestroy() {
        showTabDisposable = null
        super.onDestroy()
    }

    private fun openFragment(@IdRes navigationItemId: Int) {
        val topFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        val fragmentToOpen: Fragment = when (navigationItemId) {
            R.id.action_account -> {
                if (topFragment is AccountFragment) return
                AccountFragment()
            }
            R.id.action_medcard -> {
                if (topFragment is MedcardFragment) return
                MedcardFragment()
            }
            R.id.action_find_doctor -> {
                if (topFragment is FindDoctorFragment) return
                FindDoctorFragment()
            }
            R.id.action_chat -> {
                if (topFragment is ConversationsFragment) return
                session.runIfOpened {
                    ConversationsFragment.newInstance(unwrapResponse(it.account).email)
                } ?: run {
                    onSessionException()
                    return
                }
            }
            else -> throw IllegalStateException("Unknown navigation item id")
        }

        showTabDisposable = Completable
            .timer(150, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragmentToOpen)
                    .commit()
            }
    }

}
