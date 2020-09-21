package kg.kloop.android.smsgateway

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore


class GatewayViewModel: ViewModel() {
    private val TAG: String = GatewayViewModel::class.java.simpleName
    var data = MutableLiveData<List<Message>>()

    init {
        val db = FirebaseFirestore.getInstance()
        db.collection("messages").get().addOnSuccessListener { result ->
            data.value = result.toObjects(Message::class.java)
        }
    }

}