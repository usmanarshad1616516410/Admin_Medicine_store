package com.example.adminblinkit.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.adminblinkit.api.ApiUtilities
import com.example.adminblinkit.models.CartProducts
import com.example.adminblinkit.models.Notification
import com.example.adminblinkit.models.NotificationData
import com.example.adminblinkit.models.Orders
import com.example.adminblinkit.models.Product
import com.example.adminblinkit.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class AdminViewModel : ViewModel() {

    // product push key
    private val productRef = FirebaseDatabase.getInstance().getReference("Admins")
    // private val newItemKey: String? = productRef.push().key

    private val _isImagesUploaded = MutableStateFlow(false)
    var isImagesUploaded: StateFlow<Boolean> = _isImagesUploaded

    private val _isImagesDelete = MutableStateFlow(false)
    var isImagesDelete: StateFlow<Boolean> = _isImagesDelete

    private val _downloadsUrls = MutableStateFlow<ArrayList<String?>>(arrayListOf())
    var downloadsUrls: StateFlow<ArrayList<String?>> = _downloadsUrls



    private val _isProductSaved = MutableStateFlow(false)
    var isProductSaved: StateFlow<Boolean> = _isProductSaved

    private val _isProductDelete = MutableStateFlow(false)
    var isProductDelete: StateFlow<Boolean> = _isProductDelete


    fun saveImageInDB(imageUri: ArrayList<Uri>) {
        val downloadUrls = ArrayList<String?>()

        imageUri.forEach { uri ->
            val imageRef =
                FirebaseStorage.getInstance().reference.child(Utils.currentUser().toString())
                    .child("images").child(UUID.randomUUID().toString())
            imageRef.putFile(uri).continueWithTask {
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                val url = task.result
                downloadUrls.add(url.toString())

                if (downloadUrls.size == imageUri.size) {
                    _isImagesUploaded.value = true
                    _downloadsUrls.value = downloadUrls
                }
            }
        }
    }
    fun deleteImageInDB(imageUri: ArrayList<String?>?){
        // Implement deletion from Storage here
        // For example:
        val storageRef = FirebaseStorage.getInstance().reference
        imageUri?.forEach { imageUrl ->
            val imageRef = storageRef.child(Utils.currentUser().toString())
                .child("images").child(UUID.randomUUID().toString())
            imageRef.delete().addOnSuccessListener {
                // Image deleted successfully
                _isImagesDelete.value = true
            }.addOnFailureListener { exception ->
                // Handle any errors
                _isImagesDelete.value = false
            }
        }
    }

    fun saveProduct(product: Product) {
//  product Store in admin node
        FirebaseDatabase.getInstance().getReference("Admins").child(Utils.currentUser()!!)
            .child("AllProducts").child(product.productRandomId!!).setValue(product)
            .addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference("Admins").child(Utils.currentUser()!!)
                    .child("ProductCategory/${product.productCategory}")
                    .child(product.productRandomId!!)
                    .setValue(product)
                    .addOnSuccessListener {
                        FirebaseDatabase.getInstance().getReference("Admins")
                            .child(Utils.currentUser()!!)
                            .child("ProductType/${product.productType}")
                            .child(product.productRandomId!!)
                            .setValue(product)
                            .addOnSuccessListener {

// product store in All Product details node
                                FirebaseDatabase.getInstance().getReference("AllProductsDetails")
                                    .child("AllProducts").child(product.productRandomId!!)
                                    .setValue(product)
                                    .addOnSuccessListener {
                                        FirebaseDatabase.getInstance()
                                            .getReference("AllProductsDetails")
                                            .child("ProductCategory/${product.productCategory}")
                                            .child(product.productRandomId!!)
                                            .setValue(product)
                                            .addOnSuccessListener {
                                                FirebaseDatabase.getInstance()
                                                    .getReference("AllProductsDetails")
                                                    .child("ProductType/${product.productType}")
                                                    .child(product.productRandomId!!)
                                                    .setValue(product)
                                                    .addOnSuccessListener {
                                                        _isProductSaved.value = true

                                                    }

                                            }

                                    }
                            }

                    }

            }
    }

    fun deleteProductFromDatabase(product: Product) {
        // Implement deletion from Realtime Database here
        // For example:
        FirebaseDatabase.getInstance().getReference("Admins").child(Utils.currentUser()!!)
            .child("AllProducts").child(product.productRandomId!!).removeValue()
            .addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference("Admins").child(Utils.currentUser()!!)
                    .child("ProductCategory/${product.productCategory}")
                    .child(product.productRandomId!!)
                    .removeValue()
                    .addOnSuccessListener {
                        FirebaseDatabase.getInstance().getReference("Admins")
                            .child(Utils.currentUser()!!)
                            .child("ProductType/${product.productType}")
                            .child(product.productRandomId!!)
                            .removeValue()
                            .addOnSuccessListener {

// product delete in All Product details node
                                FirebaseDatabase.getInstance().getReference("AllProductsDetails")
                                    .child("AllProducts").child(product.productRandomId!!)
                                    .removeValue()
                                    .addOnSuccessListener {
                                        FirebaseDatabase.getInstance()
                                            .getReference("AllProductsDetails")
                                            .child("ProductCategory/${product.productCategory}")
                                            .child(product.productRandomId!!)
                                            .removeValue()
                                            .addOnSuccessListener {
                                                FirebaseDatabase.getInstance()
                                                    .getReference("AllProductsDetails")
                                                    .child("ProductType/${product.productType}")
                                                    .child(product.productRandomId!!)
                                                    .removeValue()
                                                    .addOnSuccessListener {
                                                        _isProductDelete.value = true

                                                    }

                                            }

                                    }
                            }

                    }

            }

    }

    fun getAllOrders(): Flow<List<Orders>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child(Utils.currentUser()!!)
            .child("AdminOrders").orderByChild("OrderStatus")
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Orders>()
                for (orders in snapshot.children) {
                    val order = orders.getValue(Orders::class.java)

                    orderList.add(order!!)

                }
                trySend(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }

    fun orderedProducts(orderId: String): Flow<List<CartProducts>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child(Utils.currentUser()!!)
            .child("AdminOrders").child(orderId)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(Orders::class.java)
                trySend(order?.orderList!!)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }


    suspend fun fetchAllTheProducts(category: String): Flow<List<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins"!!).child(Utils.currentUser()!!)
            .child("AllProducts"!!)

        Log.d("db","db ${db}")

        if (!(db == null)){

            val eventListner = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val products = ArrayList<Product>()
                    for (product in snapshot.children) {
                        val prod = product.getValue(Product::class.java)
                        if (category == "All" || prod?.productCategory == category) {
                            products.add(prod!!)
                        }


                    }
                    // Sort products by timestamp in descending order
                   // products.sortByDescending { it?.timestamp }
                    trySend(products)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }

            db.addValueEventListener(eventListner)

            awaitClose { db.removeEventListener(eventListner) }
        }

        val eventListner = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children) {
                    val prod = product.getValue(Product::class.java)
                    if (category == "All" || prod?.productCategory == category) {
                        products.add(prod!!)
                    }


                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        db.addValueEventListener(eventListner)

        awaitClose { db.removeEventListener(eventListner) }
    }


    fun savingUpdateProducts(product: Product) {


        //  product Store in admin node
        FirebaseDatabase.getInstance().getReference("Admins").child(Utils.currentUser()!!)
            .child("AllProducts").child(product.productRandomId!!).setValue(product)
            .addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference("Admins").child(Utils.currentUser()!!)
                    .child("ProductCategory/${product.productCategory}")
                    .child(product.productRandomId!!)
                    .setValue(product)
                    .addOnSuccessListener {
                        FirebaseDatabase.getInstance().getReference("Admins")
                            .child(Utils.currentUser()!!)
                            .child("ProductType/${product.productType}")
                            .child(product.productRandomId!!)
                            .setValue(product)
                            .addOnSuccessListener {

// product store in All Product details node
                                FirebaseDatabase.getInstance().getReference("AllProductsDetails")
                                    .child("AllProducts").child(product.productRandomId!!)
                                    .setValue(product)
                                    .addOnSuccessListener {
                                        FirebaseDatabase.getInstance()
                                            .getReference("AllProductsDetails")
                                            .child("ProductCategory/${product.productCategory}")
                                            .child(product.productRandomId!!)
                                            .setValue(product)
                                            .addOnSuccessListener {
                                                FirebaseDatabase.getInstance()
                                                    .getReference("AllProductsDetails")
                                                    .child("ProductType/${product.productType}")
                                                    .child(product.productRandomId!!)
                                                    .setValue(product)
                                                    .addOnSuccessListener {
                                                        _isProductSaved.value = true

                                                    }

                                            }

                                    }
                            }

                    }

            }


//        productRef.child("AllProducts/${product.productRandomId}").setValue(product)
//        productRef.child("ProductCategory /${product.productCategory}")
//            .child(product.productRandomId!!)
//            .setValue(product)
//        // productRef.child("ProductCategory").child(product.productCategory!!).child(product.itemPushKey!!).setValue(product)
//        productRef.child("ProductType /${product.productType}").child(product.productRandomId!!)
//            .setValue(product)
    }

    fun updateOrderStatus(orderId: String, status: Int) {
        FirebaseDatabase.getInstance().getReference("Admins").child(Utils.currentUser()!!)
            .child("AdminOrders").child(orderId).child("orderStatus").setValue(status)

        FirebaseDatabase.getInstance().getReference("AllProductsDetails").child("AllOrders")
            .child(orderId).child("orderStatus").setValue(status)

        val user =
            FirebaseDatabase.getInstance().getReference("Admins").child(Utils.currentUser()!!)
                .child("AdminOrders").child(orderId).child("orderingUserUid").get()
        user.addOnCompleteListener { task ->
            val userUid = task.result.getValue(String::class.java)

            FirebaseDatabase.getInstance().getReference("AllUsers").child(userUid.toString())
                .child("UserOrders").child(orderId).child("orderStatus").setValue(status)

            // Log.d("UUU","userUid: ${userUid}")
        }


    }

    fun logOutUser() {
        FirebaseAuth.getInstance().signOut()
    }


    /// retrofit
    suspend fun sendNotification(orderId: String, title: String, message: String) {

        val getUser =
            FirebaseDatabase.getInstance().getReference("Admins").child(Utils.currentUser()!!)
                .child("AdminOrders").child(orderId).child("orderingUserUid").get()
        Log.d("NotiApp", "getUser : ${getUser}")
        getUser.addOnCompleteListener { task ->
            val userUid = task.result.getValue(String::class.java)
            Log.d("NotiApp", "userUid : ${userUid}")
            val getToken = FirebaseDatabase.getInstance().getReference("AllUsers").child(userUid!!)
                .child("UserInfo").child("userToken").get()
            Log.d("NotiApp", "get token : ${getToken}")
            getToken.addOnCompleteListener { task ->

                Log.d("NotiApp", "getToken : ${getToken}")

                val token = task.result.getValue(String::class.java)
                // val token="fBLSZ_cvThKvB6kMbWcBOl:APA91bH14uLy3sFLRzzuvk2ghBbZ1I3DAt11_zFvpBb-okxbp67mlsQxodDiLD7uxJf3yr9XTfZw7nc4zyJ5FZbUXfZcHnJqndNUWvoWDvkblZjc0xcb3dYhLrm7-aMpcg4ZqjVZ549_"
                Log.d("NotiApp", "asal token : ${token}")
                val notification = Notification(token, NotificationData(title, message))
                Log.d("NotiApp", "Notification : ${notification}")

                ApiUtilities.notificationApi.sendNotification(notification).enqueue(object :
                    Callback<Notification> {
                    override fun onResponse(
                        call: Call<Notification>,
                        response: Response<Notification>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("NotiApp", "Send Notification")


                        } else {
                            Log.e("NotiApp", "Failed to send notification: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<Notification>, t: Throwable) {
                        TODO("Not yet implemented")
                    }

                })

            }


        }


    }


}