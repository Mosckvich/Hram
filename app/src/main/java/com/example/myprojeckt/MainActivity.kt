package com.example.myprojeckt

import android.widget.ArrayAdapter
import android.widget.TextView
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class Joke {
    @SerializedName("type")
    @Expose
    var type:String? = null

    @SerializedName("setup")
    @Expose
    var setup:String? = null

    @SerializedName("punchline")
    @Expose
    var punchline:String? = null

}

interface Api {
    @GET("jokes/{type}/random")
    fun randJoke(@Path("type") type: Callback<List<Joke>>):Call<List<Joke>>
}


class ListAdapter(context: Context, resource:Int, array: List<Joke>):
    ArrayAdapter<Joke>(context,resource,array) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val joke = getItem(position)

        if (joke == null) {
            throw Exception()
        }

        val view = if (convertView!=null) {
            convertView
        } else {
            LayoutInflater.from(context).inflate(R.layout.joke_item,null)
        }

        val nameView = view.findViewById<TextView>(R.id.type)
        val capitalView = view.findViewById<TextView>(R.id.setup)
        val region = view.findViewById<TextView>(R.id.punchline)

        nameView.text = joke.type
        capitalView.text = joke.setup
        region.text = joke.punchline
        return view
    }
}


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val retrofit = Retrofit.Builder().
                baseUrl("https://official-joke-api.appspot.com/").
                addConverterFactory(GsonConverterFactory.create()).
                build()

        val list = jList
        val api = retrofit.create(Api::class.java)

        Thread(Runnable {
            api.randJoke(object : Callback<List<Joke>>{
                override fun onFailure(call: Call<List<Joke>>, t: Throwable) {
                    Log.d("Tag","Error")
                }

                override fun onResponse(call: Call<List<Joke>>, response: Response<List<Joke>>) {
                   list.post{
                       list.adapter = ListAdapter(
                           this@MainActivity,
                           R.layout.joke_item,
                           response.body()!!)

                   }
                }

            })

        })
    }
}
