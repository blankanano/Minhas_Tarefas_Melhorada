package com.example.minhas_tarefas_melhorada

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.minhas_tarefas_melhorada.fragments.FragmentSenhaDificuldade
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

@Suppress("DEPRECATION")
class CreateAccount : AppCompatActivity() {
    lateinit var editEmail: EditText
    lateinit var editPassword: FragmentSenhaDificuldade
    lateinit var editConfirmPassword: EditText
    lateinit var createAccountInputArray: Array<Any>

    val Req_Code:Int=123;
    lateinit var mGoogleSignInClient: GoogleSignInClient;
    private lateinit var firebaseAuth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        supportActionBar?.hide()

        FirebaseApp.initializeApp(this)
        firebaseAuth = FirebaseAuth.getInstance();

        editEmail = findViewById<EditText>(R.id.editEmail)
        editPassword = supportFragmentManager.findFragmentById(R.id.editPassword) as FragmentSenhaDificuldade
        editConfirmPassword = findViewById<EditText>(R.id.editConfirmaPassword)

        createAccountInputArray = arrayOf(editEmail, editPassword, editConfirmPassword)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById<View>(R.id.loginView).setOnClickListener{
            val activity = Intent(this, LoginScreen::class.java);
            startActivity(activity);
        }

        findViewById<View>(R.id.btnEntrar).setOnClickListener {
            Toast.makeText(this, R.string.registrar_com_google, Toast.LENGTH_SHORT).show()
            entrarComGoogle();
        }

        findViewById<View>(R.id.btnCriarConta).setOnClickListener {
            entrar()
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
        println("Entrou na função - Entrar");
        if(FuncoesCompartilhadas.campoVazio(editEmail.text.toString().trim(),
                                             editPassword.text.toString().trim(),
                                             editConfirmPassword.text.toString().trim())){
            println("Entrou na função - Campo Vazio");
            if (FuncoesCompartilhadas.SenhasIdenticas(editPassword.text.toString().trim(),
                                                      editConfirmPassword.text.toString().trim())){
                println("Entrou na função - Senhas identicas");
                if (FuncoesCompartilhadas.VerificarTamanhoSenha(editPassword.text.toString().trim())) {
                    println("Entrou na função - Verificar tamanho da senha");
                    val userEmail = editEmail.text.toString().trim()
                    val userPassword = editPassword.text.toString().trim()
                    firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener{task ->
                        if(task.isSuccessful){
                            enviarEmailVerificacao()
                            Toast.makeText(this, R.string.usuario_criado_sucesso, Toast.LENGTH_SHORT).show();
                            finish()
                        }else{
                            val exception = task.exception;
                            if(exception is FirebaseAuthException && exception.errorCode == "ERROR_EMAIL_ALREADY_IN_USE"){
                                Toast.makeText(this, R.string.email_cadastrado, Toast.LENGTH_SHORT).show();
                            }else if(exception is FirebaseAuthException && exception.errorCode == "ERROR_WEAK_PASSWORD"){
                                Toast.makeText(this, R.string.senha_fraca, Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(this, R.string.erro_criar_usuario, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }else{
                    Toast.makeText(this, R.string.tamanho_senha_inadequado, Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, R.string.senha_diferentes, Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, R.string.preecha_todos_campos, Toast.LENGTH_SHORT).show()
        }
    }

    private fun enviarEmailVerificacao(){
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        firebaseUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(this, R.string.email_enviado_com_sucesso, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}