package com.example.minhas_tarefas_melhorada

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    lateinit var mGoogleSignClient: GoogleSignInClient;

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid/tasks")
    val listItems = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        supportFragmentManager.beginTransaction().replace(R.id.fragmento_weather, FragmentWeather()).commit()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        val listView = findViewById<android.widget.ListView>(R.id.listViewTasks)
        listView.adapter = adapter

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();
        mGoogleSignClient = GoogleSignIn.getClient(this, gso);

        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        if(firebaseUser == null){
            val intent = Intent(this, LoginScreen::class.java);
            startActivity(intent);
        }

        findViewById<View>(R.id.imgLogout).setOnClickListener{
            firebaseAuth.signOut();
            mGoogleSignClient.signOut();

            val activity = Intent(this, LoginScreen::class.java);
            startActivity(activity)
            finish()
        }

        findViewById<View>(R.id.imgProfile).setOnClickListener{
            val activity = Intent(this, ProfileActivity::class.java);
            startActivity(activity)
            finish()
        }

        findViewById<View>(R.id.btnAddTask).setOnClickListener{
            val activity = Intent(this, TaskActivity::class.java);
            startActivity(activity)
            finish()
        }

        ref.addValueEventListener(object: ValueEventListener {
            val ctx = this@MainActivity;

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listItems.clear()

                for(child in dataSnapshot.children){
                    listItems.add(child.child("tituloTarefa").value.toString())
                }

                adapter.notifyDataSetChanged()

                listView.setOnItemLongClickListener { parent, view, position, id ->
                    val itemId =  dataSnapshot.children.toList()[position].key

                    if(itemId != null){
                        AlertDialog.Builder(ctx)
                            .setTitle(R.string.deletar_tarefa)
                            .setMessage(R.string.deseja_deletar_tarefa)
                            .setPositiveButton(R.string.sim){ dialog, which ->
                                ref.child(itemId).removeValue()
                                Toast.makeText(ctx, R.string.tarefa_deletada, Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton(R.string.nao){ dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
                    }

                    true
                }
                listView.setOnItemClickListener { parent, view, position, id ->
                    val itemId =  dataSnapshot.children.toList()[position].key

                    val activity = Intent(ctx, TaskActivity::class.java)
                    activity.putExtra("id", itemId)
                    startActivity(activity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(ctx, R.string.erro_carregar_tarefa, Toast.LENGTH_SHORT).show()
            }
        })

    }
}