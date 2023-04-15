package com.example.minhas_tarefas_melhorada

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgotPassWord : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();

        findViewById<View>(R.id.textEntrar).setOnClickListener{
            retornarParaLogin()
        }

        findViewById<View>(R.id.btnRecuperar).setOnClickListener{
            val emailAddress = findViewById<EditText>(R.id.editEmail).text.toString()

            firebaseAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, R.string.email_enviado_com_sucesso, Toast.LENGTH_SHORT).show()
                    retornarParaLogin()
                }
            }
        }
    }

    fun retornarParaLogin(){
        val activity = Intent(this, LoginScreen::class.java);
        startActivity(activity)
        finish()
    }
}