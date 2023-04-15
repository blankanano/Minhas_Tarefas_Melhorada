package com.example.minhas_tarefas_melhorada

import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class Funcoes_Compartilhadas {


    companion object {
        val confIniciais = Configuracoes_Iniciais();

        fun campoVazio(vararg pCampos: String): Boolean {
            for (lcCampo in pCampos) {

                println("Entrando na função");
                println(lcCampo);

                if (!lcCampo.isEmpty()) {
                    return true
                }
            }
            return false
        }

         fun SenhasIdenticas(pSenha: String, pConfirmaSenha: String): Boolean{
            return (pSenha == pConfirmaSenha);
        }

        fun VerificarTamanhoSenha(pSenha: String): Boolean{
            return (pSenha.length >= confIniciais.QuantidadeCaracteresSenhaMin);
        }

        fun validarDifuculdadeSenha(pPassword: String): String {
            val temMaiuscula = pPassword.any { it.isUpperCase() }
            val temMinuscula = pPassword.any { it.isLowerCase() }
            val temDigito = pPassword.any { it.isDigit() }
            val temCaracteresEspeciais = pPassword.any { !it.isLetterOrDigit() }

            if (pPassword.length == 0) {
                return R.string.sem_senha.toString()
            } else if (pPassword.length < confIniciais.QuantidadeCaracteresSenhaMin) {
                return R.string.senha_fraca.toString()
            } else if (!temMaiuscula || !temMinuscula || !temDigito || !temCaracteresEspeciais) {
                return R.string.senha_media.toString()
            } else {
                return R.string.senha_forte.toString()
            }
        }
    }
}