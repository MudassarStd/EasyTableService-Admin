package com.android.foodorderingsystemadmin.activities


import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.isDigitsOnly
import com.android.foodorderingsystemadmin.databinding.ActivityAddItemBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AddItemActivity : AppCompatActivity() {

    //Food item Details
    private lateinit var foodName: String
    private lateinit var foodPrice: String
    private lateinit var foodDescription: String
    private lateinit var foodIngredients: MutableList<String>
    private var foodImageUri: Uri? = null

    private val binding: ActivityAddItemBinding by lazy {
        ActivityAddItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        foodIngredients = mutableListOf()


        binding.addItemButton.setOnClickListener {
            //Get data from fields
            foodName = binding.foodName.text.toString().trim()
            foodPrice = binding.foodPrice.text.toString().trim()
            foodDescription = binding.description.text.toString().trim()
            foodIngredients.add(binding.ingredients.text.toString().trim())

            if (!(foodName.isBlank() || foodPrice.isBlank() || foodDescription.isBlank() )) {
                uploadData()
                Toast.makeText(this, "Uploading new Menu Item", Toast.LENGTH_SHORT).show()
                finish()

            } else {
                Toast.makeText(this, "Fill  Details", Toast.LENGTH_SHORT).show()
            }
        }
        binding.selectImage.setOnClickListener {
            pickImage.launch("image/*")
        }
        binding.backButton.setOnClickListener {
            finish()
        }

    }

    private fun uploadData() {
        val db = FirebaseFirestore.getInstance()
        val menuItemsRef = db.collection("mainMenu")

        if (foodImageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val newItemKey = menuItemsRef.document().id  // Generate a unique document ID
            val imageRef = storageRef.child("menu_Images/${newItemKey}.jpg")
            val uploadTask = imageRef.putFile(foodImageUri!!)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // Create new menu Item
                    val newItem = hashMapOf(
                        "name" to foodName,
                        "price" to foodPrice,
                        "description" to foodDescription,
                        "ingredients" to foodIngredients[0],
                        "image" to downloadUrl.toString()
                    )

                    menuItemsRef.document(newItemKey).set(newItem)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Menu Item Uploaded Successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()
                        }
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to get download URL", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Image Upload Failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please Select an image", Toast.LENGTH_SHORT).show()
        }
    }


    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            binding.selectedImage.setImageURI(uri)
            foodImageUri = uri
        }
    }
}