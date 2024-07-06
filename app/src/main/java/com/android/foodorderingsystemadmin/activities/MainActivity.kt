package com.android.foodorderingsystemadmin.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.foodorderingsystemadmin.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var pendingOrders : String = ""
    private var readyOrders : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.addMenu.setOnClickListener{
            val intent = Intent (this,AddItemActivity::class.java)
            startActivity(intent)
        }

        binding.allItemsMenu.setOnClickListener {
            val intent = Intent (this,AllItemActivity::class.java)
            startActivity(intent)
        }
//
        binding.outForDelivery.setOnClickListener {
            val intent = Intent (this,OutForDeliveryActivity::class.java)
            startActivity(intent)
        }

        binding.pendingOrderText.setOnClickListener {
            val intent = Intent (this,PendingOrdersActivity::class.java)
            startActivity(intent)
        }

        binding.pendingOrderCount.setOnClickListener {
            val intent = Intent (this,PendingOrdersActivity::class.java)
            startActivity(intent)
        }

        binding.logOutButton.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        pendingOrders()
//        completedOrders()
//        wholeTimeEarning()

    }


    private fun pendingOrders() {
        val db = FirebaseFirestore.getInstance()
            var pendingOrdersCount = 0
            var readyOrdersCount = 0

            db.collection("orders")
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener { documents ->
                    pendingOrdersCount = documents.size()
                    println("Pending orders: $pendingOrdersCount")

                    binding.pendingOrderCount.text = pendingOrdersCount.toString()
                }
                .addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                }

            db.collection("orders")
                .whereEqualTo("status", "ready")
                .get()
                .addOnSuccessListener { documents ->
                    readyOrdersCount = documents.size()

                    binding.readyOrderCount.text = readyOrdersCount.toString()

                }
                .addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                }
        }

    override fun onResume() {
        super.onResume()
        pendingOrders()
    }
    }
