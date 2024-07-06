package com.android.foodorderingsystemadmin.activities


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.foodorderingsystemadmin.databinding.ActivityOrderDetailsBinding
import com.android.foodorderingsystemcustomer.model.MenuItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.firestore.FieldPath

class OrderDetailsActivity : AppCompatActivity() {
    private val binding: ActivityOrderDetailsBinding by lazy {
        ActivityOrderDetailsBinding.inflate(layoutInflater)
    }

    private var customerName: String = ""
    private var tableNo: Long = -1
    private var orderBill: String = ""
    private var orderStatus: String = ""
    private var menuItemsIds: List<String> = listOf()
    private var itemQuantities: List<Long> = listOf()
//    private var menuItems : MutableList<MenuItem> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        itemQuantities = listOf()
        getDataFromIntent()

    }

    private fun getDataFromIntent() {
        val orderId = intent.getStringExtra("orderId")!!

        loadDataFromFireStore(orderId)
    }

    private fun loadDataFromFireStore(orderId: String) {
        val db = FirebaseFirestore.getInstance()
        val ordersRef = db.collection("orders")

        ordersRef.document(orderId).get()
            .addOnSuccessListener { document ->
                // Extract data from each document
                customerName = document.getString("customerName") ?: ""
                itemQuantities = document.get("itemQuantities") as? List<Long> ?: emptyList()
                menuItemsIds = document.get("items") as? List<String> ?: emptyList()
                val orderDate = document.getTimestamp("orderDate")?.toDate()
                orderStatus = document.getString("status") ?: ""
                tableNo = document.getLong("tableNo") ?: 0
                orderBill = (document.getDouble("totalPrice") ?: 0.0).toString()

                populateData()
                // Format the orderDate if not null
                val formattedOrderDate = orderDate?.let {
                    SimpleDateFormat("dd MMMM yyyy 'at' HH:mm:ss z", Locale.getDefault()).format(it)
                } ?: ""


            }

            .addOnFailureListener { exception ->
                Log.e("OrderDetails", "Error getting order details: ${exception.message}")
            }
    }

    private fun populateData() {

        getMenuItemsFromId(menuItemsIds)

        binding.nameOrderDetails.text = customerName
        binding.etOrderBill.text = "€"+orderBill
        binding.etTableNoOrderDetails.text = tableNo.toString()

    }

    private fun getMenuItemsFromId(menuItemIds: List<String>) {
        val db = FirebaseFirestore.getInstance()
        val menuItemsRef = db.collection("mainMenu")
        val menuItems :MutableList<MenuItem> = mutableListOf()

        // Query documents where the document ID is in menuItemIds list
        menuItemsRef.whereIn(FieldPath.documentId(), menuItemIds)
            .get()
                .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                for (document in querySnapshot) {
                    // Extract data from each document
                    val description = document.getString("description") ?: ""
                    val image = document.getString("image") ?: ""
                    val ingredients = document.get("ingredients") as? List<String> ?: emptyList()
                    val name = document.getString("name") ?: ""
                    val price = document.getString("price") ?: ""

                    val item = MenuItem(document.id, name, price, description, image, ingredients)
                    menuItems.add(item)
                }

                binding.orderDetailsProgressBar.visibility = View.GONE
                Log.d("MenuItemfdsf", " menu items: ${menuItems}")
                setAdapter(menuItems)
            }
            .addOnFailureListener { exception ->
                Log.e("MenuItemfdsf", "Error getting menu items: ${exception.message}")
            }
    }

    private fun setAdapter(menuItems : List<MenuItem>) {
        binding.orderDetailsRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = OrderDetailsAdapter(this,menuItems.toMutableList(), itemQuantities)
        binding.orderDetailsRecyclerView.adapter = adapter
    }
}
