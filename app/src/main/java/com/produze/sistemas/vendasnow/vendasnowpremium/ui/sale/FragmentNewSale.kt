package com.produze.sistemas.vendasnow.vendasnowpremium.ui.sale

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.Timestamp
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.database.DataSourceUser
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentNewSaleBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.*
import com.produze.sistemas.vendasnow.vendasnowpremium.services.NotificationUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterSaleProduct
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterSaleService
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.MainUtils
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FragmentNewSale : Fragment(){
    private lateinit var viewModel: ViewModelSale
    private lateinit var viewModelClient: ClientViewModel
    private lateinit var viewModelProduct: ProductViewModel
    private lateinit var viewModelSaleProduct: ViewModelSaleProduct
    private lateinit var viewModelSaleService: ViewModelSaleService
    private lateinit var binding: FragmentNewSaleBinding
    private var sale: Sale = Sale()
    private lateinit var saleProduct: SaleProduct
    private lateinit var saleService: SaleService
    private lateinit var client: Client
    private lateinit var formPayment: FormPayment
    private var datePickerDialog: DatePickerDialog? = null
    private var formsPayment: MutableList<FormPayment> = ArrayList()
    val lst = mutableListOf<SaleProduct>()
    val accounts = mutableListOf<Account>()
    val lstServices = mutableListOf<SaleService>()
    val nFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private lateinit var viewModelMain: ViewModelMain
    private var datasource: DataSourceUser? = null
    private lateinit var token: Token
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_new_sale,
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
        viewModel = ViewModelProvider(this).get(ViewModelSale::class.java)
        viewModelClient = ViewModelProvider(this).get(ClientViewModel::class.java)
        viewModelProduct = ViewModelProvider(this).get(ProductViewModel::class.java)
        viewModelSaleProduct = ViewModelProvider(this).get(ViewModelSaleProduct::class.java)
        viewModelSaleService = ViewModelProvider(this).get(ViewModelSaleService::class.java)
        binding.progressBar.visibility = View.GONE;
        binding.recyclerViewProducts.apply {
            adapter = AdapterSaleProduct(lst, viewModelSaleProduct)
            layoutManager = LinearLayoutManager(context)
        }
        binding.recyclerViewServices.apply {
            adapter = AdapterSaleService(lstServices, viewModelSaleService)
            layoutManager = LinearLayoutManager(context)
        }
        binding.toolbar.inflateMenu(R.menu.menu_sale_add_product)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add_product -> {
                    loadProducts()
                    true
                }
                else -> false
            }
        }
        binding.toolbarService.inflateMenu(R.menu.menu_sale_add_service)
        binding.toolbarService.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add_service -> {
//                    loadServices()

                    saleService = SaleService()
                        val dialog = saleService?.let {
                            DialogSaleService(
//                                (state.data as MutableList<Service>).sortedWith(
//                                    compareBy(
//                                        String.CASE_INSENSITIVE_ORDER,
//                                        { it.name })
//                                )
                            ) {
                                view?.let { view ->
                                    lstServices.add(it)
                                    binding.recyclerViewServices.adapter = AdapterSaleService(
                                        lstServices,
                                        viewModelSaleService
                                    )
                                    viewModel.getTotalServices(lstServices)
                                    viewModel.getTotalSale(lstServices, lst)
                                }
                            }
                        }
                        dialog?.show(childFragmentManager, "dialog")

                    true
                }
                else -> false
            }
        }

        activity?.run {
            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
        } ?: throw Throwable("invalid activity")
        viewModelMain.updateActionBarTitle(getString(R.string.menu_new_sale))

        binding.spinnerClient.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                client = binding.spinnerClient.selectedItem as Client
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {

            }
        }

        binding.spinnerFormPayment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                formPayment = binding.spinnerFormPayment.selectedItem as FormPayment
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {

            }
        }

        binding.btnDate.setOnClickListener{ view: View? ->
            if (view != null) {
                val c = Calendar.getInstance()
                val mYear = c[Calendar.YEAR] // current year
                val mMonth = c[Calendar.MONTH] // current month
                val mDay = c[Calendar.DAY_OF_MONTH] // current day

                datePickerDialog = context?.let {
                    DatePickerDialog(
                        it,
                        { view, year, monthOfYear, dayOfMonth -> // set day of month , month and year value in the edit text
                            binding.textViewSaleDate.setText(
                                String.format(
                                    "%02d",
                                    dayOfMonth
                                ) + "/" + (monthOfYear + 1) + "/" + year
                            )
                        }, mYear, mMonth, mDay
                    )
                }
                datePickerDialog?.show()
            }
        }

        binding.bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        viewModel.totalProducts.observe(viewLifecycleOwner, Observer<Double> {
            binding.textViewTotalProducts.text = nFormat.format(it)
        })
        viewModel.getTotalProducts(lst)

        viewModel.totalServices.observe(viewLifecycleOwner, Observer<Double> {
            binding.textViewTotalServices.text = nFormat.format(it)
        })
        viewModel.getTotalServices(lstServices)

        viewModel.totalSale.observe(viewLifecycleOwner, Observer<Double> {
            binding.textViewTotalSale.text = nFormat.format(it)
        })
        viewModel.getTotalSale(lstServices, lst)

        val observerSaleProduct = Observer<SaleProduct> { saleProduct ->
            lst.remove(saleProduct)
            binding.recyclerViewProducts.apply {
                adapter = AdapterSaleProduct(lst, viewModelSaleProduct)
                layoutManager = LinearLayoutManager(context)
            }
            viewModel.getTotalProducts(lst)
            viewModel.getTotalSale(lstServices, lst)
        }
        viewModelSaleProduct.itemButtonClickEvent.observe(viewLifecycleOwner, observerSaleProduct)

        val observerSaleService = Observer<SaleService> { saleService ->
            lstServices.remove(saleService)
            binding.recyclerViewServices.apply {
                adapter = AdapterSaleService(lstServices, viewModelSaleService)
                layoutManager = LinearLayoutManager(context)
            }
            viewModel.getTotalServices(lstServices)
            viewModel.getTotalSale(lstServices, lst)
        }
        viewModelSaleService.itemButtonClickEvent.observe(viewLifecycleOwner, observerSaleService)

        binding.radioGroup.check(R.id.radioButtonProducts)
        binding.ConstraintLayoutServices.visibility = View.GONE
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioButtonProducts -> {
                    binding.ConstraintLayoutServices.visibility = View.GONE
                    binding.ConstraintLayoutProducts.visibility = View.VISIBLE
                }
                R.id.radioButtonServices -> {
                    binding.ConstraintLayoutServices.visibility = View.VISIBLE
                    binding.ConstraintLayoutProducts.visibility = View.GONE
                }
            }
        }

        loadClients()
        loadFormPayments()
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_confirm -> {
                if (binding.textViewSaleDate.text.isEmpty()) {
                    view?.let {
                        MainUtils.snack(
                            it,
                            this.resources.getString(R.string.validation_sale_date),
                            Snackbar.LENGTH_LONG)
                    }
                    return@OnNavigationItemSelectedListener true
                }

                if (binding.spinnerClient.selectedItem == null) {
                    view?.let {
                        MainUtils.snack(
                            it,
                            this.resources.getString(R.string.validation_sale_client),
                            Snackbar.LENGTH_LONG)
                    }
                   return@OnNavigationItemSelectedListener true
                }
                if (binding.spinnerFormPayment.selectedItem == null) {
                    view?.let {
                        MainUtils.snack(
                            it,
                            this.resources.getString(R.string.validation_sale_payment),
                            Snackbar.LENGTH_LONG)
                    }
                    return@OnNavigationItemSelectedListener true
                }
                if ((lst.size == 0) && (lstServices.size == 0)) {
                    view?.let {
                        MainUtils.snack(
                            it,
                            this.resources.getString(R.string.validation_sale_products_services),
                            Snackbar.LENGTH_LONG)
                    }
                    return@OnNavigationItemSelectedListener true
                }
                sale.client = binding.spinnerClient.selectedItem as Client
                sale.formPayment = formPayment
                sale.formPaymentId = formPayment.id.toInt()
                sale.saleProducts = lst
                sale.saleServices = lstServices
                val date =
                    SimpleDateFormat("dd/MM/yyyy").parse(binding.textViewSaleDate.text.toString())

                val cal = GregorianCalendar()
                cal.time = date
                cal.add(Calendar.HOUR_OF_DAY, 10)
                sale.salesDate = cal.time

                when (sale.formPayment!!.id) {
                    "4" -> {
                        var account = Account()
                        account.status = 1
                        val c = GregorianCalendar()
                        c.time = date
                        c.add(Calendar.DAY_OF_MONTH, 2)
                        c.add(Calendar.HOUR_OF_DAY, 10)

                        account.dueDate = c.time
                        account.value = viewModel.getTotalSaleToAccount(sale.saleServices.toMutableList(), sale.saleProducts.toMutableList())
                        account.uniqueIDNotification = UUID.randomUUID().hashCode()
                        account.mRequestCode = UUID.randomUUID().hashCode()
                        accounts.add(account)
                        sale.accounts = accounts
                    }
                    "7" -> {
                        var account = Account()
                        account.status = 1
                        val c = GregorianCalendar()
                        c.time = date
                        c.add(Calendar.MONTH, 1)
                        c.add(Calendar.HOUR_OF_DAY, 10)
                        account.dueDate = c.time
                        account.value = viewModel.getTotalSaleToAccount(sale.saleServices.toMutableList(), sale.saleProducts.toMutableList())
                        accounts.add(account)
                        sale.accounts = accounts
                    }
                    "8" -> {
                        for (i in 1..2) {
                            var account = Account()
                            account.status = 1
                            val c = GregorianCalendar()
                            c.time = date
                            c.add(Calendar.MONTH, i)
                            c.add(Calendar.HOUR_OF_DAY, 10)
                            account.dueDate = c.time
                            account.value = viewModel.getTotalSaleToAccount(sale.saleServices.toMutableList(), sale.saleProducts.toMutableList()) / 2
                            accounts.add(account)
                            sale.accounts = accounts
                        }

                    }
                    "9" -> {
                        for (i in 1..3) {
                            var account = Account()
                            account.status = 1
                            val c = GregorianCalendar()
                            c.time = date
                            c.add(Calendar.MONTH, i)
                            c.add(Calendar.HOUR_OF_DAY, 10)
                            account.dueDate = c.time
                            account.value = viewModel.getTotalSaleToAccount(sale.saleServices.toMutableList(), sale.saleProducts.toMutableList()) / 3
                            accounts.add(account)
                            sale.accounts = accounts
                        }
                    }
                    "10" -> {
                        for (i in 1..4) {
                            var account = Account()
                            account.status = 1
                            val c = GregorianCalendar()
                            c.time = date
                            c.add(Calendar.MONTH, i)
                            c.add(Calendar.HOUR_OF_DAY, 10)
                            account.dueDate = c.time
                            account.value = viewModel.getTotalSaleToAccount(sale.saleServices.toMutableList(), sale.saleProducts.toMutableList()) / 4
                            accounts.add(account)
                            sale.accounts = accounts
                        }
                    }

                }

                insert(sale, view)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_cancel -> {
                view?.findNavController()?.navigate(R.id.nav_sale)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun loadClients() {
        lifecycleScope.launch {
//            viewModelClient.getAll(token.email).collectLatest { state ->
//                when (state) {
//                    is State.Loading -> {
//                    }
//                    is State.Success -> {
//                        val adapter: ArrayAdapter<Client>? = context?.let {
//                            ArrayAdapter<Client>(
//                                it,
//                                android.R.layout.simple_spinner_dropdown_item,
//                                (state.data as MutableList<Client>).sortedWith(
//                                    compareBy(
//                                        String.CASE_INSENSITIVE_ORDER,
//                                        { it.name })
//                                )
//                            )
//                        }
//                        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                        binding.spinnerClient.adapter = adapter
//                    }
//
//                    is State.Failed -> {
//                        Toast.makeText(
//                            activity, state.message,
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
        }
    }
    private fun loadFormPayments() {
        val res = resources
        val forms = res.getStringArray(R.array.ArrayPayment)
        forms.forEach {
            val s = it.split(",")
            val form = FormPayment()
            form.id = s[0]
            form.name = s[1]
            formsPayment.add(form)
        }
        val adapterFormPayment: ArrayAdapter<FormPayment>? = context?.let { ArrayAdapter<FormPayment>(
            it,
            android.R.layout.simple_spinner_dropdown_item,
            formsPayment
        ) }
        adapterFormPayment?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFormPayment.adapter = adapterFormPayment
    }
    private fun loadProducts() {
//        lifecycleScope.launch {
//            viewModelProduct.getAll(token.email).collectLatest { state ->
//                when (state) {
//                    is State.Loading -> {
//
//                    }
//                    is State.Success -> {
//                        saleProduct = SaleProduct()
//                        val dialog = saleProduct?.let {
//                            DialogSaleProduct(
//                                (state.data as MutableList<Product>).sortedWith(
//                                    compareBy(
//                                        String.CASE_INSENSITIVE_ORDER,
//                                        { it.name })
//                                )
//                            ) {
//                                view?.let { view ->
//                                    lst.add(it)
//                                    binding.recyclerViewProducts.adapter = AdapterSaleProduct(
//                                        lst,
//                                        viewModelSaleProduct
//                                    )
//                                    viewModel.getTotalProducts(lst)
//                                    viewModel.getTotalSale(lstServices, lst)
//                                }
//                            }
//                        }
//                        dialog?.show(childFragmentManager, "dialog")
//                    }
//
//                    is State.Failed -> {
//                        Toast.makeText(
//                            activity, state.message,
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//        }
    }

    private fun insert(sale: Sale, view: View?) {
//        if (context?.let { it1 -> MainUtils.isOnline(it1) }!!) {
//        lifecycleScope.launch {
//            viewModel.add(sale, token.email).collectLatest { state ->
//                when (state) {
//                    is State.Loading -> {
//                        binding.progressBar.visibility = View.VISIBLE
//                    }
//
//                    is State.Success -> {
//                        val docReference = state.data
//                        sale.id = docReference.id
////                        sendNotification(sale)
//                        binding.progressBar.visibility = View.GONE
//                        view?.findNavController()?.navigate(R.id.nav_sale)
//                    }
//
//                    is State.Failed -> {
//                        binding.progressBar.visibility = View.GONE
//                        Toast.makeText(
//                            activity, state.message,
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//        }
//        } else {
//            Toast.makeText(context, R.string.validation_connection,
//                Toast.LENGTH_SHORT).show()
//        }

    }
//    private fun sendNotification(saleToNotification: Sale) {
//        when (saleToNotification.formPaymentId) {
//            4,7 -> {
//                var accountToNotification: Account = saleToNotification.accounts.first()
//                val c = GregorianCalendar()
//                c.time = accountToNotification.dueDate
//                activity?.let { NotificationUtils().setNotification(c, it, saleToNotification, accountToNotification) }
//            }
//            8 -> {
//                for (i in 1..2) {
//                    var accountToNotification: Account = saleToNotification.accounts[i - 1]
//                    val c = GregorianCalendar()
//                    c.time = accountToNotification.dueDate
//                    activity?.let { NotificationUtils().setNotification(c, it, saleToNotification, accountToNotification) }
//                }
//
//            }
//            9 -> {
//                for (i in 1..3) {
//                    var accountToNotification: Account = saleToNotification.accounts[i - 1]
//                    val c = GregorianCalendar()
//                    c.time = accountToNotification.dueDate
//                    activity?.let { NotificationUtils().setNotification(c, it, saleToNotification, accountToNotification) }
//                }
//            }
//            10 -> {
//                for (i in 1..4) {
//                    var accountToNotification: Account = saleToNotification.accounts[i - 1]
//                    val c = GregorianCalendar()
//                    c.time = accountToNotification.dueDate
//                    activity?.let { NotificationUtils().setNotification(c, it, saleToNotification, accountToNotification) }
//                }
//            }
//
//        }
//    }




    }


