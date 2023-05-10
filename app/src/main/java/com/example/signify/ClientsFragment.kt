package com.example.signify

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.signify.databinding.FragmentClientsBinding
import com.example.signify.databinding.FragmentOrdersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ClientsFragment : Fragment() {

    private lateinit var binding: FragmentClientsBinding
    //private lateinit var firestore: FirebaseFirestore
    //private lateinit var auth: FirebaseAuth

    private lateinit var recyclerView: RecyclerView
    private var db = Firebase.firestore
    private lateinit var clients: ArrayList<Clients>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentClientsBinding.inflate(inflater, container, false)
        binding.clientRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.add.setOnClickListener{
            val fragment = AddClientFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }

        clients = arrayListOf()
        db = FirebaseFirestore.getInstance()

        // trying to get sum of payment for each client
        // Create a map to store the client IDs and their total cost
        /*val clientTotalCosts = mutableMapOf<String, Double>()

        // Query the orders collection to get all documents
        db.collection("orders").get()
            .addOnSuccessListener { documents ->
                // Iterate over each document in the orders collection
                for (document in documents) {
                    // Get the client ID and total cost for the current order
                    val clientID = document.getString("clientID")
                    val totalCost = document.getDouble("totalCost") ?: 0.0

                    // If the client ID is not null, update the total cost for that client
                    if (clientID != null) {
                        if (clientTotalCosts.containsKey(clientID)) {
                            clientTotalCosts[clientID] = clientTotalCosts[clientID]!! + totalCost
                        } else {
                            clientTotalCosts[clientID] = totalCost
                        }
                    }
                }
            }
*/

        // query the "Clients" collection to get a list of Client objects:
        db.collection("clients").get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (data in it.documents){
                        val attachedManager = data.getString("AttachedManager")
                        // !!!!! replace second comparable to dynamic form  !!!!!
                        if (attachedManager == "8qj7Z0umSLdgY8vJBXqArEjyPZV2") {
                            val name = data.getString("Name") ?: "Elon"
                            val email = data.getString("Email") ?: "elon.musk@space.x"
                            //var amount: Double = 0.0
                            //if (clientTotalCosts.containsKey(data.getString("UID"))){
                            //    amount = clientTotalCosts[data.getString("UID")]!!
                            //}
                            val client = Clients(name, email)
                            clients.add(client)
                        }
                    }
                    // display the list of clients in the RecyclerView:
                    recyclerView = binding.clientRecyclerView
                    recyclerView.adapter = ClientAdapter(clients)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }

        return binding.root
    }
}