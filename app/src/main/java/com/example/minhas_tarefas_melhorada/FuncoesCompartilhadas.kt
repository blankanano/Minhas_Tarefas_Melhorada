package com.example.minhas_tarefas_melhorada

import android.content.Context

class FuncoesCompartilhadas {

    companion object {
        val confIniciais = ConfiguracoesIniciais();

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
            println(confIniciais.QuantidadeCaracteresSenhaMin.toString())
            return (pSenha.length >= confIniciais.QuantidadeCaracteresSenhaMin);
        }

        fun validarDifuculdadeSenha(context: Context, pPassword: String): String {
            val temMaiuscula = pPassword.any { it.isUpperCase() }
            val temMinuscula = pPassword.any { it.isLowerCase() }
            val temDigito = pPassword.any { it.isDigit() }
            val temCaracteresEspeciais = pPassword.any { !it.isLetterOrDigit() }

            if (pPassword.length == 0) {
                return context.resources.getString(R.string.sem_senha)
            } else if (pPassword.length < confIniciais.QuantidadeCaracteresSenhaMin) {
                return context.resources.getString(R.string.senha_fraca)
            } else if (!temMaiuscula || !temMinuscula || !temDigito || !temCaracteresEspeciais) {
                return context.resources.getString(R.string.senha_media)
            } else {
                return context.resources.getString(R.string.senha_forte)
            }
        }
    }
}