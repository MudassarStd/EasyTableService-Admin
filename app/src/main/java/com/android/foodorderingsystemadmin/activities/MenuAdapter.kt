package com.android.foodorderingsystemadmin.activities



import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.foodorderingsystemadmin.R
import com.android.foodorderingsystemcustomer.model.MenuItem
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore


class MenuAdapter(
    private var menuItems:MutableList<MenuItem>,
    private val requiredContext : Context
): RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_item
            , parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItems.size


    fun updateMenu(menuItems : MutableList<MenuItem>){
        this.menuItems = menuItems
        notifyDataSetChanged()
    }

    inner class MenuViewHolder(view : View): RecyclerView.ViewHolder(view) {

        val menuItemName = view.findViewById<TextView>(R.id.FoodName)
        val menuItemPrice = view.findViewById<TextView>(R.id.FoodPrice)
        val menuItemImage = view.findViewById<ImageView>(R.id.FoodImage)
        val btnDelItem = view.findViewById<ImageButton>(R.id.deleteButton)

        init {
           view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
//                    openDetailActivity(position)
                }
            }

            btnDelItem.setOnClickListener {
                Toast.makeText(view.context, "Deleted Item from Menu", Toast.LENGTH_SHORT).show()
                val docId = menuItems[adapterPosition].docId
                menuItems.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)

                removeFromFireStore(docId)
            }
        }

        // set data into recyclerview item name, price, image
        fun bind(position: Int) {
            val menuItem = menuItems[position]

                    menuItemName.text = menuItem.foodName
                    menuItemPrice.text = "€"+menuItem.foodPrice
                    val uri = Uri.parse(menuItem.foodImage)
                    Glide.with(requiredContext).load(menuItem.foodImage)
                        .error(R.drawable.menu1)
                        .into(menuItemImage)
                }
        }

    private fun removeFromFireStore(docId: String) {
        val db = FirebaseFirestore.getInstance()
        val menuItemsRef = db.collection("mainMenu")

        menuItemsRef.document(docId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requiredContext, "Document successfully deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requiredContext, "Error deleting document: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
//    private fun openDetailActivity(position: Int) {
//
//        val menuItem = menuItems[position]
//
//        // intent to open detail activity and pass data
//        val intent = Intent(requiredContext, DetailsActivity::class.java).apply {
//            putExtra("MenuItemName",menuItem.foodName)
//            putExtra("MenuItemImage",menuItem.foodImage)
//            putExtra("MenuItemDescription",menuItem.foodDescription)
////            putExtra("MenuItemIngredients",menuItem.foodIngredient)
//            putExtra("MenuItemPrice",menuItem.foodPrice)
//        }
//        //start the detail activityf
//        requiredContext.startActivity(intent)
//    }

    interface OnClickListener{
        fun onItemCLick(position: Int)
    }

    }


