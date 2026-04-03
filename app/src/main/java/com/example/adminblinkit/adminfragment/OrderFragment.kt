package com.example.adminblinkit.adminfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.adminblinkit.R
import com.example.adminblinkit.adapters.AdapterOrders
import com.example.adminblinkit.databinding.FragmentOrderBinding
import com.example.adminblinkit.models.OrderedItems
import com.example.adminblinkit.utils.Utils
import com.example.adminblinkit.viewmodels.AdminViewModel
import kotlinx.coroutines.launch

class OrderFragment : Fragment() {

    private lateinit var binding: FragmentOrderBinding

    private val viewModel: AdminViewModel by viewModels()

    private lateinit var adapterOrders: AdapterOrders

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderBinding.inflate(inflater , container, false)


        getAllOrders()

        return binding.root
    }

    private fun getAllOrders() {
        lifecycleScope.launch {
            viewModel.getAllOrders().collect { orderList ->

                if (orderList.isEmpty()){
                    binding.rvOrders.visibility=View.GONE
                    binding.tvText.visibility=View.VISIBLE
                }
                else{
                    binding.rvOrders.visibility=View.VISIBLE
                    binding.tvText.visibility=View.GONE
                }


                if (orderList.isNotEmpty()) {
                    val orderedList = ArrayList<OrderedItems>()
                    for (orders in orderList) {

                        val title = StringBuilder()
                        var totalPrice = 0

                        for (products in orders.orderList!!) {
                            val price = products.productPrice?.substring(2)?.toInt()
                            val itemCount = products.productCount!!
                            totalPrice = totalPrice + ((price?.times(itemCount))!!)

                            title.append("${products.productCategory}, ")
                        }
                        val orderedItems = OrderedItems(
                            orders.orderId,
                            orders.orderDate,
                            orders.orderStatus,
                            title.toString(),
                            totalPrice,
                            orders.userAddress,
                        )
                        orderedList.add(orderedItems)
                    }

                    adapterOrders= AdapterOrders(requireContext(),::onOrderItemViewClicked)
                    binding.rvOrders.adapter=adapterOrders
                    adapterOrders.differ.submitList(orderedList)
                }
                binding.shimmerViewContainer.visibility = View.GONE
            }
        }

    }

    fun onOrderItemViewClicked(orderedItems: OrderedItems){

        val bundle=Bundle()
        bundle.putInt("status",orderedItems.itemStatus!!)
        bundle.putString("orderId",orderedItems.orderId)
        bundle.putString("userAddress",orderedItems.userAddress)
        bundle.putString("orderingUserUid",orderedItems.orderingUserUid)


        findNavController().navigate(R.id.action_orderFragment_to_orderDetailsFragment,bundle)
    }



}