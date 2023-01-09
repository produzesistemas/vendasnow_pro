package com.produze.sistemas.vendasnow.vendasnowpremium.ui.subscription

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.produze.sistemas.vendasnow.vendasnowpremium.LoginActivity
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.database.DataSourceUser
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentSubscriptionBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.*
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.*
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.*
import com.smarteist.autoimageslider.SliderView
import java.text.NumberFormat
import java.util.*


class FragmentSubscription : Fragment(){
    private lateinit var viewModel: SubscriptionViewModel
    private lateinit var viewModelCielo: CieloViewModel
    private lateinit var binding: FragmentSubscriptionBinding
    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private lateinit var viewModelMain: ViewModelMain
    private var datasource: DataSourceUser? = null
    private lateinit var token: Token
    private var selectedPlan: Plan? = null
    private var card = Card()
    private lateinit var adapterPlan: AdapterPlan
    lateinit var imageUrl: ArrayList<String>
    lateinit var sliderAdapter: SliderAdapter
    private var brands: MutableList<Brand> = ArrayList()

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
        viewModelCielo = ViewModelProvider(this)[CieloViewModel::class.java]
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

        binding.cardViewSelectedPlan.visibility = View.GONE
        binding.constraintLayoutRadio.visibility = View.GONE
        binding.constraintLayoutCard.visibility = View.GONE
        binding.constraintLayoutBrands.visibility = View.GONE
        binding.radioGroup.check(R.id.radioButtonCreditCard)
        viewModel.errorMessage.observe(viewLifecycleOwner) {
            MainUtils.snack(view, it.message, Snackbar.LENGTH_LONG)
            if (it.code == 401 || it.code == 600) {
                changeActivity()
            }

        }

        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.progressBarPlan.visibility = View.VISIBLE
            } else {
                binding.progressBarPlan.visibility = View.GONE
            }
        })

        viewModel.complete.observe(viewLifecycleOwner, Observer {
            if (it) {
                changeActivity()
            }
        })

        viewModel.plans.observe(viewLifecycleOwner) {
            adapterPlan  = AdapterPlan((it), viewModel)
            binding.recyclerView.apply {
                adapter = adapterPlan
                layoutManager = LinearLayoutManager(context)
            }
        }

        viewModel.plan.observe(viewLifecycleOwner) {
            binding.cardView.visibility = View.GONE
            binding.constraintLayoutSlider.visibility = View.GONE
            binding.recyclerView.visibility = View.GONE
            binding.cardViewSelectedPlan.visibility = View.VISIBLE
            binding.constraintLayoutRadio.visibility = View.VISIBLE
            binding.constraintLayoutCard.visibility = View.VISIBLE
            binding.constraintLayoutBrands.visibility = View.VISIBLE
            binding.textViewSelectedPlan.text = it.description + " / " + nFormat.format(it.value)
            this.selectedPlan = it
        }

        viewModelCielo.responseCard.observe(viewLifecycleOwner, Observer {
            MainUtils.snack(view, it.CardToken.toString(), Snackbar.LENGTH_LONG)

        })

        viewModelCielo.loading.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.imageViewConfirm.visibility = View.GONE
                binding.textViewConfirm.visibility = View.GONE
                binding.progressBarConfirm.visibility = View.VISIBLE
            } else {
                binding.progressBarConfirm.visibility = View.GONE
                binding.imageViewConfirm.visibility = View.VISIBLE
                binding.textViewConfirm.visibility = View.VISIBLE
            }
        })

        viewModelCielo.errorMessage.observe(viewLifecycleOwner) {
            MainUtils.snack(view, it.message, Snackbar.LENGTH_LONG)
            if (it.code == 401 || it.code == 600) {
                changeActivity()
            }
        }

        viewModelCielo.completeValidateCard.observe(viewLifecycleOwner, Observer {
            if (it) {

                card.CardNumber = binding.editTextNumberCard.text.toString()
                val brand = binding.spinnerBrand.selectedItem as Brand
                card.Brand = brand.description
                card.Holder = binding.editTextHolder.text.toString()
                card.CustomerName = binding.editTextHolder.text.toString()
                card.ExpirationDate = binding.editTextExpiration.text.toString()
                viewModelCielo.getCardToken(this.resources.getString(R.string.MerchantId),
                    this.resources.getString(R.string.MerchantKey),
                    card
                )
            } else {
                MainUtils.snack(view, "Número de Cartão inválido", Snackbar.LENGTH_LONG)
//                Log.d("VendasNowPro", "Cartão inválido")
            }
        })

        binding.cardViewConfirm.setOnClickListener{
            if (context?.let { it1 -> MainUtils.isOnline(it1) }!!) {
                if ((binding.editTextNumberCard.text.toString() == "") ||
                    (binding.editTextExpiration.text.toString() == "") ||
                        (binding.editTextHolder.text.toString() == "") ||
                    (binding.editTextSecurityCode.text.toString() == "")){
                    MainUtils.snackInTop(
                        it,
                        this.resources.getString(R.string.validation_card),
                        Snackbar.LENGTH_LONG
                    )
                } else {
                    viewModelCielo.validCardNumber(binding.editTextNumberCard.text.toString())
                }

            } else {
                MainUtils.snackInTop(it, this.resources.getString(R.string.validation_connection), Snackbar.LENGTH_LONG)
            }
        }
        loadBrands()
        viewModel.getAllPlan()

    }



    private fun changeActivity() {
        activity?.let{
            datasource!!.deleteAll()
            val intent = Intent (it, LoginActivity::class.java)
            it.startActivity(intent)
        }
    }

    private fun loadBrands() {
        val res = resources
        val forms = res.getStringArray(R.array.ArrayBrands)
        forms.forEach {
            val form = Brand()
            form.description = it
            brands.add(form)
        }
        val adapterBrand: ArrayAdapter<Brand>? = context?.let { ArrayAdapter<Brand>(
            it,
            android.R.layout.simple_spinner_dropdown_item,
            brands
        ) }
        adapterBrand?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBrand .adapter = adapterBrand
    }
    }


