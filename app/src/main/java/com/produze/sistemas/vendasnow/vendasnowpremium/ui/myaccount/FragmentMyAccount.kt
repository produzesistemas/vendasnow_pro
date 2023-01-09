package com.produze.sistemas.vendasnow.vendasnowpremium.ui.myaccount

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.produze.sistemas.vendasnow.vendasnowpremium.LoginActivity
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.database.DataSourceUser
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentMyaccountBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Token
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class FragmentMyAccount : Fragment() {
    private lateinit var binding: FragmentMyaccountBinding
    private lateinit var viewModelMain: ViewModelMain
    private lateinit var viewModel: SubscriptionViewModel
    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private var datasource: DataSourceUser? = null
    private lateinit var user: Token
    var df = SimpleDateFormat("dd/MM/yyyy")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_myaccount,
            container,
            false
        )

        activity?.run {
            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
        } ?: throw Throwable("invalid activity")
        viewModelMain.updateActionBarTitle(getString(R.string.menu_about))
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        datasource = context?.let { DataSourceUser(it) }
        user = datasource?.get()!!
        if (user.token == "") {
            changeActivity()
        }
        viewModel = ViewModelProvider(this)[SubscriptionViewModel::class.java]

        activity?.run {
            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
        } ?: throw Throwable("invalid activity")
        viewModelMain.updateActionBarTitle(getString(R.string.menu_my_account))

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            MainUtils.snack(view, it.message, Snackbar.LENGTH_LONG)
            if (it.code == 401 || it.code == 600) {
                changeActivity()
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.subscription.observe(viewLifecycleOwner) {
            binding.textViewSelectedPlan.text = it.plan?.description
            binding.textViewValue.text = nFormat.format(it.value)
            binding.textViewSubscriptionDate.text = df.format(it.subscriptionDate)
            val c = Calendar.getInstance()
            c.time = it.subscriptionDate
            it.plan?.let { it1 -> c.add(Calendar.DATE, it1.days) }
            binding.textViewExpiration.text = df.format(c.time)
        }

        viewModel.getCurrentSubscription(user.token)
    }

    private fun changeActivity() {
        activity?.let{
            datasource!!.deleteAll()
            val intent = Intent (it, LoginActivity::class.java)
            it.startActivity(intent)
        }
    }

}