package com.edoctor

import android.os.Bundle
import android.util.Log.d
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.edoctor.service.MessageService
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import okhttp3.Authenticator
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class MainActivity : AppCompatActivity() {

    val springAuthenticator = Authenticator { _, response ->
        if (response.request().header("Authorization") != null) {
            null
        } else {
            response.request().newBuilder()
                .header("Authorization", Credentials.basic("danil", "danil"))
                .build()
        }
    }

    val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor {
                HttpLoggingInterceptor { d("Retrofit", it) }
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
                    .intercept(it)
            }
            .authenticator(springAuthenticator)
            .build()
    }

    val moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    val service by lazy {
        Scarlet.Builder()
            .webSocketFactory(okHttpClient.newWebSocketFactory("ws://localhost:9095/chat"))
            .lifecycle(AndroidLifecycle.ofApplicationForeground(application))
            .addMessageAdapterFactory(MoshiMessageAdapter.Factory(moshi))
            .addStreamAdapterFactory(RxJava2StreamAdapterFactory())
            .build()
            .create<MessageService>()
    }

    val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.text_view)
        val editText = findViewById<EditText>(R.id.edit_text)
        val send = findViewById<Button>(R.id.send)
        val clean = findViewById<Button>(R.id.clean)

        disposable += service.observeEvent()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { it ->
                textView.text = textView.text.toString() + "onEvent: $it\n"
            }

        disposable += service.observeMessages()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                textView.text = textView.text.toString() + "onMessage: $it\n"
            }

        send.setOnClickListener {
            textView.text = textView.text.toString() + "onMessageSent: ${editText.text}\n"
            service.sendMessage(editText.text.toString())
        }

        clean.setOnClickListener {
            textView.text = ""
        }
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }
}
