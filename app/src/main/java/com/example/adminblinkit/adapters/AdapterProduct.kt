package com.example.adminblinkit.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.example.adminblinkit.databinding.ItemViewProductBinding
import com.example.adminblinkit.models.Product
class AdapterProduct(
    val onEditButtonClicked: (Product) -> Unit,
    val onDeleteButtonClicked: (Product) -> Unit
) : RecyclerView.Adapter<AdapterProduct.ProductViewHolder>(), Filterable {

    private val originalList: MutableList<Product> = mutableListOf()

    class ProductViewHolder(val binding: ItemViewProductBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffUtil = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.productRandomId == newItem.productRandomId
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemViewProductBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.binding.apply {
            // Populate your views here

            val imageList = ArrayList<SlideModel>()
            val productImage = product.productImageUris

            for (i in 0 until productImage?.size!!) {
                imageList.add(SlideModel(product.productImageUris!![i].toString()))

            }

            ivImageSlider.setImageList(imageList)

            tvProductTitle.text = product.productTitle
            val quantity = product.productQuantity.toString() + product.productUnit
            tvProductquantity.text = quantity

            tvProductPrice.text = "Rs" + product.productPrice

            btnDelete.setOnClickListener {
                val productToDelete = differ.currentList[position]
                onDeleteButtonClicked(productToDelete)
            }

        }


        holder.itemView.setOnClickListener {
            onEditButtonClicked(product)
        }

//        fun deleteProduct(position: Int) {
//            val productToDelete = differ.currentList[position]
//            onDeleteButtonClicked(productToDelete)
//        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<Product>()
                val query = constraint.toString().trim().toLowerCase()
                if (query.isEmpty()) {
                    filteredList.addAll(originalList)
                } else {
                    originalList.forEach {
                        if (
                            it.productTitle!!.toLowerCase().contains(query) ||
                            it.productCategory!!.toLowerCase().contains(query) ||
                            it.productType!!.toLowerCase().contains(query) ||
                            it.productPrice.toString().toLowerCase().contains(query) ||
                            it.productUnit!!.toLowerCase().contains(query) ||
                            it.productQuantity.toString().toLowerCase().contains(query)
                            ) {
                            filteredList.add(it)
                        }
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                differ.submitList(results?.values as MutableList<Product>?)
            }
        }
    }

    fun submitList(list: List<Product>) {
        originalList.clear()
        originalList.addAll(list)
        differ.submitList(list)
    }
}
