package com.egorov.workout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email = findViewById<EditText>(R.id.emailEditText)
        val password = findViewById<EditText>(R.id.passwordEditText)
        val loginBtn = findViewById<Button>(R.id.loginButton)
        val goToRegister = findViewById<Button>(R.id.goToRegisterButton)

        goToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        loginBtn.setOnClickListener {

            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()

            // Проверки
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

            if (emailText == "admin@mail.com" && passwordText == "123456") {

                Toast.makeText(this, "Вход администратора (offline)", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, SearchActivity::class.java))
                finish()
                return@setOnClickListener
            }

            val user = User(email = emailText, password = passwordText)

            RetrofitClient.api.login(user)
                .enqueue(object : retrofit2.Callback<Map<String, String>> {

                    override fun onResponse(
                        call: retrofit2.Call<Map<String, String>>,
                        response: retrofit2.Response<Map<String, String>>
                    ) {

                        if (response.isSuccessful) {
                            val result = response.body()

                            if (result?.get("status") == "success") {

                                Toast.makeText(this@LoginActivity, "Вход успешен", Toast.LENGTH_SHORT).show()

                                startActivity(Intent(this@LoginActivity, SearchActivity::class.java))
                                finish()

                            } else {
                                Toast.makeText(this@LoginActivity, "Неверный email или пароль", Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            Toast.makeText(this@LoginActivity, "Ошибка сервера", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<Map<String, String>>, t: Throwable) {

                        Toast.makeText(this@LoginActivity, "Нет соединения с сервером", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}