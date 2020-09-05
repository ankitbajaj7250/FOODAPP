package com.example.foodapp.util

import com.example.foodapp.model.Restaurants

class Sorter {
    companion object {
        var costComparator = Comparator<Restaurants> { res1, res2 ->
            val costOne = res1.rate2
            val costTwo = res2.rate2
            if (costOne.compareTo(costTwo) == 0) {
                ratingComparator.compare(res1, res2)
            } else {
                costOne.compareTo(costTwo)
            }
        }

        var ratingComparator = Comparator<Restaurants> { res1, res2 ->
            val ratingOne = res1.rating
            val ratingTwo = res2.rating
            if (ratingOne.compareTo(ratingTwo) == 0) {
                val costOne = res1.rate2
                val costTwo = res2.rate2
                costOne.compareTo(costTwo)
            } else {
                ratingOne.compareTo(ratingTwo)
            }
        }
    }

}