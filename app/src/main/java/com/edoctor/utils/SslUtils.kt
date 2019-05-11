package com.edoctor.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*

object SslUtils {

    fun getSslContextForCertificateFile(context: Context, fileName: String): SSLContext {
        try {
            val keyStore = getKeyStore(context, fileName)
            val sslContext = SSLContext.getInstance("SSL")
            val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)
            sslContext.init(null, trustManagerFactory.trustManagers, SecureRandom())
            return sslContext
        } catch (e: Exception) {
            val msg = "Error during creating SslContext for certificate from assets"
            e.printStackTrace()
            throw RuntimeException(msg)
        }
    }

    private fun getKeyStore(context: Context, fileName: String): KeyStore? {
        var keyStore: KeyStore? = null
        try {
            val assetManager = context.assets
            val cf = CertificateFactory.getInstance("X.509")
            val caInput = assetManager.open(fileName)
            val ca: Certificate
            try {
                ca = cf.generateCertificate(caInput)
                Log.d("SslUtilsAndroid", "ca=" + (ca as X509Certificate).subjectDN)
            } finally {
                caInput.close()
            }

            val keyStoreType = KeyStore.getDefaultType()
            keyStore = KeyStore.getInstance(keyStoreType)
            keyStore!!.load(null, null)
            keyStore.setCertificateEntry("ca", ca)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return keyStore
    }

    @SuppressLint("TrustAllX509TrustManager")
    fun getTrustAllHostsSSLSocketFactory(): Pair<SSLSocketFactory, X509TrustManager>? =
        try {
            val trustManager = object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            }

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf<TrustManager>(trustManager), SecureRandom())
            sslContext.socketFactory to trustManager
        } catch (e: KeyManagementException) {
            null
        } catch (e: NoSuchAlgorithmException) {
            null
        }

}