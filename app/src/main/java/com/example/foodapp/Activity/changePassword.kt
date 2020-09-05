package com.example.foodapp.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import com.example.foodapp.util.ConnectionManager
import com.example.foodapp.util.RESET_PASSWORD
import com.example.foodapp.util.varify
import org.json.JSONObject


class changePassword : AppCompatActivity() {

    private lateinit var otp1: EditText
    private lateinit var rlOTP: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var mobileNumber: String
    private lateinit var NewPass: EditText
    private lateinit var NewcnfPass: EditText
    private lateinit var submitbtnOTP: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reset_password)

        otp1 = findViewById(R.id.OTPpage)
        NewPass = findViewById(R.id.etxtNewPassword)
        progressBar = findViewById(R.id.progressBar)
        NewcnfPass = findViewById(R.id.etxtNewConfirmPassword)
        submitbtnOTP = findViewById(R.id.btnSubmitResetPasword)
        rlOTP = findViewById(R.id.rlOTP)

        rlOTP.visibility = View.VISIBLE
        progressBar.visibility = View.GONE

        if (intent != null) {
            mobileNumber = intent.getStringExtra("user_mobile") as String
        }

        submitbtnOTP.setOnClickListener {
            rlOTP.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            if (ConnectionManager().isNetworkAvailable(this@changePassword)) {
                if (otp1.text.length == 4) {
                    if (varify.validatePasswordLength(NewPass.text.toString())) {
                        if (varify.matchPassword(
                                NewPass.text.toString(),
                                NewcnfPass.text.toString()
                            )
                        ) {
                            resetPassword(
                                mobileNumber,
                                otp1.text.toString(),
                                NewPass.text.toString()
                            )
                        } else {
                            rlOTP.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@changePassword,
                                "Passwords do not match",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else {
                        rlOTP.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@changePassword,
                            "Invalid Password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    rlOTP.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@changePassword, "Incorrect OTP", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                rlOTP.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this@changePassword,
                    "Internet is not available !",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun resetPassword(mobileNumber: String, otp: String, password: String) {
        val queue = Volley.newRequestQueue(this)

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobileNumber)
        jsonParams.put("password", password)
        jsonParams.put("otp", otp)

        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST, RESET_PASSWORD, jsonParams, Response.Listener {
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        progressBar.visibility = View.INVISIBLE
                        val builder = AlertDialog.Builder(this@changePassword)
                        builder.setTitle("Confirmation")
                        builder.setMessage("password changed")
                        builder.setIcon(R.drawable.ic_check)
                        builder.setCancelable(false)
                        builder.setPositiveButton("Ok") { _, _ ->
                            startActivity(
                                Intent(
                                    this@changePassword,
                                    Login::class.java
                                )
                            )
                            ActivityCompat.finishAffinity(this@changePassword)
                        }
                        builder.create().show()
                    } else {
                        rlOTP.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        val error = data.getString("errorMessage")
                        Toast.makeText(
                            this@changePassword,
                            error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    rlOTP.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@changePassword,
                        "Incorrect Response!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, Response.ErrorListener {
                rlOTP.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                VolleyLog.e("Error::::", "request Error: ${it.message}")
                Toast.makeText(this@changePassword, it.message, Toast.LENGTH_SHORT).show()
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"

                    headers["token"] = "8f89898d55fa1e"
                    return headers
                }
            }
        queue.add(jsonObjectRequest)
    }
}
