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

class DeliveryAdapter(val context: Context, val orders : MutableList<Order>
) : RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(
            R.layout.delivery_item
            , parent, false)
        return DeliveryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = orders.size

    inner class DeliveryViewHolder(view : View) :
        RecyclerView.ViewHolder(view) {

        val table = view.findViewById<TextView>(R.id.CustomerNameDispatching)
        val orderAmount = view.findViewById<TextView>(R.id.tvOrderPaymentTotalDispatching)
        val btnDispatch = view.findViewById<Button>(R.id.btnOrderDispatch)

        fun bind(position: Int) {
            table.text = "Table No. "+orders[position].tableNo.toString()
            orderAmount.text = "Bill - €"+orders[position].price
        }

        init {
            btnDispatch.setOnClickListener {
                val orderId = orders[adapterPosition].orderId
                orders.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)

                updateOrdersInCloud(orderId)
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

        ordersRef.update("status", "dispatched")
            .addOnSuccessListener {
                Toast.makeText(context, "Order status updated to Dispatched", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error updating order: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }



}
