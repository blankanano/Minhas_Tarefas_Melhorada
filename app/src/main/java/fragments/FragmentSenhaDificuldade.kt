package com.example.minhas_tarefas_melhorada.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.example.minhas_tarefas_melhorada.FuncoesCompartilhadas
import com.example.minhas_tarefas_melhorada.R

class FragmentSenhaDificuldade : Fragment() {
    private lateinit var textDificuldadeSenha: TextView
    lateinit var textSenha: EditText
    lateinit var text: Editable;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_senha_dificuldade, container, false)

        textDificuldadeSenha = view.findViewById<TextView>(R.id.textViewDificuldade)
        textSenha = view.findViewById<EditText>(R.id.editPassword)

        textSenha.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    text = s
                }

                textDificuldadeSenha.text = FuncoesCompartilhadas.validarDifuculdadeSenha(requireContext(), text.toString())
            }
        })

        return view
    }
}