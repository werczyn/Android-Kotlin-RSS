package com.example.projekt_3

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    private val btnLogin by lazy { findViewById<Button>(R.id.btnLogin) }
    private val btnGoRegister by lazy { findViewById<Button>(R.id.btnGoRegister) }

    private val textEmail by lazy { findViewById<EditText>(R.id.textEmail) }
    private val textPassword by lazy { findViewById<EditText>(R.id.textPassword) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener { view ->
            signIn(view,textEmail.text.toString(), textPassword.text.toString())
        }

        btnGoRegister.setOnClickListener{
            startActivity(Intent(this,RegisterActivity::class.java))
        }
    }

    private fun signIn(view: View, email: String, password: String){
        showMessage(view,"Authenticating...")

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if(task.isSuccessful){
                var intent = Intent(this, LoggedInActivity::class.java)
                intent.putExtra("id", mAuth.currentUser?.email)
                startActivity(intent)
            }else{
                showMessage(view,"Error: ${task.exception?.message}")
            }
        }
    }

    private fun showMessage(view:View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setAction("Action", null).show()
    }
}