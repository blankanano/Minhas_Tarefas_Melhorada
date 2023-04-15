package com.example.minhas_tarefas_melhorada

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.minhas_tarefas_melhorada.databinding.ActivityTaskBinding
import com.example.minhas_tarefas_melhorada.services.ReceptorNotificacao
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class TaskActivity : AppCompatActivity() {

    val idUser = FirebaseAuth.getInstance().currentUser?.uid
    val db_ref = FirebaseDatabase.getInstance().getReference("/users/$idUser/tasks")

    var idTarefa: String = ""

    private lateinit var binding: ActivityTaskBinding;
    private lateinit var firebaseAnalytics: FirebaseAnalytics;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        carregarTarefa()

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val in_date = findViewById<EditText>(R.id.editData)
        val in_time = findViewById<EditText>(R.id.editHora)

        val current_date_time = Calendar.getInstance()
        val day = current_date_time.get(Calendar.DAY_OF_MONTH)
        val month = current_date_time.get(Calendar.MONTH)
        val year = current_date_time.get(Calendar.YEAR)
        val hour = current_date_time.get(Calendar.HOUR_OF_DAY)
        val minute = current_date_time.get(Calendar.MINUTE)

        in_date.setText(String.format("%02d/%02d/%04d", day, month + 1, year))
        in_time.setText(String.format("%02d:%02d", hour, minute))

        findViewById<Button>(R.id.btndata).setOnClickListener{
            val datePickerDialog = DatePickerDialog(this, {_, yearOfYear, monthOfYear, dayOfMonth ->
                in_date.setText(String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, yearOfYear))
            }, year, month, day)
            datePickerDialog.show()
        }

        findViewById<Button>(R.id.btnhora).setOnClickListener{
            val timePickerDialog = TimePickerDialog(this, {_, hourOfDay, minuteOfHour ->
                in_time.setText(String.format("%02d:%02d", hourOfDay, minuteOfHour))
            }, hour, minute, true)
            timePickerDialog.show()
        }

        findViewById<Button>(R.id.btnSalvarTarefa).setOnClickListener{
            SalvarTarefa()
        }

        criarNotificacao()
    }

    fun carregarTarefa(){
        this.idTarefa = intent.getStringExtra("id") ?: ""
        if(idTarefa === "") return

        val ref = FirebaseDatabase.getInstance().getReference("/users/$idUser/tasks/$idTarefa")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()) return

                findViewById<EditText>(R.id.editTituloTarefa).setText(snapshot.child("tituloTarefa").value.toString())
                findViewById<EditText>(R.id.editDescricaoTarefa).setText(snapshot.child("descricaoTarefa").value.toString())
                findViewById<EditText>(R.id.editData).setText(snapshot.child("dataTarefa").value.toString())
                findViewById<EditText>(R.id.editHora).setText(snapshot.child("horaTarefa").value.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TaskActivity, R.string.erro_carregar_tarefa, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun SalvarTarefa(){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, idUser)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "create_task")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

        if(idTarefa !== ""){
            val ref = FirebaseDatabase.getInstance().getReference("/users/$idUser/tasks/$idTarefa")

            ref.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(!snapshot.exists()) return
                    val task = snapshot.value as HashMap<String, String>

                    task["tituloTarefa"] = findViewById<EditText>(R.id.editTituloTarefa).text.toString()
                    task["descricaoTarefa"] = findViewById<EditText>(R.id.editDescricaoTarefa).text.toString()
                    task["dataTarefa"] = findViewById<EditText>(R.id.editData).text.toString()
                    task["horaTarefa"] = findViewById<EditText>(R.id.editHora).text.toString()

                    ref.setValue(task)
                    Toast.makeText(this@TaskActivity, R.string.tarefa_atualizada_sucesso, Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@TaskActivity, R.string.erro_atualizar_tarefa, Toast.LENGTH_SHORT).show()
                }
            })
        }else{
            val tituloTarefa = findViewById<EditText>(R.id.editTituloTarefa)
            val descricaoTarefa = findViewById<EditText>(R.id.editDescricaoTarefa)
            val dataTarefa = findViewById<EditText>(R.id.editData)
            val horaTarefa = findViewById<EditText>(R.id.editHora)

            val task =  hashMapOf(
                "tituloTarefa" to tituloTarefa.text.toString(),
                "descricaoTarefa" to descricaoTarefa.text.toString(),
                "dataTarefa" to dataTarefa.text.toString(),
                "horaTarefa" to horaTarefa.text.toString(),
            )

            val novoElemento = db_ref.push()
            novoElemento.setValue(task)

            Toast.makeText(this, R.string.tarefa_criada_sucesso, Toast.LENGTH_SHORT).show()

            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }
        agendarNotificacao(findViewById<EditText>(R.id.editData).text.toString(),
                           findViewById<EditText>(R.id.editHora).text.toString())
    }

    private fun criarNotificacao(){
        val name = "Notificação do canal Family Silva"
        val descriptionText = "Canal de notificação Family Silva"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("FamilySilva", name, importance).apply {
            description = descriptionText
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun agendarNotificacao(data: String, hora: String){
        val intent = Intent(this, ReceptorNotificacao::class.java)
        val title = "Título da notificação"
        val message = "Mensagem da notificação"

        intent.putExtra("title", title)
        intent.putExtra("message", message)

        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        val data_dia = data.substring(0, 2).toInt()
        val data_mes = data.substring(3, 5).toInt() - 1
        val data_ano = data.substring(6, 10).toInt()
        val hora_hora = hora.substring(0, 2).toInt()
        val hora_minuto = hora.substring(3, 5).toInt()

        calendar.set(
            data_ano,
            data_mes,
            data_dia,
            hora_hora,
            hora_minuto,
            0
        )
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
        Toast.makeText(this, R.string.notificao_agendada, Toast.LENGTH_SHORT).show()
    }

}