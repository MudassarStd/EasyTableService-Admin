package com.android.foodorderingsystemadmin.activities


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.foodorderingsystemadmin.databinding.ActivityAllItemBinding
import com.android.foodorderingsystemcustomer.model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class AllItemActivity : AppCompatActivity() {



    private lateinit var menuItems:MutableList<MenuItem>

    private val binding : ActivityAllItemBinding by lazy {
        ActivityAllItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        retrieveMenuItems()

        binding.backButton.setOnClickListener {
            finish()
        }
    }
    private fun retrieveMenuItems() {
        val db = FirebaseFirestore.getInstance()
        val menuItemsRef = db.collection("mainMenu")
        menuItems = mutableListOf()

        menuItemsRef.get()
            .addOnSuccessListener { result ->
                val menuItemList = mutableListOf<MenuItem>()
                for (document in result.documents) {
                    val docId = document.id
                    val data = document.data ?: continue  // Skip empty documents

                    val foodName = data["name"].toString()
                    val foodPrice = data["price"].toString()
                    val foodDescription = data["description"].toString()
                    val foodImage = data["image"].toString()
                    val ingredients = data.get("foodIngredient") as? List<*>
                    val foodIngredientList: List<String>? = ingredients?.map { it as String }

                    val menuItem = MenuItem(docId, foodName, foodPrice, foodDescription, foodImage, foodIngredientList)
                    menuItems.add(menuItem)
                }

                Log.d("Firestorebjkbhjhgbjhhgjk", "Data From FireBase: ${menuItems}" )

                binding.allMenuItemsProgressBar.visibility = View.GONE
                setAdapter()
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting menu items:", exception)
                Toast.makeText(this, "FireStore data Fetch Error: ${exception}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setAdapter() {

        val adapter = MenuAdapter(menuItems, this)
        binding.MenuRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.MenuRecyclerView.adapter = adapter
    }

//    private fun deleteMenuItems(position: Int) {
//        val menuItemToDelete = menuItems[position]
//        val menuItemKey = menuItemToDelete.key
//        val foodMenuReference = database.reference.child("menu").child(menuItemKey!!)
//        foodMenuReference.removeValue().addOnCompleteListener { Task ->
//            if (Task.isSuccessful){
//                menuItems.removeAt(position)
//                binding.MenuRecyclerView.adapter?.notifyItemRemoved(position)
//            }else{
//                Toast.makeText(this, "Item Not Delete", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
}