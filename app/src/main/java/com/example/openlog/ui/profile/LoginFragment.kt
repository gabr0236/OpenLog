package com.example.openlog.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.openlog.R
import com.example.openlog.databinding.FragmentAuthBinding
import com.example.openlog.databinding.FragmentLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            login.setOnClickListener {
                //if (isInputValid(this.email.toString(), this.password.toString())) {
                    signIn("john@doe.com", "qwerty")
                /*} else {
                    Toast.makeText(
                        requireContext(),
                        "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }*/
            }
        }
    }

    private fun isInputValid(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Log.d("LoginFragment", "Input invalid")
            return false
        }
        Log.d("LoginFragment", "Input valid")
        return true
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity(), OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    // Sign in success, redirect to log screen
                    Log.d("LoginFragment", "signInWithEmail:success")
                    val action = LoginFragmentDirections.actionLoginFragmentToAddLogItemFragment()
                    findNavController().navigate(action)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("LoginFragment", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(),
                        "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Do nothing
                }
            })
    }
}