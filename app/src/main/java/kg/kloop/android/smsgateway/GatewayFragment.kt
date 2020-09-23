package kg.kloop.android.smsgateway

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kg.kloop.android.smsgateway.databinding.FragmentGatewayBinding


class GatewayFragment : Fragment() {
    private val PERMISSIONS_REQUEST_READ_SMS: Int = 999
    private val TAG: String = GatewayFragment::class.java.simpleName
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    Log.i(TAG, "permission is granted")
                } else {
                    Log.i(TAG, "no permission")
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentGatewayBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_gateway, container, false)

        mainViewModel.user.observe(viewLifecycleOwner, Observer { firebaseUser ->
            Log.i(TAG, "firebaseUser: ${firebaseUser?.displayName}")
            binding.hello = "${getString(R.string.hello)}, ${firebaseUser?.displayName}\n" +
                    "${getString(R.string.app_is_working)}"
            if (firebaseUser == null) {
                Log.i(TAG, "navigate to main fragment")
                findNavController().navigate(R.id.action_gatewayFragment_to_mainFragment)
            }
        })
        checkForSmsPermission()
        return binding.root
    }

    private fun checkForSmsPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_SMS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS) -> {
            }
            else -> {
                Log.i(TAG, "request permission")
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_SMS
                )
            }
        }
    }

}