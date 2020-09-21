package kg.kloop.android.smsgateway

import android.app.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kg.kloop.android.smsgateway.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val TAG: String = MainFragment::class.java.simpleName
    private val RC_SIGN_IN: Int = 200
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentMainBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_main,
            container,
            false)

        viewModel.user.value = FirebaseAuth.getInstance().currentUser
        viewModel.user.observe(viewLifecycleOwner, Observer { firebaseUser ->
            if (firebaseUser == null) {
                signIn()
            } else {
                requireView().findNavController().navigate(R.id.action_mainFragment_to_gatewayFragment)
            }
        })
        return binding.root
    }

    private fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                viewModel.user.value = FirebaseAuth.getInstance().currentUser
            } else {
                Toast.makeText(activity, "Fail", Toast.LENGTH_SHORT).show()
            }
        }
    }

}