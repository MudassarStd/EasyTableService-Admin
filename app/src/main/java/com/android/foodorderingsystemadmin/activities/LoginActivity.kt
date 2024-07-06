package com.android.foodorderingsystemadmin.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.foodorderingsystemadmin.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.loginbutton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty())
            {
                binding.loginProgressBar.visibility = View.VISIBLE
                binding.textView15.visibility = View.GONE
                loginUser(email, password)

            }
            else{
                Toast.makeText(this, "Fields cannot be empty.",
                    Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    if (user != null) {
                        // Navigate to the main activity or dashboard
                        binding.loginProgressBar.visibility = View.GONE
                        binding.textView15.visibility = View.VISIBLE

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    binding.loginProgressBar.visibility = View.GONE
                    binding.textView15.visibility = View.VISIBLE
                    Toast.makeText(this, "Invalid Email or password.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}
