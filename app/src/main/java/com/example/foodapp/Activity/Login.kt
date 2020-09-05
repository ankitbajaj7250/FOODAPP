package com.example.foodapp.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import com.example.foodapp.util.ConnectionManager
import com.example.foodapp.util.LOGIN
import com.example.foodapp.util.period_manager
import com.example.foodapp.util.varify
import org.json.JSONException
import org.json.JSONObject

class Login : AppCompatActivity() {
    lateinit var MobileNumber: EditText
    lateinit var signUp: TextView
    lateinit var etPassword: EditText
    lateinit var loginbutton: Button
    lateinit var txtForgetPassword: TextView
    lateinit var periodmanager: period_manager
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in)
        MobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        loginbutton = findViewById(R.id.btnLogin)
        txtForgetPassword = findViewById(R.id.forgetPassword)
        signUp = findViewById(R.id.signUp)
        periodmanager = period_manager(this)
        sharedPreferences =
            this.getSharedPreferences(periodmanager.PREF_NAME, periodmanager.PRIVATE_MODE)
        txtForgetPassword.setOnClickListener {
            startActivity(Intent(this@Login, ForgetPassword::class.java))
        }
        signUp.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }
        loginbutton.setOnClickListener {
            loginbutton.visibility = View.INVISIBLE
            if (varify.validateMobile(MobileNumber.text.toString()) && varify.validatePasswordLength(etPassword.text.toString())) {
                if (ConnectionManager().isNetworkAvailable(this@Login)) {
                    val queue = Volley.newRequestQueue(this@Login)

                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", MobileNumber.text.toString())
                    jsonParams.put("password", etPassword.text.toString())

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Method.POST, LOGIN, jsonParams,
                        Response.Listener {

                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val response = data.getJSONObject("data")
                                    sharedPreferences.edit()
                                        .putString("user_id", response.getString("user_id")).apply()
                                    sharedPreferences.edit()
                                        .putString("user_name", response.getString("name")).apply()
                                    sharedPreferences.edit()
                                        .putString(
                                            "user_mobile_number",
                                            response.getString("mobile_number")
                                        )
                                        .apply()
                                    sharedPreferences.edit()
                                        .putString("user_address", response.getString("address"))
                                        .apply()
                                    sharedPreferences.edit()
                                        .putString("user_email", response.getString("email")).apply()
                                    periodmanager.setLogin(true)
                                    startActivity(
                                        Intent(
                                            this@Login,
                                            Dashboard::class.java
                                        )
                                    )
                                    finish()
                                } else {
                                    loginbutton.visibility = View.VISIBLE
                                    txtForgetPassword.visibility = View.VISIBLE
                                    loginbutton.visibility = View.VISIBLE
                                    val errorMessage = data.getString("errorMessage")
                                    Toast.makeText(
                                        this@Login,
                                        errorMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                loginbutton.visibility = View.VISIBLE
                                txtForgetPassword.visibility = View.VISIBLE
                                signUp.visibility = View.VISIBLE
                                e.printStackTrace()
                            }
                        },
                        Response.ErrorListener {
                            loginbutton.visibility = View.VISIBLE
                            txtForgetPassword.visibility = View.VISIBLE
                            signUp.visibility = View.VISIBLE
                            Log.e("Error::::", "/request Error occurred !: ${it.message}")
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val header = HashMap<String, String>()
                            header["Content-type"] = "application/json"
                            header["token"] = "8f89898d55fa1e"
                            return header
                        }
                    }
                    queue.add(jsonObjectRequest)

                } else {
                    loginbutton.visibility = View.VISIBLE
                    txtForgetPassword.visibility = View.VISIBLE
                    signUp.visibility = View.VISIBLE
                    Toast.makeText(this@Login, "internet is not available", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                loginbutton.visibility = View.VISIBLE
                txtForgetPassword.visibility = View.VISIBLE
                signUp.visibility = View.VISIBLE
                Toast.makeText(this@Login, "Invalid credential", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }
}
