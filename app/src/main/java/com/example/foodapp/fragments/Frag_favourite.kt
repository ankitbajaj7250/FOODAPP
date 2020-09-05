package com.example.foodapp.fragments

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodapp.adapter.Res_adapter
import com.example.foodapp.database.ResDatabase
import com.example.foodapp.database.ResEntity
import com.example.foodapp.model.Restaurants
import com.example.foodapp.util.Drawer

class Frag_favourite: Fragment() {
    private lateinit var recyclerRestaurant: RecyclerView
    private lateinit var resadapter: Res_adapter
    private var restaurantList = arrayListOf<Restaurants>()
    private lateinit var rlLoading: RelativeLayout
    private lateinit var rlFav: RelativeLayout
    private lateinit var rlNoFav: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(com.example.foodapp.R.layout.frag_fav, container, false)
        (activity as Drawer).setDrawerEnabled(true)
        rlFav = view.findViewById(com.example.foodapp.R.id.rlFav)
        rlNoFav = view.findViewById(com.example.foodapp.R.id.rlNoFav)
        rlLoading = view.findViewById(com.example.foodapp.R.id.rlprogressBarFav)
        rlLoading.visibility = View.VISIBLE
        setUpRecycler(view)
        return view
    }

    private fun setUpRecycler(view: View) {
        recyclerRestaurant = view.findViewById(com.example.foodapp.R.id.recyclerRestaurants)


        /*In case of favourites, simply extract all the data from the DB and send to the adapter.
        * Here we can reuse the adapter created for the home fragment. This will save our time and optimize our app as well*/
        val backgroundList = FavouritesAsync(activity as Context).execute().get()
        if (backgroundList.isEmpty()) {
            rlLoading.visibility = View.GONE
            rlFav.visibility = View.GONE
            rlNoFav.visibility = View.VISIBLE
        } else {
            rlFav.visibility = View.VISIBLE
            rlLoading.visibility = View.GONE
            rlNoFav.visibility = View.GONE
            for (i in backgroundList) {
                restaurantList.add(
                    Restaurants(
                        i.id,
                        i.name,
                        i.rating,
                        i.costForTwo.toInt(),
                        i.imageUrl
                    )
                )
            }

            resadapter = Res_adapter(restaurantList, activity as Context)
            val mLayoutManager = LinearLayoutManager(activity)
            recyclerRestaurant.layoutManager = mLayoutManager
            recyclerRestaurant.itemAnimator = DefaultItemAnimator()
            recyclerRestaurant.adapter = resadapter
            recyclerRestaurant.setHasFixedSize(true)
        }

    }


    /*A new async class for fetching the data from the DB*/
    class FavouritesAsync(context: Context) : AsyncTask<Void, Void, List<ResEntity>>() {

        val db = Room.databaseBuilder(context, ResDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg params: Void?): List<ResEntity> {

            return db.restaurantDao().getAllRestaurants()
        }

    }


}