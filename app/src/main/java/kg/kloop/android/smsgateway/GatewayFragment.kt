package kg.kloop.android.smsgateway

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kg.kloop.android.smsgateway.databinding.FragmentGatewayBinding

class GatewayFragment : Fragment() {
    private val TAG: String = GatewayViewModel::class.java.simpleName
    private val viewModel: GatewayViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentGatewayBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_gateway, container, false)
        viewModel.data.observe(viewLifecycleOwner, Observer {
            binding.gatewayTextView.text = it.toString()
        })

        binding.signOutButton.setOnClickListener {
            mainViewModel.signOut()
        }
        return binding.root
    }
}