package com.example.projekt_3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_logged_in.*
import kotlin.concurrent.thread

class LoggedInActivity : AppCompatActivity() {

    lateinit var mDatabase : FirebaseFirestore
    private lateinit var mAuth : FirebaseAuth

    private val textLogged by lazy { findViewById<TextView>(R.id.textLogged) }
    private val btnFavourites by lazy { findViewById<TextView>(R.id.btnFavourites) }
    private val btnHome by lazy { findViewById<TextView>(R.id.btnHome) }

    private val rssParser by lazy { RSSParser() }

    private val adapter by lazy { NewsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseFirestore.getInstance()

        loggedRecyclerView.let {
            it.layoutManager = GridLayoutManager(this, 1)
            it.adapter = this.adapter
        }

        btnFavourites.setOnClickListener{
            adapter.setIsHome(false)
            favourites()

        }

        btnHome.setOnClickListener{
            adapter.setIsHome(true)
            parse()
        }

        mAuth.addAuthStateListener {
            if(mAuth.currentUser == null){
                this.finish()
            }
        }

        mDatabase.collection("names").document(mAuth.currentUser!!.uid).get().addOnSuccessListener { document ->
            if (document != null) {
                textLogged.text = "Welcome " + document.data!!["name"].toString()
            }
        }.addOnFailureListener { exception ->
            Log.d("get failed with ", exception.toString())
        }

        parse()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.user_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.itemLogout){
            signOut()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOut(){
        mAuth.signOut()
    }

    private fun showMessage(view: View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).setAction("Action", null).show()
    }

    private fun parse(){
        val thread = thread {
            val news :List<RSSItem?>  = rssParser.parse("https://www.polsatnews.pl/rss/wszystkie.xml")
            showRSS(news.filter{e-> e?.enclosure!!.isNotEmpty()})
        }
    }

    private fun showRSS(news : List<RSSItem?>){
        Log.i("NEWS",news.toString())
        runOnUiThread{
            adapter.setNews(news as List<RSSItem>)
        }
    }

    private fun favourites() {
        var news = mutableListOf<RSSItem>()

        mDatabase.collection("names").document(mAuth.currentUser!!.uid).collection("favourites").get().addOnSuccessListener { documents ->
            for (document in documents){
                val item = document.toObject(RSSItem::class.java)
                news.add(item)
            }
        }.addOnCompleteListener {

            showRSS(news)
        }
    }

}