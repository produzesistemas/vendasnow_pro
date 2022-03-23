package com.produze.sistemas.vendasnow.vendasnowpremium.ui.sale

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentNewSaleProductBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Product
import com.produze.sistemas.vendasnow.vendasnowpremium.model.SaleProduct
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MoneyTextWatcher
import java.text.NumberFormat
import java.util.*

class DialogSaleProduct(private var lst: List<Product>,
                        val onClickAction: (SaleProduct) -> Unit)  : DialogFragment() {

    private lateinit var binding: FragmentNewSaleProductBinding
    private lateinit var saleProduct: SaleProduct
    private lateinit var product: Product

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_new_sale_product,
                container,
                false
        )
        val mLocale = Locale("pt", "BR")
        binding.editTextValueSale.addTextChangedListener(MoneyTextWatcher(binding.editTextValueSale, mLocale))
        binding.editTextQuantity.addTextChangedListener(MoneyTextWatcher(binding.editTextQuantity, mLocale))
        binding.btnConfirm.setOnClickListener {
            if ((binding.editTextQuantity.text.isEmpty()) ||
                    (binding.editTextQuantity.text.equals("0")) ||
                (binding.editTextValueSale.text.isEmpty()) ||
                (binding.editTextValueSale.text.equals("0"))
                    ){
                Toast.makeText(activity, R.string.validation_quantity_and_value,
                        Toast.LENGTH_SHORT).show()
            } else {
                saleProduct = SaleProduct()
                var vl: String = binding.editTextValueSale.text.toString().trim { it <= ' ' }
                vl = vl.trim { it <= ' ' }
                vl = vl.replace(".", "")
                vl = vl.replace(",", ".")
                vl = vl.replace("\\s".toRegex(), "")
                saleProduct.valueSale = vl.toDouble()

                var vlq: String = binding.editTextQuantity.text.toString().trim { it <= ' ' }
                vlq = vlq.trim { it <= ' ' }
                vlq = vlq.replace(".", "")
                vlq = vlq.replace(",", ".")
                vlq = vlq.replace("\\s".toRegex(), "")
                saleProduct.quantity = vlq.toDouble()
                saleProduct.product = product
                dismiss()
                onClickAction(saleProduct)
            }
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        val adapter: ArrayAdapter<Product>? = context?.let { ArrayAdapter<Product>(it, android.R.layout.simple_spinner_dropdown_item, lst) }
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerProduct.adapter = adapter
        binding.spinnerProduct.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                product = Product()
                product = binding.spinnerProduct.selectedItem as Product
                var strSaleValue = NumberFormat.getCurrencyInstance(mLocale).format(product.value)
                strSaleValue = strSaleValue.replace("R$", "")
                strSaleValue = strSaleValue.trim()
                binding.editTextValueSale.setText(strSaleValue)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onResume() {
        // Get existing layout params for the window
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
        dialog!!.getWindow()?.setBackgroundDrawableResource(R.drawable.custom_dialog_fragment);
        // Call super onResume after sizing
        super.onResume()
    }

}