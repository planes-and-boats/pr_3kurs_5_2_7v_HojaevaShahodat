package com.dignerdranch.anroid.pract_5_2_hojaeva

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class LogInFragment : Fragment(R.layout.fragment_log_in) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireContext().getSharedPreferences("DATA", Context.MODE_PRIVATE)

        val etLogIn = view.findViewById<EditText>(R.id.etLogin)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnLogIn = view.findViewById<Button>(R.id.btnLogIn)

        btnLogIn.setOnClickListener {
            val login = etLogIn.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    R.string.errorEmptyData,
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val savedLogin = sharedPref.getString("LOGIN", null)
            val savedPassword = sharedPref.getString("PASSWORD", null)

            if (savedLogin == null || savedPassword == null) {
                val prefEditor = sharedPref.edit()
                prefEditor.putString("LOGIN", login)
                prefEditor.putString("PASSWORD", password)
                prefEditor.apply()

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragments_parent, SearchMovieFragment())
                    .commit()
            }else {
                if (savedLogin != login || savedPassword != password) {
                    Toast.makeText(
                        requireContext(),
                        R.string.errorWrongData,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragments_parent, SearchMovieFragment())
                    .commit()
            }

        }


    }
}