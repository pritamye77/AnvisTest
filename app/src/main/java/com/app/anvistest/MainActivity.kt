package com.app.anvistest

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.app.anvistest.model.Movie
import com.app.anvistest.model.Search
import com.app.anvistest.network.MovieApi
import com.app.anvistest.utils.RecyclerViewMainActivity.GalleryImageClickListener
import com.app.anvistest.utils.RecyclerViewMainActivity.MovieAdapter
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val apiUrl = "https://www.omdbapi.com/"

class MainActivity : AppCompatActivity(), GalleryImageClickListener {
    var listOfMovies: MutableList<Movie> = mutableListOf()
    lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        initRecyclerView()
    }



    private fun initRecyclerView() {
        movieAdapter = MovieAdapter(listOfMovies)
        recyclerView_mainActivity.apply {
            hasFixedSize()
            layoutManager = GridLayoutManager(context, 2)
            adapter = movieAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.queryHint = "Search a movie"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                retrofitSearch(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                retrofitSearch(query)
                return false
            }
        })
        return true
    }

    fun retrofitSearch(searchText: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val resultSearch = retrofit.create(MovieApi::class.java).search(searchText)
        resultSearch.enqueue(object : Callback<Search> {
            override fun onFailure(call: Call<Search>, t: Throwable) {
                Log.e("Search", "Error: ${t}")
            }

            override fun onResponse(call: Call<Search>, response: Response<Search>) {
                resetList()
                val allSearch = response.body()?.resultSearch
                allSearch?.let {
                    for (movie in allSearch) {
                        if (movie.title != null && movie.poster != null) {
                            listOfMovies.add(movie)
                        }
                        Log.i("Response : ", "$movie")
                    }
//                    recyclerView_mainActivity.adapter?.notifyDataSetChanged()
                    refreshRecyclerView()
                }
            }
        })
    }

    fun resetList() {
        listOfMovies.clear()
    }

    fun refreshRecyclerView() {
        movieAdapter.notifyDataSetChanged()
    }

    override fun onClick(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
