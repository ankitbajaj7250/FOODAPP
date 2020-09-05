package com.example.foodapp.Activity

import android.R
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.adapter.Cart_adapter
import com.example.foodapp.adapter.Menu_adaper
import com.example.foodapp.database.OrderEntity
import com.example.foodapp.database.ResDatabase
import com.example.foodapp.fragments.Frag_restaurant
import com.example.foodapp.model.itemlist
import com.example.foodapp.util.PLACE_ORDER
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

class Cart:AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var recyclerCart: RecyclerView
    private lateinit var cartIadapter: Cart_adapter
    private var orderList = ArrayList<itemlist>()
    private lateinit var txtResName: TextView
    private lateinit var rlLoading: RelativeLayout
    private lateinit var rlCart: RelativeLayout
    private lateinit var btnPlaceOrder: Button
    private var resId: Int = 0
    private var resName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.foodapp.R.layout.cart_activity)

        init()
        setupToolbar()
        setUpCartList()
        placeOrder()
    }


    private fun init() {
        rlLoading = findViewById(com.example.foodapp.R.id.rlProgressBarcart)
        rlCart = findViewById(com.example.foodapp.R.id.rlCart)
        txtResName = findViewById(com.example.foodapp.R.id.ResNameCart)
        txtResName.text = Frag_restaurant.resName
        val bundle = intent.getBundleExtra("data")
        resId = bundle?.getInt("resId", 0) as Int
        resName = bundle.getString("resName", "") as String
    }

    private fun setupToolbar() {
        toolbar = findViewById(com.example.foodapp.R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpCartList() {
        recyclerCart = findViewById(com.example.foodapp.R.id.recyclerCartItems)
        val dbList = GetItemsFromDBAsync(applicationContext).execute().get()
        for (element in dbList) {
            val addAll = orderList.addAll(
                Gson().fromJson(element.foodItems, Array<itemlist>::class.java).asList()
            )
        }
 if (orderList.isEmpty()) {
            rlCart.visibility = View.GONE
            rlLoading.visibility = View.VISIBLE
        } else {
            rlCart.visibility = View.VISIBLE
            rlLoading.visibility = View.GONE
        }
  cartIadapter = Cart_adapter(orderList, this@Cart)
        val mLayoutManager = LinearLayoutManager(this@Cart)
        recyclerCart.layoutManager = mLayoutManager
        recyclerCart.itemAnimator = DefaultItemAnimator()
        recyclerCart.adapter = cartIadapter
    }


    private fun placeOrder() {
        btnPlaceOrder = findViewById(com.example.foodapp.R.id.btnConfirmOrder)
  var sum = 0
        for (i in 0 until orderList.size) {
            sum += orderList[i].cost as Int
        }
        val total = "Place Order(Total: Rs. $sum)"
        btnPlaceOrder.text = total

        btnPlaceOrder.setOnClickListener {
            rlLoading.visibility = View.VISIBLE
            rlCart.visibility = View.INVISIBLE
            sendServerRequest()
        }
    }

    private fun sendServerRequest() {
        val queue = Volley.newRequestQueue(this)
val jsonParams = JSONObject()
        jsonParams.put(
            "user_id",
            this@Cart.getSharedPreferences("FoodApp", Context.MODE_PRIVATE).getString(
                "user_id",
                null
            ) as String
        )
        jsonParams.put("restaurant_id", Frag_restaurant.resId?.toString() as String)
        var sum = 0
        for (i in 0 until orderList.size) {
            sum += orderList[i].cost as Int
        }
        jsonParams.put("total_cost", sum.toString())
        val foodArray = JSONArray()
        for (i in 0 until orderList.size) {
            val foodId = JSONObject()
            foodId.put("food_item_id", orderList[i].id)
            foodArray.put(i, foodId)
        }
        jsonParams.put("food", foodArray)

        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST, PLACE_ORDER, jsonParams, Response.Listener {

                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val clearCart =
                            ClearDBAsync(applicationContext, resId.toString()).execute().get()
                        Menu_adaper.isCartEmpty = true
  val dialog = Dialog(
                            this@Cart,
                            R.style.Theme_Black_NoTitleBar_Fullscreen
                        )
                        dialog.setContentView(com.example.foodapp.R.layout.order_sent)
                        dialog.show()
                        dialog.setCancelable(false)
                        val btnOk = dialog.findViewById<Button>(com.example.foodapp.R.id.btnOk)
                        btnOk.setOnClickListener {
                            dialog.dismiss()
                            startActivity(Intent(this@Cart, Dashboard::class.java))
                            ActivityCompat.finishAffinity(this@Cart)
                        }
                    } else {
                        rlCart.visibility = View.VISIBLE
                        Toast.makeText(this@Cart, "Some Error occurred", Toast.LENGTH_SHORT)
                            .show()
                    }

                } catch (e: Exception) {
                    rlCart.visibility = View.VISIBLE
                    e.printStackTrace()
                }

            }, Response.ErrorListener {
                rlCart.visibility = View.VISIBLE
                Toast.makeText(this@Cart, it.message, Toast.LENGTH_SHORT).show()
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"

                    //The below used token will not work, kindly use the token provided to you in the training
                    headers["token"] = "8f89898d55fa1e"
                    return headers
                }
            }

        queue.add(jsonObjectRequest)

    }
 class GetItemsFromDBAsync(context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
        val db = Room.databaseBuilder(context, ResDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): List<OrderEntity> {
            return db.orderDao().getAllOrders()
        }

    }
 class ClearDBAsync(context: Context, val resId: String) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, ResDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().deleteOrders(resId)
            db.close()
            return true
        }

    }
 override fun onSupportNavigateUp(): Boolean {
        if (ClearDBAsync(applicationContext, resId.toString()).execute().get()) {
            Menu_adaper.isCartEmpty = true
            onBackPressed()
            return true
        }
        return false
    }

    override fun onBackPressed() {
        ClearDBAsync(applicationContext, resId.toString()).execute().get()
        Menu_adaper.isCartEmpty = true
        super.onBackPressed()
    }
}