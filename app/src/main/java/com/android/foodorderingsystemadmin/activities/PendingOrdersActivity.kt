package com.android.foodorderingsystemadmin.activities


import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.foodorderingsystemadmin.databinding.ActivityPendingOrdersBinding
import com.google.firebase.firestore.FirebaseFirestore

class PendingOrdersActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityPendingOrdersBinding
    private var listOfName: MutableList<String> = mutableListOf()
    private var listOfTotalPrice: MutableList<String> = mutableListOf()
    private var listOfImageFirstFoodOrder: MutableList<String> = mutableListOf()
    private var orders: MutableList<Order> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)



        getOrdersDetails()

        binding.backButton.setOnClickListener {
            finish()
        }


    }

    private fun getOrdersDetails() {
        val db = FirebaseFirestore.getInstance()
        val ordersRef = db.collection("orders")

        ordersRef.whereEqualTo("status", "pending").get()
            .addOnSuccessListener { documents ->
                val ordersList = mutableListOf<Order>()
                for (document in documents) {
                    val orderId = document.id
                    val tableNo = document.getLong("tableNo")?.toInt() ?: 0
                    val price = document.getLong("totalPrice")?.toString() ?: ""

                    val order = Order(orderId, tableNo, price)
                    ordersList.add(order)
                }

                // Do something with the ordersList, such as updating a RecyclerView
                binding.pendingOrdersProgressBar.visibility = View.GONE
                updateOrders(ordersList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@PendingOrdersActivity, "Error getting orders: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun updateOrders(orders: List<Order>) {
        binding.pendingOrderRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter =
            PendingOrderAdapter(this, orders.toMutableList())
        binding.pendingOrderRecyclerView.adapter = adapter

        if(adapter.itemCount == 0)
        {
          binding.tvNoPendingOrders.visibility = View.VISIBLE
        }
    }

//    override fun onItemClickListener(position: Int) {
//        val intent = Intent(this, OrderDetailsActivity::class.java)
//        val userOrderDetails = listOfOrderItem[position]
//        intent.putExtra("UserOrderDetails", userOrderDetails)
//        startActivity(intent)
//    }
//
//    override fun onItemAcceptClickListener(position: Int) {
//        // handle item acceptance and update database
//        val childItemPushKey = listOfOrderItem[position].itemPushKey
//        val clickItemOrderReference = childItemPushKey?.let {
//            database.reference.child("OrderDetails").child(it)
//        }
//
//        clickItemOrderReference?.child("orderAccepted")?.setValue(true)
//        updatedOrderAcceptStatus(position)
//
//    }
//
//    override fun onItemDispatchClickListener(position: Int) {
//        // handle item Dispatch and update database
//        val dispatchItemPushKey = listOfOrderItem[position].itemPushKey
//        val dispatchItemOrderReference = database.reference.child("CompletedOrder").child(dispatchItemPushKey!!)
//        dispatchItemOrderReference?.setValue(listOfOrderItem[position])
//            ?.addOnSuccessListener {
//                deleteThisItemFromOrderDetails(dispatchItemPushKey)
//            }
//
//    }
//
//    private fun deleteThisItemFromOrderDetails(dispatchItemPushKey: String) {
//        val orderDetailsItemsReference = database.reference.child("OrderDetails").child(dispatchItemPushKey)
//        orderDetailsItemsReference.removeValue()
//            .addOnSuccessListener {
//                Toast.makeText(this, "Order Is Dispatched", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener {
//                Toast.makeText(this, "Order Is Not Dispatched", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun updatedOrderAcceptStatus(position: Int) {
//        // update order acceptance in User's BuyHistory and OrderDetails
//        val userIdOfCLickedItem = listOfOrderItem[position].userUid
//        val pushKeyOfClickedItem = listOfOrderItem[position].itemPushKey
//        val buyHistoryReference =
//            database.reference.child("user").child(userIdOfCLickedItem!!).child("BuyHistory")
//                .child(pushKeyOfClickedItem!!)
//        buyHistoryReference.child("orderAccepted").setValue(true)
//        databaseOrderDetails.child(pushKeyOfClickedItem).child("orderAccepted").setValue(true)
//    }
}
