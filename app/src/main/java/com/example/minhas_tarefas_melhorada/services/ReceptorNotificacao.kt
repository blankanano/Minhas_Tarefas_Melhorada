package com.example.minhas_tarefas_melhorada.services

import com.example.minhas_tarefas_melhorada.R
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat

class ReceptorNotificacao : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Notification", "Notification received")

        if (context != null) {
            val notification: NotificationCompat.Builder = NotificationCompat.Builder(context, "FamilySilva")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.resources.getString(R.string.vencimento_tarefa))
                .setContentText(context.resources.getString(R.string.vencimento_tarefa_agora))
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(0, notification.build())
        }
    }
}