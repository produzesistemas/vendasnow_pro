package com.produze.sistemas.vendasnow.vendasnowpremium.ui.product

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentNewProductBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Product
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MoneyTextWatcher
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelProduct
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*


class DialogNewProduct(private var viewModel: ViewModelProduct, private val product: Product,
                       val onClickAction: (Product) -> Unit)  : DialogFragment() {

    private lateinit var binding: FragmentNewProductBinding

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

        binding.btnConfirm.setOnClickListener {
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

                if (product.id.isEmpty())
                {
                    insert(product)
                } else {
                    binding.progressBar.visibility = View.VISIBLE
                    update(product)
                }
            }
            } else {
                Toast.makeText(context, R.string.validation_connection,
                    Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    private fun insert(product: Product) {
        lifecycleScope.launch {
            viewModel.add(product).collectLatest { state ->
                when (state) {
                    is State.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is State.Success -> {
                        binding.progressBar.visibility = View.GONE
                        dismiss()
                        onClickAction(product)
                    }

                    is State.Failed -> {
                        binding.progressBar.visibility = View.GONE
                        dismiss()
                        Toast.makeText(activity, state.message,
                                Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun update(product: Product) {
        lifecycleScope.launch {
            viewModel.update(product).collectLatest {
                dismiss()
                onClickAction(product)
            }
        }
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