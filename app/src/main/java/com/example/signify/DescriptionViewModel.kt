package com.example.signify

import android.graphics.Paint
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class DescriptionViewModel : ViewModel() {
    var billboardId: String = ""
    var orderId: String = ""
    var price: Double = 0.0

    fun getBillboardAvailability(billboardId: String): LiveData<HashMap<String, Boolean>> {
        val db = FirebaseFirestore.getInstance()
        val billboardsRef = db.collection("billboards")
        val availability = MutableLiveData<HashMap<String, Boolean>>()

        billboardsRef.document(billboardId).get().addOnSuccessListener { billboardSnapshot ->
            val billboardData = billboardSnapshot.data ?: return@addOnSuccessListener
            availability.value = ((billboardData["NotAvailable"] as? HashMap<String, Boolean> ?: emptyMap()) as HashMap<String, Boolean>?)
        }

        return availability
    }

    fun getPrice(billboardId: String): LiveData<Double> {
        val db = FirebaseFirestore.getInstance()
        val billboardsRef = db.collection("billboards")
        val price = MutableLiveData<Double>()

        billboardsRef.document(billboardId).get().addOnSuccessListener { billboardSnapshot ->
            price.value = billboardSnapshot.getDouble("price")
        }

        return price
    }

}
