package kg.kloop.android.smsgateway

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kg.kloop.android.smsgateway.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val TAG: String = MainFragment::class.java.simpleName
    private val RC_SIGN_IN: Int = 200
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentMainBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main,
            container,
            false
        )

        viewModel.user.value = FirebaseAuth.getInstance().currentUser
        if (viewModel.user.value != null) {
            findNavController().navigate(R.id.action_mainFragment_to_gatewayFragment)
        }
        binding.signInButton.setOnClickListener {
            if (viewModel.user.value == null) {
                signIn()
            }
        }
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
                findNavController().navigate(R.id.action_mainFragment_to_gatewayFragment)
            } else {
                Log.i(TAG, "Error: ${response?.error?.stackTrace}")
                Toast.makeText(activity, getString(R.string.login_is_required), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}