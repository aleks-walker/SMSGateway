package kg.kloop.android.smsgateway

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainViewModel : ViewModel() {
    val user = MutableLiveData<FirebaseUser?>()

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        user.value = null
    }
}