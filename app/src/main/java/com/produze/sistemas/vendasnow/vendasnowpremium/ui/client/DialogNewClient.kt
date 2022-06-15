package com.produze.sistemas.vendasnow.vendasnowpremium.ui.client

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.database.DataSourceUser
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentNewClientBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Client
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.Mask
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelClient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class DialogNewClient(private var viewModel: ViewModelClient, private val client: Client,
                      val onClickAction: (Client) -> Unit)  : DialogFragment() {

    private lateinit var binding: FragmentNewClientBinding
    private var datasource: DataSourceUser? = null
    private lateinit var token: Token
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_new_client,
                container,
                false
        )

        datasource = context?.let { DataSourceUser(it) }
        token = datasource?.get()!!
        if (token.token == "") {

        }

        binding.editTextTelefone.addTextChangedListener(Mask.insert("(##)#####-####", binding.editTextTelefone))
        binding.editTextNome.setText(client.name)
        binding.editTextTelefone.setText(client.telephone)

        binding.btnConfirm.setOnClickListener {
            if (context?.let { it1 -> MainUtils.isOnline(it1) }!!) {
            if (binding.editTextNome.text.isEmpty()) {
                Toast.makeText(activity, R.string.hint_new_client,
                        Toast.LENGTH_SHORT).show()
            } else {
                client.name = binding.editTextNome.text.toString()
                client.telephone = binding.editTextTelefone.text.toString()

                if (client.id.isEmpty())
                { insert(client) } else {
                    binding.progressBar.visibility = View.VISIBLE
                    update(client)
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

    private fun insert(client: Client) {
    lifecycleScope.launch {
        viewModel.add(client, token.email).collectLatest { state ->
            when (state) {
                is State.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is State.Success -> {
                    binding.progressBar.visibility = View.GONE
                    dismiss()
                    onClickAction(client)
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

    private fun update(client: Client) {
        lifecycleScope.launch {
            viewModel.update(client).collectLatest {
//                binding.progressBar.visibility = View.GONE
                        dismiss()
                        onClickAction(client)
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