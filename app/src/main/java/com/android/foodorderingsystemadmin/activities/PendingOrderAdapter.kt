package com.android.foodorderingsystemadmin.activities


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.foodorderingsystemadmin.R
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class PendingOrderAdapter(val context: Context, val orders : MutableList<Order>
) : RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {

    private lateinit var acceptedOrders : MutableList<Order>

    init {
        acceptedOrders = mutableListOf()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(
            R.layout.pending_order_items
            , parent, false)
        return PendingOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = orders.size

    inner class PendingOrderViewHolder(view : View) :
        RecyclerView.ViewHolder(view) {

            val table = view.findViewById<TextView>(R.id.tvTableNo)
            val orderAmount = view.findViewById<TextView>(R.id.tvTotalAmountOrder)
            val btnAccept = view.findViewById<Button>(R.id.btnOrderAccept)
            val btnReject = view.findViewById<Button>(R.id.btnOrderReject)
            val tvAccepted = view.findViewById<TextView>(R.id.tvOrderAccepted)
            val orderImage = view.findViewById<ImageView>(R.id.orderFoodImage)

        fun bind(position: Int) {
                table.text = "Table No. "+orders[position].tableNo.toString()
                orderAmount.text = "Bill - €"+orders[position].price

                Glide.with(context).load(R.drawable.menu1).into(orderImage)
            }

        init {
            btnAccept.setOnClickListener {
                val orderId = orders[adapterPosition].orderId
                btnAccept.visibility = View.GONE
                tvAccepted.visibility = View.VISIBLE
                acceptedOrders.add(orders[adapterPosition])
                orders.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)

                updateOrdersInCloud(orderId)
            }

            btnReject.setOnClickListener {
                val orderId = orders[adapterPosition].orderId
                orders.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)

                updateOrdersInCloudRejection(orderId)
            }

            view.setOnClickListener {
                val intent = Intent(context, OrderDetailsActivity::class.java)
                intent.putExtra("orderId", orders[adapterPosition].orderId)
                context.startActivity(intent)
            }
        }

        }

    private fun updateOrdersInCloud(orderId: String) {
        val db = FirebaseFirestore.getInstance()
        val ordersRef = db.collection("orders").document(orderId)

        ordersRef.update("status", "ready")
            .addOnSuccessListener {
                Toast.makeText(context, "Order status updated to ready", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error updating order: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

 private fun updateOrdersInCloudRejection(orderId: String) {
        val db = FirebaseFirestore.getInstance()
        val ordersRef = db.collection("orders").document(orderId)

        ordersRef.update("status", "cancelled")
            .addOnSuccessListener {
                Toast.makeText(context, "Order status updated to Cancelled", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error Cancelling order: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

    }
