package com.produze.sistemas.vendasnow.vendasnowpremium.ui.service

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentNewServiceBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Service
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MoneyTextWatcher
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class DialogNewService (private var viewModel: ViewModelService, private val service: Service,
                        val onClickAction: (Service) -> Unit)  : DialogFragment() {

    private lateinit var binding: FragmentNewServiceBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_new_service,
                container,
                false
        )
        val mLocale = Locale("pt", "BR")
        binding.editTextValue.addTextChangedListener(MoneyTextWatcher(binding.editTextValue, mLocale))
        binding.editTextDescription.setText(service.name)
        var strSaleValue = NumberFormat.getCurrencyInstance(mLocale).format(service.value)
        strSaleValue = strSaleValue.replace("R$", "")
        strSaleValue = strSaleValue.trim()
        binding.editTextValue.setText(strSaleValue)
        binding.btnConfirm.setOnClickListener {
            if ((binding.editTextDescription.text.isEmpty()) ||
                    (binding.editTextValue.text.equals("0,00"))){
                Toast.makeText(activity, R.string.hint_new_description,
                        Toast.LENGTH_SHORT).show()
            } else {
                service.name = binding.editTextDescription.text.toString()
                var vl: String = binding.editTextValue.text.toString().trim { it <= ' ' }
                vl = vl.trim { it <= ' ' }
                vl = vl.replace(".", "")
                vl = vl.replace(",", ".")
                vl = vl.replace("\\s".toRegex(), "")
                service.value = vl.toDouble()

                if (service.id.isEmpty())
                {
                    insert(service)
                } else {
                    binding.progressBar.visibility = View.VISIBLE
                    update(service)
                }
            }
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    private fun insert(service: Service) {
        lifecycleScope.launch {
            viewModel.add(service).collectLatest { state ->
                when (state) {
                    is State.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is State.Success -> {
                        binding.progressBar.visibility = View.GONE
                        dismiss()
                        onClickAction(service)
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

    private fun update(service: Service) {
        lifecycleScope.launch {
            viewModel.update(service).collectLatest {
                dismiss()
                onClickAction(service)
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