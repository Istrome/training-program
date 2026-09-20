package com.egorov.workout

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.egorov.workout.RetrofitClient
import com.egorov.workout.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val email = findViewById<EditText>(R.id.emailEditText)
        val password = findViewById<EditText>(R.id.passwordEditText)
        val registerBtn = findViewById<Button>(R.id.registerButton)
        val goToLogin = findViewById<Button>(R.id.goToLoginButton)

        goToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        registerBtn.setOnClickListener {

            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordText.length < 6) {
                Toast.makeText(this, "Пароль должен быть минимум 6 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(email = emailText, password = passwordText)

            Log.d("API", "Отправка регистрации: $user")

            RetrofitClient.api.register(user)
                .enqueue(object : Callback<Map<String, String>> {

                    override fun onResponse(
                        call: Call<Map<String, String>>,
                        response: Response<Map<String, String>>
                    ) {
                        Log.d("API", "Ответ сервера: ${response.body()}")

                        if (response.isSuccessful) {
                            Toast.makeText(this@RegisterActivity, "Регистрация успешна", Toast.LENGTH_SHORT).show()

                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@RegisterActivity, "Ошибка сервера", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                        Log.e("API", "Ошибка сети: ${t.message}")
                        Toast.makeText(this@RegisterActivity, "Ошибка сети", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}