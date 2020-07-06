package com.example.projekt_3

import android.content.Intent
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item.view.*


class NewsViewHolder(view: View): RecyclerView.ViewHolder(view)

class NewsAdapter : RecyclerView.Adapter<NewsViewHolder>() {

    private var news = mutableListOf<RSSItem>()

    lateinit var mDatabase : FirebaseFirestore
    private lateinit var mAuth : FirebaseAuth
    private var isHome = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseFirestore.getInstance()
        return NewsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item, parent, false)
        )
    }

    override fun getItemCount(): Int = news.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        news[position].title.let { holder.itemView.itemTitleText.text = it }
        news[position].description.let { holder.itemView.itemDescriptionText.text = it }
        news[position].enclosure.let {Picasso.get().load(it).into(holder.itemView.itemImage)}
        //news[position].enclosure.let { DownloadImageTask(holder.itemView.itemImage).execute(it) }

        var documentReference = mDatabase.collection("names").document(mAuth.currentUser!!.uid).collection("displayed").document(news[position].guid)
        documentReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    makeGray(holder)
                }
            }
        }

        if (isHome){
            holder.itemView.itemToggleButton.visibility = View.VISIBLE

            documentReference = mDatabase.collection("names").document(mAuth.currentUser!!.uid).collection("favourites").document(news[position].guid)
            documentReference.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document!!.exists()) {
                        holder.itemView.itemToggleButton.isChecked = true
                    }
                }
            }
        }else{
            holder.itemView.itemToggleButton.visibility = View.INVISIBLE
        }

        holder.itemView.setOnClickListener{
            makeGray(holder)
            mDatabase.collection("names").document(mAuth.currentUser!!.uid).collection("displayed").document(news[position].guid).set(news[position])

            val context = holder.itemView.context
            context.startActivity(Intent(context,WebsiteActivity::class.java).putExtra("link",news[position].link))
        }

        holder.itemView.itemToggleButton.setOnClickListener {
            var btn = it as ToggleButton
            if (btn.isChecked){
                mDatabase.collection("names").document(mAuth.currentUser!!.uid).collection("favourites").document(news[position].guid).set(news[position])
            }else{
                mDatabase.collection("names").document(mAuth.currentUser!!.uid).collection("favourites").document(news[position].guid).delete()
            }
        }

    }

    private fun makeGray(holder: NewsViewHolder){
        holder.itemView.setBackgroundColor(Color.LTGRAY)
        holder.itemView.alpha = 0.5f

        val matrix = ColorMatrix()
        matrix.setSaturation(0f) //0 means grayscale
        val cf = ColorMatrixColorFilter(matrix)
        holder.itemView.itemImage.colorFilter = cf
        holder.itemView.itemImage.alpha = 0.5F // 128 = 0.5
    }

    fun setNews(news: List<RSSItem>) {
        val oldSize = this.news.size
        this.news = news.toMutableList()
//        notifyItemRangeInserted(oldSize, news.size)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setIsHome(value : Boolean){
        isHome = value
    }


}