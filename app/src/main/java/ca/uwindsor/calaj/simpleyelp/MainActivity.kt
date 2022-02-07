package ca.uwindsor.calaj.simpleyelp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "MainActivity"
private const val BASE_URL = "https://api.yelp.com/v3/"
private const val API_KEY = "kAX5qhl-8RxiLecIu1NuvLabAWEC4iPoJnoFUaEIb_BgGJLFUF0iGSSOGg2Sr7-SgBVHCpub6vJ0gRE23VubOFj7ASQ8Ue9wzag8vwm1_ghqwTYS5PFKZpHJA2MAYnYx"
private const val SEARCH_TERM = "Avocado Toast"
private const val SEARCH_LOCATION = "New York"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val restaurants = mutableListOf<YelpRestaurant>()
        val adapter = RestaurantsAdapter(this, restaurants)
        rvRestaurants.adapter = adapter
        rvRestaurants.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        val yelpService = retrofit.create(YelpService::class.java)
        //searchRestaurants will be a async function so we have to enqueue and use callbacks
        yelpService.searchRestaurants("Bearer $API_KEY", SEARCH_TERM, SEARCH_LOCATION).enqueue(object: Callback<YelpSearchResult> {
            override fun onResponse(call: Call<YelpSearchResult>, response: Response<YelpSearchResult>) {
                Log.i(TAG, "onResponse $response")
                val body = response.body()
                if (body == null){
                    Log.w(TAG, "Did not recieve valid response body from Yelp API ... exiting")
                    return
                }
                restaurants.addAll(body.restaurants)
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                Log.i(TAG, "onFailure $t")
            }
        })
        supportActionBar?.title = "$SEARCH_TERM in $SEARCH_LOCATION"
    }
}