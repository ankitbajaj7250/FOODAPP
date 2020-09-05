package com.example.foodapp.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import com.example.foodapp.util.ConnectionManager
import com.example.foodapp.util.FORGOT_PASSWORD
import com.example.foodapp.util.varify
import org.json.JSONObject

class ForgetPassword : AppCompatActivity() {
    lateinit var progressBarForget: ProgressBar
    lateinit var rlforget: RelativeLayout
    lateinit var mobileNumberForget: EditText
    lateinit var emailforget: EditText
    lateinit var nextbtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgetpassword)
        mobileNumberForget = findViewById(R.id.etxtMobileForgetPassword)
        emailforget = findViewById(R.id.etxtEmailForgetPassword)
        nextbtn = findViewById(R.id.btnForgetPassword)
        rlforget = findViewById(R.id.rlForgetPassword)
        progressBarForget = findViewById(R.id.progressBar)
        rlforget.visibility = View.VISIBLE
        progressBarForget.visibility = View.GONE


        nextbtn.setOnClickListener {
            val forgotMobileNumber = mobileNumberForget.text.toString()
            if (varify.validateMobile(forgotMobileNumber)) {
                mobileNumberForget.error = null
                if (varify.validateEmail(emailforget.text.toString())) {
                    if (ConnectionManager().isNetworkAvailable(this@ForgetPassword)) {
                        rlforget.visibility = View.GONE
                        progressBarForget.visibility = View.VISIBLE
                        sendOTP(mobileNumberForget.text.toString(), emailforget.text.toString())
                    } else {
                        rlforget.visibility = View.VISIBLE
                        progressBarForget.visibility = View.GONE
                        Toast.makeText(
                            this@ForgetPassword,
                            "Internet is not available!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    rlforget.visibility = View.VISIBLE
                    progressBarForget.visibility = View.GONE
                    emailforget.error = "Invalid Email"
                }
            } else {
                rlforget.visibility = View.VISIBLE
                progressBarForget.visibility = View.GONE
                mobileNumberForget.error = "Invalid phone Number"
            }
        }
    }

    private fun sendOTP(mobileNumber: String, email: String) {
        val queue = Volley.newRequestQueue(this)

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobileNumber)
        jsonParams.put("email", email)

        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST, FORGOT_PASSWORD, jsonParams, Response.Listener {
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val firstTry = data.getBoolean("first_try")
                        if (firstTry) {
                            val builder = AlertDialog.Builder(this@ForgetPassword)
                            builder.setTitle("Inform")
                            builder.setMessage("Please check your Email for the OTP.")
                            builder.setCancelable(false)
                            builder.setPositiveButton("Ok") { _, _ ->
                                val intent = Intent(
                                    this@ForgetPassword,
                                    changePassword::class.java
                                )
                                intent.putExtra("user_mobile", mobileNumber)
                                startActivity(intent)
                            }
                            builder.create().show()
                        } else {
                            val builder = AlertDialog.Builder(this@ForgetPassword)
                            builder.setTitle("Information")
                            builder.setMessage("Please refer to the previous email for the OTP.")
                            builder.setCancelable(false)
                            builder.setPositiveButton("Ok") { _, _ ->
                                val intent = Intent(
                                    this@ForgetPassword,
                                    changePassword::class.java
                                )
                                intent.putExtra("user_mobile", mobileNumber)
                                startActivity(intent)
                            }
                            builder.create().show()
                        }
                    } else {
                        rlforget.visibility = View.VISIBLE
                        progressBarForget.visibility = View.GONE
                        Toast.makeText(
                            this@ForgetPassword,
                            "Mobile number not registered!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    rlforget.visibility = View.VISIBLE
                    progressBarForget.visibility = View.GONE
                    Toast.makeText(
                        this@ForgetPassword,
                        "Incorrect credential !!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, Response.ErrorListener {
                rlforget.visibility = View.VISIBLE
                progressBarForget.visibility = View.GONE
                VolleyLog.e("Error::::", " request Error: ${it.message}")
                Toast.makeText(this@ForgetPassword, it.message, Toast.LENGTH_SHORT).show()
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "9bf534118365f1"
                    return headers
                }
            }
        queue.add(jsonObjectRequest)

    }
}
