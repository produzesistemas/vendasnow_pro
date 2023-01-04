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
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentAlertdialogDeleteBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Product
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

class ProductAlertDialogDelete (private var viewModel: ProductViewModel,
                                private val product: Product
)  : DialogFragment() {

    private lateinit var binding: FragmentAlertdialogDeleteBinding
    private var datasource: DataSourceUser? = null
    private lateinit var token: Token
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_alertdialog_delete,
                container,
                false
        )
        datasource = context?.let { DataSourceUser(it) }
        token = datasource?.get()!!
        if (token.token == "") {

        }
        binding.textViewMessage.text = product.name
        binding.btnConfirm.setOnClickListener {
            if (context?.let { it1 -> MainUtils.isOnline(it1) }!!) {
                delete(product)
                dismiss()
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

    private fun delete(product: Product) {
        lifecycleScope.launch {
            viewModel.delete(product, token.token)
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