package com.example.minhas_tarefas_melhorada

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

@Suppress("DEPRECATION")
class LoginScreen : AppCompatActivity() {

    lateinit var editEmail: EditText
    lateinit var editPassword: EditText

    val Req_Code:Int=123;
    lateinit var mGoogleSignInClient: GoogleSignInClient;
    private lateinit var firebaseAuth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)
        supportActionBar?.hide()

        editEmail = findViewById<EditText>(R.id.editEmail)
        editPassword = findViewById<EditText>(R.id.editPassword)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        firebaseAuth = FirebaseAuth.getInstance();

        findViewById<View>(R.id.btnregistrarView).setOnClickListener{
            val activity = Intent(this, CreateAccount::class.java);
            startActivity(activity);
        }

        findViewById<View>(R.id.btnEntrarComGoogle).setOnClickListener {
            Toast.makeText(this, R.string.entrar_com_google, Toast.LENGTH_SHORT).show()
            entrarComGoogle();
        }

        findViewById<View>(R.id.btnEntrar).setOnClickListener {
            entrar()
        }

        findViewById<View>(R.id.textEsqueciSenha).setOnClickListener {
            val activity = Intent(this, ForgotPassWord::class.java);
            startActivity(activity);
        }
    }

    private fun entrarComGoogle(){
        val signIntent: Intent = mGoogleSignInClient.signInIntent;
        startActivityForResult(signIntent, Req_Code);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Req_Code){
            val result = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleResult(result);
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>){
        try{
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java);
            Toast.makeText(this, R.string.logado_sucesso, Toast.LENGTH_SHORT).show();
            if(account != null){
                UpdateUser(account)
            }
        }catch (e: ApiException){
            println(e)
            Toast.makeText(this, R.string.falha_logar, Toast.LENGTH_SHORT).show();
        }
    }

    private fun UpdateUser(account: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(account.idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {task ->
            if(task.isSuccessful){
                val intent = Intent(this, MainActivity::class.java);
                startActivity(intent);
                finish()
            }
        }
    }

    private fun entrar(){
        println(editEmail.text.toString().trim());
        println(editPassword.text.toString().trim())
        if(FuncoesCompartilhadas.campoVazio(editEmail.text.toString().trim(), editPassword.text.toString().trim())){
            println("Entrou na função - Entrar");

            val userEmail = editEmail.text.toString().trim()
            val userPassword = editPassword.text.toString().trim()

            firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
                    if(firebaseUser != null && firebaseUser.isEmailVerified()){
                        startActivity(Intent(this, MainActivity::class.java))
                        Toast.makeText(this, R.string.logado_sucesso, Toast.LENGTH_SHORT).show()
                        finish()
                    }else if(firebaseUser != null && !firebaseUser.isEmailVerified()){
                        firebaseAuth.signOut()
                        Toast.makeText(this, R.string.verifique_seu_email, Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, R.string.falha_logar, Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, R.string.falha_logar, Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            Toast.makeText(this, R.string.preecha_todos_campos, Toast.LENGTH_SHORT).show()
        }
    }

}