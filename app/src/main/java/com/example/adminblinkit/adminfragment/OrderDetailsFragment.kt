package com.example.adminblinkit.adminfragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.adminblinkit.R
import com.example.adminblinkit.adapters.AdapterCartProducts
import com.example.adminblinkit.databinding.FragmentOrderDetailsBinding
import com.example.adminblinkit.utils.Utils
import com.example.adminblinkit.viewmodels.AdminViewModel
import kotlinx.coroutines.launch

class OrderDetailsFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailsBinding

    private val viewModel: AdminViewModel by viewModels()

    private lateinit var adapterCartProducts: AdapterCartProducts




    private var status = 0
    private var currentStatus = 1
    private var orderId = ""

    private lateinit var orderingUserUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)


        onBackButtonClicked()

        getValues()

        settingStatus(status)

        onChangeStatusButtonClicked()

        getOrderedProducts()

        return binding.root
    }

    private fun onChangeStatusButtonClicked() {
        binding.btnChangeStatus.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)
            popupMenu.show()

            popupMenu.setOnMenuItemClickListener { menu ->
                when (menu.itemId) {

                    R.id.menuReceived -> {
                        currentStatus = 1
                        if (currentStatus > status) {
                            status=1
                            settingStatus(1)
                            viewModel.updateOrderStatus(orderId, 1)
                            lifecycleScope.launch { viewModel.sendNotification(orderId,"Received","Your order is received...") }
                        }
                        else{
                            Utils.showToast(requireContext(),"Orders is already received...")
                        }
                        true
                    }

                    R.id.menuDispatched -> {
                        currentStatus = 2
                        if (currentStatus > status) {
                            status=2
                            settingStatus(2)
                            viewModel.updateOrderStatus(orderId, 2)
                            lifecycleScope.launch { viewModel.sendNotification(orderId,"Dispatched","Your order is dispatched...") }
                        }
                        else{
                            Utils.showToast(requireContext(),"Orders is already dispatched...")
                        }
                        true
                    }

                    R.id.menuDelivered -> {
                        currentStatus = 3
                        if (currentStatus > status) {
                            status=3
                            settingStatus(3)
                            viewModel.updateOrderStatus(orderId, 3)
                            lifecycleScope.launch { viewModel.sendNotification(orderId,"Delivered","Your order is delivered...") }
                        }
                        else{
                            Utils.showToast(requireContext(),"Orders is already delivered...")
                        }
                        true
                    }

                    else -> {
                        false
                    }
                }

            }
        }
    }

    private fun getOrderedProducts() {
        lifecycleScope.launch {
            viewModel.orderedProducts(orderId).collect { cartList ->
                adapterCartProducts = AdapterCartProducts()
                binding.rvProductItems.adapter = adapterCartProducts
                adapterCartProducts.differ.submitList(cartList)
            }
        }
    }

    private fun settingStatus(status: Int) {


        when (status) {
            0 -> {
                binding.iv1.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.tv1.setTextColor(ContextCompat.getColor(requireContext(),R.color.blue))
            }

            1 -> {
                binding.iv1.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.iv2.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.view1.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)

                binding.tv1.setTextColor(ContextCompat.getColor(requireContext(),R.color.blue))
                binding.tv2.setTextColor(ContextCompat.getColor(requireContext(),R.color.blue))
            }

            2 -> {
                binding.iv1.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.iv2.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.view1.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.iv3.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.view2.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)

                binding.tv1.setTextColor(ContextCompat.getColor(requireContext(),R.color.blue))
                binding.tv2.setTextColor(ContextCompat.getColor(requireContext(),R.color.blue))
                binding.tv3.setTextColor(ContextCompat.getColor(requireContext(),R.color.blue))
            }

            3 -> {
                binding.iv1.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.iv2.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.view1.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.iv3.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.view2.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.view3.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.iv4.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)

                binding.tv1.setTextColor(ContextCompat.getColor(requireContext(),R.color.blue))
                binding.tv2.setTextColor(ContextCompat.getColor(requireContext(),R.color.blue))
                binding.tv3.setTextColor(ContextCompat.getColor(requireContext(),R.color.blue))
                binding.tv4.setTextColor(ContextCompat.getColor(requireContext(),R.color.blue))
            }
        }
    }

    private fun getValues() {
        val bundle = arguments
        status = bundle?.getInt("status")!!
        orderId = bundle.getString("orderId").toString()
        orderingUserUid=bundle.getString("orderingUserUid").toString()


        binding.tvShowAddress.text = bundle.getString("userAddress")
    }

    private fun onBackButtonClicked() {
        binding.tbOrderDetail.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_orderDetailsFragment_to_orderFragment)
        }


    }
}