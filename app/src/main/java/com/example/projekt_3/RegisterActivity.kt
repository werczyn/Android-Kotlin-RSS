package com.example.projekt_3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    lateinit var mDatabase : FirebaseFirestore

    private val emailTxt by lazy{ findViewById<EditText>(R.id.textRegisterEmail) }
    private val passwordTxt by lazy{ findViewById<EditText>(R.id.textRegisterPassword) }
    private val nameTxt by lazy{ findViewById<EditText>(R.id.textRegisterName) }

    private val btnRegister by lazy{ findViewById<Button>(R.id.btnRegister) }
    private val btnBackLogin by lazy{ findViewById<Button>(R.id.btnBackLogin) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseFirestore.getInstance()

        btnRegister.setOnClickListener{
            registerUser()
        }

        btnBackLogin.setOnClickListener{
            startActivity(Intent(this,MainActivity::class.java))
        }

    }

    private fun registerUser () {
        val email = emailTxt.text.toString()
        val password = passwordTxt.text.toString()
        val name = nameTxt.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    val uid = user!!.uid



                    mDatabase.collection("names").document(uid).set(mapOf("name" to name))
                    startActivity(Intent(this, MainActivity::class.java))
                    Toast.makeText(this, "Successfully registered :)", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        this,
                        "Error registering, try again later :(",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }else {
            Toast.makeText(this,"Please fill up the Credentials :|", Toast.LENGTH_LONG).show()
        }
    }

}