package com.example.signify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.example.signify.databinding.FragmentManagerOrdersBinding
import com.google.firebase.firestore.FieldPath
import java.text.SimpleDateFormat
import java.util.*

class ManagerOrdersFragment : Fragment() {

    private lateinit var binding: FragmentManagerOrdersBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentManagerOrdersBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentManagerId = "8qj7Z0umSLdgY8vJBXqArEjyPZV2"
        if (currentManagerId != null) {
            firestore.collection("client_to_manager")
                .document(currentManagerId)
                .get()
                .addOnSuccessListener { document ->
                    val clientIds = document["clients_id"] as List<String>
                    getOrderDetailsForClients(clientIds)
                }
                .addOnFailureListener { exception ->
                    // handle error
                }
        }

        return binding.root
    }

    private fun getOrderDetailsForClients(clientIds: List<String>) {
        val orders = mutableListOf<Order>()
        for (clientId in clientIds) {
            firestore.collection("authorization")
                .document(clientId)
                .get()
                .addOnSuccessListener { document ->
                    val orderIds = document["orders"] as List<String>
                    getOrderDetails(orderIds, orders)
                }
                .addOnFailureListener { exception ->
                    // handle error
                }
        }
        displayOrders(orders)
    }

    private fun getOrderDetails(orderIds: List<String>, orders: MutableList<Order>) {
        val ordersRef = firestore.collection("orders")
        val query = ordersRef.whereIn(FieldPath.documentId(), orderIds)
        query.get()
            .addOnSuccessListener { querySnapshot ->
                val orders = mutableListOf<Order>()
                for (document in querySnapshot.documents) {
                    val billboardId = document.getString("billboard_id") ?: ""
                    val ordersStatusMap = document.get("order_status") as? HashMap<String, Any>
                    var newestDate: Date? = null
                    var newestStatus: String? = null

                    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    for ((dateStr, status) in ordersStatusMap!!) {
                        val date = dateFormatter.parse(dateStr.toString())
                        if (newestDate == null || date.after(newestDate)) {
                            newestDate = date
                            newestStatus = status.toString()
                        }
                    }
                    val billboardRef = firestore.collection("billboards").document(billboardId)
                    billboardRef.get().addOnSuccessListener { billboardSnapshot ->
                        val location = billboardSnapshot.getString("location") ?: ""
                        val orderId = document.id
                        val order = Order(billboardId, newestStatus!!, location, orderId)
                        orders.add(order)
                        displayOrders(orders)
                    }
                }

                displayOrders(orders)
            }
            .addOnFailureListener { exception ->
                // handle error
            }
    }

    private fun displayOrders(orders: List<Order>) {
        val adapter = OrdersAdapter(orders)
        binding.ordersRecyclerView.adapter = adapter
        binding.ordersRecyclerView.layoutManager = LinearLayoutManager(context)
    }
}
