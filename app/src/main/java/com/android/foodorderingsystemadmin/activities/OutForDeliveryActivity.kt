package com.android.foodorderingsystemadmin.activities


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.foodorderingsystemadmin.databinding.ActivityOutForDeliveryBinding
import com.google.firebase.firestore.FirebaseFirestore

class OutForDeliveryActivity : AppCompatActivity() {
    private val binding: ActivityOutForDeliveryBinding by lazy {
        ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        // retrieve and display completed order
        retrieveCompleteOrderDetail()

    }

    private fun retrieveCompleteOrderDetail() {
        val db = FirebaseFirestore.getInstance()
        val ordersRef = db.collection("orders")

        ordersRef.whereEqualTo("status", "ready").get()
            .addOnSuccessListener { documents ->
                val ordersList = mutableListOf<Order>()
                for (document in documents) {
                    val orderId = document.id
                    val tableNo = document.getLong("tableNo")?.toInt() ?: 0
                    val price = document.getLong("totalPrice")?.toString() ?: ""

                    val order = Order(orderId, tableNo, price)
                    ordersList.add(order)
                }

                binding.outFDProgressBar.visibility = View.GONE
                // Do something with the ordersList, such as updating a RecyclerView
//                binding.pendingOrdersProgressBar.visibility = View.GONE
                updateOrders(ordersList)

            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting orders: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateOrders(orders : List<Order>) {
        val adapter = DeliveryAdapter(this, orders.toMutableList() )
        binding.deliveryRecyclerView.adapter = adapter
        binding.deliveryRecyclerView.layoutManager = LinearLayoutManager(this)
        if (adapter.itemCount == 0)
        {
            binding.tvNoDispatchableOrders.visibility = View.VISIBLE
        }
    }

}