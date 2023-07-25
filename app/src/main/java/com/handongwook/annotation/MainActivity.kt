package com.handongwook.annotation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.Keep
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.handongwook.annotation.ui.theme.AnnotationTheme
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : ComponentActivity() {

    private val api by lazy {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor())
                    .addInterceptor(HttpLoggingInterceptor().setLevel(
                        HttpLoggingInterceptor.Level.BODY)
                    )
                    .build()
            )
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(MyApi::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        checkBirthDateByAnnotation()

        lifecycleScope.launch {
            api.getUser()
            api.getPost()
            /**
             * --> GET https://jsonplaceholder.typicode.com/users/1
             * Authorization: my_token
             * --> END GET
             * <-- 200 https://jsonplaceholder.typicode.com/users/1 (172ms)
             */
        }
    }

    fun checkBirthDateByAnnotation() {
        // RuntimeException Caused by: java.lang.IllegalArgumentException: Birth date is not a valid date: 1993-12-20h
        val user = User(
            name = "DongWook",
            birthDate = "1993-12-20h"
        )
    }
}

data class User(
    val name: String,
    @AllowedRegex("\\d{4}-\\d{2}-\\d{2}") val birthDate: String
) {
    init {
        val fields = this::class.java.declaredFields
        fields.forEach { field ->
            field.annotations.forEach { annotation ->
                if (field.isAnnotationPresent(AllowedRegex::class.java)) {
                    val regex = field.getAnnotation(AllowedRegex::class.java)?.regex
                    if (regex?.toRegex()?.matches(birthDate) == false) {
                        throw IllegalArgumentException("Birth date is not a valid date: $birthDate")
                    }
                }
            }
        }
    }
}

@Target(AnnotationTarget.FIELD)
annotation class AllowedRegex(val regex: String)