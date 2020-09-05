package com.example.foodapp.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import com.example.foodapp.adapter.Menu_adaper
import com.example.foodapp.fragments.*
import com.example.foodapp.fragments.Frag_restaurant.Companion.resId
import com.example.foodapp.util.Drawer
import com.example.foodapp.util.period_manager
import com.google.android.material.navigation.NavigationView

class  Dashboard : AppCompatActivity(), Drawer {

    override fun setDrawerEnabled(enabled: Boolean) {
        val lockMode = if (enabled)
            DrawerLayout.LOCK_MODE_UNLOCKED
        else
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED

        drawerLayout.setDrawerLockMode(lockMode)
        actionBarDrawerToggle.isDrawerIndicatorEnabled = enabled
    }

    private lateinit var toolbar: Toolbar
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var periodmanager: period_manager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigation: NavigationView
    private var menuItem: MenuItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        periodmanager = period_manager(this@Dashboard)
        sharedPreferences = this@Dashboard.getSharedPreferences(
            periodmanager.PREF_NAME,
            periodmanager.PRIVATE_MODE
        )
        init()
        setupToolbar()
        setupActionBarToggle()
        displayHome()
        navigation.setNavigationItemSelectedListener { item: MenuItem ->
            if (menuItem != null) {
                menuItem?.isChecked = false
            }
            item.isCheckable = true
            item.isChecked = true
            menuItem = item
            val mPendingRunnable = Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
            Handler().postDelayed(mPendingRunnable, 100)
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            when (item.itemId) {
                R.id.home -> {
                    val homeFragment = Frag_home()
                    fragmentTransaction.replace(R.id.frame, homeFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title = "Restaurants"
                }
                R.id.myProfile -> {
                    val profileFragment = Frag_profile()
                    fragmentTransaction.replace(R.id.frame, profileFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title = "profile"
                }
                R.id.order_history -> {
                    val orderHistoryFragment = Frag_history()
                    fragmentTransaction.replace(R.id.frame, orderHistoryFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title = "Previous Orders"
                }
                R.id.favRes -> {
                    val favFragment = Frag_favourite()
                    fragmentTransaction.replace(R.id.frame, favFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title = "Favorite Restaurants"
                }
                R.id.faqs -> {
                    val faqFragment = Frag_faqs()
                    fragmentTransaction.replace(R.id.frame, faqFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title = "Frequently Asked Questions"
                }
                R.id.logout -> {
                    val builder = AlertDialog.Builder(this@Dashboard)
                    builder.setTitle("Confirmation")
                        .setMessage("sure for exit?")
                        .setPositiveButton("Yes") { _, _ ->
                            periodmanager.setLogin(false)
                            sharedPreferences.edit().clear().apply()
                            startActivity(Intent(this@Dashboard, Login::class.java))
                            Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
                            ActivityCompat.finishAffinity(this)
                        }
                        .setNegativeButton("No") { _, _ ->
                            displayHome()
                        }
                        .create()
                        .show()

                }

            }
            return@setNavigationItemSelectedListener true
        }
        val convertView = LayoutInflater.from(this@Dashboard).inflate(R.layout.drawer_header, null)
        val userName: TextView = convertView.findViewById(R.id.txtDrawerText)
        val appIcon: ImageView = convertView.findViewById(R.id.imgDrawerImage)
        userName.text = sharedPreferences.getString("user_name", null)
        val phoneText = "+91-${sharedPreferences.getString("user_mobile_number", null)}"
        navigation.addHeaderView(convertView)
        userName.setOnClickListener {
            val profileFragment = Frag_profile()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, profileFragment)
            transaction.commit()
            supportActionBar?.title = "profile"
            val mPendingRunnable = Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
            Handler().postDelayed(mPendingRunnable, 50)
        }

        appIcon.setOnClickListener {
            val profileFragment = Frag_profile()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, profileFragment)
            transaction.commit()
            supportActionBar?.title = "profile"
            val mPendingRunnable = Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
            Handler().postDelayed(mPendingRunnable, 50)
        }

    }
    private fun displayHome() {
        val fragment = Frag_home()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = "Restaurants"
        navigation.setCheckedItem(R.id.home)
    }

    private fun setupActionBarToggle() {
        actionBarDrawerToggle = object :
            ActionBarDrawerToggle(this, drawerLayout, R.string.openDrawer, R.string.closeDrawer) {
            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                val pendingRunnable = Runnable {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                }
                Handler().postDelayed(pendingRunnable, 50)
            }
        }
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
    }
    private fun init() {
        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigation = findViewById(R.id.navigation_view)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val f = supportFragmentManager.findFragmentById(R.id.frame)
        when (id) {
            android.R.id.home -> {
                if (f is Frag_restaurant) {
                    onBackPressed()
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val f = supportFragmentManager.findFragmentById(R.id.frame)
        when (f) {
            is Frag_home -> {
                Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
                super.onBackPressed()
            }
            is Frag_restaurant -> {
                if (!Menu_adaper.isCartEmpty) {
                    val builder = AlertDialog.Builder(this@Dashboard)
                    builder.setTitle("Confirmation")
                        .setMessage("Reset cart items.")
                        .setPositiveButton("Yes") { _, _ ->
                            val clearCart =
                                Cart.ClearDBAsync(applicationContext, resId.toString()).execute().get()
                            displayHome()
                            Menu_adaper.isCartEmpty = true
                        }
                        .setNegativeButton("No") { _, _ ->

                        }
                        .create()
                        .show()
                } else {
                    displayHome()
                }
            }
            else -> displayHome()
        }
    }

}
