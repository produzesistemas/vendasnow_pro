package com.produze.sistemas.vendasnow.vendasnowpremium.ui.sale

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentNewSaleServiceBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.SaleService
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MoneyTextWatcher
import java.util.*

class DialogSaleService(val onClickAction: (SaleService) -> Unit)  : DialogFragment() {

    private lateinit var binding: FragmentNewSaleServiceBinding
    private lateinit var saleService: SaleService

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_new_sale_service,
                container,
                false
        )

        val mLocale = Locale("pt", "BR")
        binding.editTextValueSale.addTextChangedListener(MoneyTextWatcher(binding.editTextValueSale, mLocale))
        binding.btnConfirm.setOnClickListener {
            if ((binding.editTextValueSale.text.isEmpty()) ||
                (binding.editTextValueSale.text.equals("0"))){
                Toast.makeText(activity, R.string.validation_quantity_and_value,
                    Toast.LENGTH_SHORT).show()
            } else {
                saleService = SaleService()
                saleService.description = binding.editTextDescription.text.toString()
                var vl: String = binding.editTextValueSale.text.toString().trim { it <= ' ' }
                vl = vl.trim { it <= ' ' }
                vl = vl.replace(".", "")
                vl = vl.replace(",", ".")
                vl = vl.replace("\\s".toRegex(), "")
                saleService.valueSale = vl.toDouble()
                saleService.quantity = 1.00

                dismiss()
                onClickAction(saleService)
            }
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
//        val adapter: ArrayAdapter<Service>? = context?.let { ArrayAdapter<Service>(it, android.R.layout.simple_spinner_dropdown_item, lst) }
//        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.spinnerService.adapter = adapter
//        binding.spinnerService.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
//                service = Service()
//                service = binding.spinnerService.selectedItem as Service
//                var strSaleValue = NumberFormat.getCurrencyInstance(mLocale).format(service.value)
//                strSaleValue = strSaleValue.replace("R$", "")
//                strSaleValue = strSaleValue.trim()
//                binding.editTextValueSale.setText(strSaleValue)
//            }
//
//            override fun onNothingSelected(parentView: AdapterView<*>?) {
//                // your code here
//            }
//        }
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