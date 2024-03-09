package com.snstudio.hyper

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var channel: MethodChannel
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        /*
        val flutterEngine = FlutterEngine(applicationContext)
        flutterEngine.dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())
        channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "native_bridge_channel")

        //channel.invokeMethod("search", "emre")
        channel.invokeMethod("getAudioUrl", "1wv54LPIxZg")

        channel.setMethodCallHandler { call, result ->
            if (call.method == "receiveSearchData") {
                val a = call.argument<List<HashMap<String, String>>>("data")
                println("emre${a?.first()}")
                result.success(null)
            } else if (call.method == "receiveAudioUrl") {
                val a = call.argument<String>("data")
                a?.let { downloadFileWithOkHttp(it) }
                println("emre$a")
            } else {
                println("implemente")
                result.notImplemented()
            }
        }*/

    }

    fun downloadFileWithOkHttp(url: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        val downloadDirectoryPath: String =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("emre$e")
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                println("emre$response")
                response.body?.byteStream()?.use { input ->
                    File("${downloadDirectoryPath}/emre.mp3").outputStream().use { output ->
                        println("emre$output")
                        input.copyTo(output)
                    }
                }
            }
        })
    }


    suspend fun callFlutterMethod(flutterMethod: String, params: String): String? =
        suspendCoroutine { continuation ->
            channel.invokeMethod(flutterMethod, params, object : MethodChannel.Result {
                override fun success(result: Any?) {
                    val resultString = result?.toString()
                    continuation.resume(resultString)
                    println("result$resultString")
                }

                override fun error(
                    errorCode: String,
                    errorMessage: String?,
                    errorDetails: Any?
                ) {
                    // Hata durumunda null döndürün
                    println("result$errorMessage")
                    continuation.resume(null)
                }

                override fun notImplemented() {
                    // Gerçekleştirilmemiş metod hatası durumunda null döndürün
                    continuation.resume(null)
                }
            })
        }

}