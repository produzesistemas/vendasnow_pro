package com.produze.sistemas.vendasnow.vendasnowpremium.ui.product

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.database.DataSourceUser
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentNewProductBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Product
import com.produze.sistemas.vendasnow.vendasnowpremium.model.ResponseBody
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MoneyTextWatcher
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*


class DialogNewProduct(private var viewModel: ProductViewModel,
                       private val product: Product)  : DialogFragment() {
    private lateinit var binding: FragmentNewProductBinding
    private var datasource: DataSourceUser? = null
    private lateinit var token: Token
    var responseBody = ResponseBody()
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_new_product,
                container,
                false
        )
        val mLocale = Locale("pt", "BR")
        datasource = context?.let { DataSourceUser(it) }
        token = datasource?.get()!!
        if (token.token == "") {

        }
        binding.editTextValue.addTextChangedListener(MoneyTextWatcher(binding.editTextValue, mLocale))
        binding.editTextCostValue.addTextChangedListener(MoneyTextWatcher(binding.editTextCostValue, mLocale))
        binding.editTextDescription.setText(product.name)
        var strSaleValue = NumberFormat.getCurrencyInstance(mLocale).format(product.value)
        strSaleValue = strSaleValue.replace("R$", "")
        strSaleValue = strSaleValue.trim()
        binding.editTextValue.setText(strSaleValue)

        var strCostValue = NumberFormat.getCurrencyInstance(mLocale).format(product.costValue)
        strCostValue = strCostValue.replace("R$", "")
        strCostValue = strCostValue.trim()

        binding.editTextCostValue.setText(strCostValue)

        binding.cardViewConfirm.setOnClickListener {
            if (context?.let { it1 -> MainUtils.isOnline(it1) }!!) {
            if ((binding.editTextDescription.text.isEmpty()) ||
                    (binding.editTextValue.text.equals("0,00"))){
                Toast.makeText(activity, R.string.hint_new_description,
                        Toast.LENGTH_SHORT).show()
            } else {
                product.name = binding.editTextDescription.text.toString()
                var vl: String = binding.editTextValue.text.toString().trim { it <= ' ' }
                vl = vl.trim { it <= ' ' }
                vl = vl.replace(".", "")
                vl = vl.replace(",", ".")
                vl = vl.replace("\\s".toRegex(), "")
                product.value = vl.toDouble()

                var vlCost: String = binding.editTextCostValue.text.toString().trim { it <= ' ' }
                vlCost = vlCost.trim { it <= ' ' }
                vlCost = vlCost.replace(".", "")
                vlCost = vlCost.replace(",", ".")
                vlCost = vlCost.replace("\\s".toRegex(), "")
                product.costValue = vlCost.toDouble()
                binding.imageViewLogin.visibility = View.GONE
                binding.textViewLogin.visibility = View.GONE
                    save(product)
            }
            } else {
                Toast.makeText(context, R.string.validation_connection,
                    Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loading.observe(this) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.complete.observe(this) {
            if (it) {
                dismiss()
            }
        }
        return binding.root
    }

    private fun save(product: Product) {
        lifecycleScope.launch {
            viewModel.save(product, token.token)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        viewModel.setCompleteFalse()
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