package com.example.adminblinkit.adminfragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.adminblinkit.R
import com.example.adminblinkit.activities.MainActivity
import com.example.adminblinkit.adapters.AdapterSelectedImage
import com.example.adminblinkit.databinding.FragmentAddProductsBinding
import com.example.adminblinkit.models.Product
import com.example.adminblinkit.utils.Constants
import com.example.adminblinkit.utils.Utils
import com.example.adminblinkit.viewmodels.AdminViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AddProductsFragment : Fragment() {

    private  val viewModel : AdminViewModel by viewModels()

    private lateinit var binding: FragmentAddProductsBinding

    private var imageUris: ArrayList<Uri> = arrayListOf()
    val selectImage = registerForActivityResult(ActivityResultContracts.GetMultipleContents()){listOfUri->
        val fiveImages = listOfUri.take(5)
        imageUris.clear()
        imageUris.addAll(fiveImages)

        binding.rcProductImage.adapter = AdapterSelectedImage(imageUris)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddProductsBinding.inflate(inflater , container, false)


       setAutoCompleteTextView()

        onImageSelectClicked()

        onAddBtnClick()

        return binding.root
    }

    private fun onAddBtnClick() {
        binding.addBtn.setOnClickListener{
            Utils.showDialog(requireContext(), "Uploading...")

            val productTitle = binding.productTitle.text.toString()
            val productQuantity = binding.productQuantity.text.toString()
            val productUnit = binding.productUnit.text.toString()
            val productPrice = binding.productPrice.text.toString()
            val productStock = binding.productNoStock.text.toString()
            val productCategory = binding.productCategoryList.text.toString()
            val productType = binding.productTypeList.text.toString()

            if (productTitle.isEmpty() || productQuantity.isEmpty() || productUnit.isEmpty() || productPrice.isEmpty() || productStock.isEmpty() || productCategory.isEmpty() || productType.isEmpty()){
                Utils.apply {
                    hideDialog()
                    showToast(requireContext(),"Empty Field are not allowed !!")
                }
            }
            else if (imageUris.isEmpty()){
                Utils.apply {
                    hideDialog()
                    showToast(requireContext(),"Please upload some Images !!")
                }
            }
            else{
                val product = Product(
                    productTitle=productTitle,
                    productQuantity = productQuantity.toInt(),
                    productUnit = productUnit,
                    productPrice = productPrice.toInt(),
                    productStock = productStock.toInt(),
                    productCategory = productCategory,
                    productType = productType,
                    itemCount = 0,
                    adminUid = Utils.currentUser(),
                    productRandomId = Utils.getRandomId(),
                    itemPushKey = Utils.itemPushKey()
                )
                saveImage(product)
            }
        }
    }

    private fun saveImage(product: Product) {
        viewModel.saveImageInDB(imageUris)
        lifecycleScope.launch {
            viewModel.isImagesUploaded.collect{
                if (it){
                    Utils.apply {
                        hideDialog()
                        showToast(requireContext(),"Image Saved")
                    }
                    getUrls(product)
                }
            }
        }
    }

    private fun getUrls(product: Product) {
        Utils.showToast(requireContext(),"Publishing product...")
        lifecycleScope.launch {
            viewModel.downloadsUrls.collect{
                val urls = it
                product.productImageUris = urls
                saveProduct(product)
            }
        }
    }

    private fun saveProduct(product: Product) {
        viewModel.saveProduct(product)
        lifecycleScope.launch {
            viewModel.isProductSaved.collect{
                if (it){
                    Utils.hideDialog()
                    Utils.showToast(requireContext(),"Your product is live")
                }
            }
        }
    }

    private fun onImageSelectClicked() {
        binding.galleryBtn.setOnClickListener{
            selectImage.launch("image/*")
        }
    }



    private fun setAutoCompleteTextView() {
        val units = ArrayAdapter(requireContext(),R.layout.show_list,Constants.allUnitsOfProducts)
        val productCategory = ArrayAdapter(requireContext(),R.layout.show_list,Constants.allProductsCategory)
        val productType = ArrayAdapter(requireContext(),R.layout.show_list,Constants.allProductType)

        binding.apply {
            productUnit.setAdapter(units)
            productCategoryList.setAdapter(productCategory)
            productTypeList.setAdapter(productType)

        }
    }

}