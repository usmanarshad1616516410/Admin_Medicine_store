package com.example.adminblinkit.adminfragment


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.adminblinkit.R
import com.example.adminblinkit.activities.AuthActivity
import com.example.adminblinkit.adapters.AdapterProduct
import com.example.adminblinkit.adapters.CategoriesAdapter
import com.example.adminblinkit.databinding.EditProductLayoutBinding
import com.example.adminblinkit.databinding.FragmentHomeBinding
import com.example.adminblinkit.models.Category
import com.example.adminblinkit.models.Product
import com.example.adminblinkit.utils.Constants
import com.example.adminblinkit.utils.Utils
import com.example.adminblinkit.viewmodels.AdminViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private val viewModel : AdminViewModel by viewModels()

    private lateinit var binding: FragmentHomeBinding

    private lateinit var adapterProduct: AdapterProduct


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater , container, false)

        binding.shimmerViewContainer.visibility = View.VISIBLE


        setCategories()

        searchProducts()

        getAllTheProducts("All")

        onLogOut()

        return binding.root
    }

    private fun onLogOut() {
        binding.tbHome.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.menuLogout->{
                    logOutUser()
                    true
                }
                else->{false}
            }
        }
    }

    private fun logOutUser() {
        val builder= androidx.appcompat.app.AlertDialog.Builder(requireContext())
        val alertDialog=builder.create()
        builder.setTitle("Log out")
            .setMessage("Do you want to log out ?")
            .setPositiveButton("Yes"){_,_->
                viewModel.logOutUser()
                startActivity(Intent(requireContext(), AuthActivity::class.java))
                requireActivity().finish()
            }
            .setNegativeButton("No"){_,_->
                alertDialog.dismiss()
            }
            .show()
            .setCancelable(false)
    }

    private fun searchProducts() {

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapterProduct.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun getAllTheProducts(category: String) {
        lifecycleScope.launch {
            viewModel.fetchAllTheProducts(category).collect { products ->
                if (products.isEmpty()) {
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                } else {
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }

                adapterProduct = AdapterProduct(::onEditButtonClicked,::onDeleteButtonClicked)
                adapterProduct.submitList(products)
                binding.rvProducts.adapter = adapterProduct
                binding.shimmerViewContainer.visibility = View.GONE
            }
        }
    }

    private fun onEditButtonClicked(product: Product){
val editProduct = EditProductLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        editProduct.apply {
            editProductTitle.setText(product.productTitle)
            editProductQuantity.setText(product.productQuantity.toString())
            editProductUnit.setText(product.productUnit)
            editProductPrice.setText(product.productPrice.toString())
            editProductNoStock.setText(product.productStock.toString())
            editProductCategoryList.setText(product.productCategory)
            editProductTypeList.setText(product.productType)
        }
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(editProduct.root)
            .create()
        alertDialog.show()

        editProduct.editBtn.setOnClickListener{
            editProduct.apply {
                editProductTitle.isEnabled=true
                editProductQuantity.isEnabled=true
                editProductUnit.isEnabled=true
                editProductPrice.isEnabled=true
                editProductNoStock.isEnabled=true
//                editProductCategoryList.isEnabled=true
//                editProductTypeList.isEnabled=true
            }
        }

        setAutoCompleteTextView(editProduct)

        editProduct.saveBtn.setOnClickListener{

            lifecycleScope.launch {

                product.productTitle = editProduct.editProductTitle.text.toString()
                product.productQuantity = editProduct.editProductQuantity.text.toString().toInt()
                product.productUnit = editProduct.editProductUnit.text.toString()
                product.productPrice = editProduct.editProductPrice.text.toString().toInt()
                product.productStock = editProduct.editProductNoStock.text.toString().toInt()
                product.productCategory = editProduct.editProductCategoryList.text.toString()
                product.productType = editProduct.editProductTypeList.text.toString()

                viewModel.savingUpdateProducts(product)
            }

            Utils.showToast(requireContext(),"Saved changes!")
            alertDialog.dismiss()
    }
    }

    private fun setAutoCompleteTextView(editProduct: EditProductLayoutBinding) {
        val units = ArrayAdapter(requireContext(), R.layout.show_list,Constants.allUnitsOfProducts)
        val productCategory = ArrayAdapter(requireContext(), R.layout.show_list,Constants.allProductsCategory)
        val productType = ArrayAdapter(requireContext(), R.layout.show_list,Constants.allProductType)

        editProduct.apply {
            editProductUnit.setAdapter(units)
            editProductCategoryList.setAdapter(productCategory)
            editProductTypeList.setAdapter(productType)

        }
    }

    private fun setCategories() {
        val categoryList = ArrayList<Category>()

        for(i in 0 until Constants.allProductsCategoryIcon.size){
            categoryList.add(Category(Constants.allProductsCategory[i], Constants.allProductsCategoryIcon[i]))
        }

        binding.rvCategories.adapter = CategoriesAdapter(categoryList,::onCategoryClicked)
    }

    private fun onCategoryClicked(category: Category){

        getAllTheProducts(category.category)

    }

    private fun onDeleteButtonClicked(product: Product) {

        val builder= androidx.appcompat.app.AlertDialog.Builder(requireContext())
        val alertDialog=builder.create()
        builder.setTitle("Delete ")
            .setMessage("Do you want to delete this Product ?")
            .setPositiveButton("Yes"){_,_->

                Utils.showDialog(requireContext(),"Deleting Product")

                // Delete from Storage
                viewModel.deleteImageInDB(product.productImageUris)
                // Delete from Realtime Database
                viewModel.deleteProductFromDatabase(product)

                lifecycleScope.launch {
                    viewModel.isImagesDelete.collect{
                        if (true){
                            viewModel.isProductDelete.collect{
                                if (true){
                                   Utils.hideDialog()
                                }
                                else{
                                    Utils.hideDialog()
                                    Utils.showToast(requireContext(),"Something went wrong")
                                }
                            }
                        }
                        else{
                            Utils.hideDialog()
                            Utils.showToast(requireContext(),"Something went wrong")
                        }
                    }

                }


                // Notify the adapter
                adapterProduct.notifyDataSetChanged()

            }
            .setNegativeButton("No"){_,_->
                alertDialog.dismiss()
            }
            .show()
            .setCancelable(false)


    }



}