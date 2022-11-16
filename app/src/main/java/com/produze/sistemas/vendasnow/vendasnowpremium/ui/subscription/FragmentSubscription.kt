package com.produze.sistemas.vendasnow.vendasnowpremium.ui.subscription

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.produze.sistemas.vendasnow.vendasnowpremium.LoginActivity
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.database.DataSourceUser
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentNewSaleBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentSubscriptionBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.*
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.*
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.*
import com.smarteist.autoimageslider.SliderView
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FragmentSubscription : Fragment(){
    private lateinit var viewModel: SubscriptionViewModel
    private lateinit var binding: FragmentSubscriptionBinding
    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private lateinit var viewModelMain: ViewModelMain
    private var datasource: DataSourceUser? = null
    private lateinit var token: Token
    private lateinit var selectedPlan: Plan
    private lateinit var adapterPlan: AdapterPlan
    lateinit var imageUrl: ArrayList<String>
    lateinit var sliderView: SliderView
    lateinit var sliderAdapter: SliderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_subscription,
            container,
            false
        )

        return binding.root
    }


    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        datasource = context?.let { DataSourceUser(it) }
        token = datasource?.get()!!
        if (token.token == "") {

        }
        viewModel = ViewModelProvider(this).get(SubscriptionViewModel::class.java)
        adapterPlan = AdapterPlan(arrayListOf(), viewModel)
        activity?.run {
            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
        } ?: throw Throwable("invalid activity")
        viewModelMain.updateActionBarTitle(getString(R.string.menu_subscription))

        imageUrl = ArrayList()
        imageUrl =
            (imageUrl + "https://produzesistemas.com.br/assets/item1.jpg") as ArrayList<String>
        imageUrl =
            (imageUrl + "https://produzesistemas.com.br/assets/item2.jpg") as ArrayList<String>
        imageUrl =
            (imageUrl + "https://produzesistemas.com.br/assets/item3.jpg") as ArrayList<String>
        imageUrl =
            (imageUrl + "https://produzesistemas.com.br/assets/item4.jpg") as ArrayList<String>
        imageUrl =
            (imageUrl + "https://produzesistemas.com.br/assets/item5.jpg") as ArrayList<String>

        sliderAdapter = SliderAdapter( imageUrl)
        // on below line we are setting auto cycle direction
        // for our slider view from left to right.
        binding.slider.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR

        // on below line we are setting adapter for our slider.
        binding.slider.setSliderAdapter(sliderAdapter)

        // on below line we are setting scroll time
        // in seconds for our slider view.
        binding.slider.scrollTimeInSec = 3

        // on below line we are setting auto cycle
        // to true to auto slide our items.
        binding.slider.isAutoCycle = true

        // on below line we are calling start
        // auto cycle to start our cycle.
        binding.slider.startAutoCycle()

        viewModel.errorMessage.observe(this) {
            MainUtils.snack(view, it.message, Snackbar.LENGTH_LONG)
            if (it.code == 401 || it.code == 600) {
                changeActivity()
            }

        }

        viewModel.loading.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })

        viewModel.complete.observe(this, Observer {
            if (it) {
                changeActivity()
            }
        })

        viewModel.plans.observe(this) {
            adapterPlan  = AdapterPlan((it), viewModel)
            binding.recyclerView.apply {
                adapter = adapterPlan
                layoutManager = LinearLayoutManager(context)
            }
        }

        viewModel.plan.observe(this) {
            this.selectedPlan = it
        }

        viewModel.getAllPlan()

    }



    private fun changeActivity() {
        activity?.let{
            datasource!!.deleteAll()
            val intent = Intent (it, LoginActivity::class.java)
            it.startActivity(intent)
        }
    }


    }

