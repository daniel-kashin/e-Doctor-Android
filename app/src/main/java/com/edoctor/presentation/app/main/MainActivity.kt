package com.edoctor.presentation.app.main

import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.edoctor.R
import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.presentation.app.account.AccountFragment
import com.edoctor.presentation.app.conversations.ConversationsFragment
import com.edoctor.presentation.app.findDoctor.FindDoctorFragment
import com.edoctor.presentation.app.medcard.MedcardFragment
import com.edoctor.presentation.app.medicalAccessesForPatient.MedicalAccessesForPatientFragment
import com.edoctor.presentation.app.medicalAccessesForDoctor.MedicalAccessesForDoctorFragment
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

        session.runIfOpened { userInfo ->
            when (userInfo) {
                is DoctorModel -> {
                    bottomNavigationView.menu.findItem(R.id.action_medcard).title = getString(R.string.btn_medcards)
                    bottomNavigationView.menu.removeItem(R.id.action_find_doctor)
                    bottomNavigationView.menu.removeItem(R.id.action_restrictions)
                }
                is PatientModel -> {
                    bottomNavigationView.menu.findItem(R.id.action_medcard).title = getString(R.string.btn_medcard)
                }
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
                session.runIfOpened { userInfo ->
                    when (userInfo) {
                        is DoctorModel -> {
                            if (topFragment is MedicalAccessesForDoctorFragment) return
                            MedicalAccessesForDoctorFragment()
                        }
                        is PatientModel -> {
                            if (topFragment is MedcardFragment) return
                            MedcardFragment.newInstance(null)
                        }
                    }
                } ?: run {
                    onSessionException()
                    return
                }
            }
            R.id.action_find_doctor -> {
                if (topFragment is FindDoctorFragment) return
                FindDoctorFragment()
            }
            R.id.action_chat -> {
                if (topFragment is ConversationsFragment) return
                session.runIfOpened { userInfo ->
                    ConversationsFragment.newInstance(userInfo)
                } ?: run {
                    onSessionException()
                    return
                }
            }
            R.id.action_restrictions -> {
                if (topFragment is MedicalAccessesForPatientFragment) return
                MedicalAccessesForPatientFragment()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}
