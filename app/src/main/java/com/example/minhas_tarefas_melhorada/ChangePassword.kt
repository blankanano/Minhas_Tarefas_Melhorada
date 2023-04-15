package com.example.minhas_tarefas_melhorada

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ChangePassword : AppCompatActivity() {
    lateinit var editPassword: EditText
    lateinit var editConfirmPassword: EditText
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        editPassword = findViewById<EditText>(R.id.editPassword)
        editConfirmPassword = findViewById<EditText>(R.id.editConfirmPassword)

        findViewById<View>(R.id.btnChangePassword).setOnClickListener {
            alterarSenha()
        }
    }

    fun alterarSenha(){
        if(FuncoesCompartilhadas.campoVazio(editPassword.text.toString().trim(),
                                            editConfirmPassword.text.toString().trim())){
            println("Entrou na função - Campo Vazio");
            if (FuncoesCompartilhadas.SenhasIdenticas(editPassword.text.toString().trim(),
                                                      editConfirmPassword.text.toString().trim())){
                println("Entrou na função - Senhas identicas");
                if (FuncoesCompartilhadas.VerificarTamanhoSenha(editPassword.text.toString().trim())) {
                    val user = firebaseAuth.currentUser

                    user!!.updatePassword(editPassword.text.toString().trim())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, R.string.senha_alterada, Toast.LENGTH_SHORT).show()
                            }
                        }

                    val activity = Intent(this, ProfileActivity::class.java);
                    startActivity(activity)
                    finish()
                } else {
                    Toast.makeText(this, R.string.senha_diferentes, Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, R.string.tamanho_senha_inadequado, Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, R.string.preecha_todos_campos, Toast.LENGTH_SHORT).show()
        }
    }
}