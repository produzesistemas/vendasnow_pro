package com.produze.sistemas.vendasnow.vendasnowpremium.ui.service

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentServiceBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Service
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterService
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelMain
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FragmentService : Fragment() {

    private lateinit var viewModel: ViewModelService
    private lateinit var binding: FragmentServiceBinding
    private lateinit var product: Service
    private lateinit var viewModelMain: ViewModelMain
    private lateinit var adapterService: AdapterService

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.fragment_service,
                container,
                false
        )
        return binding.root
    }


    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ViewModelService::class.java)
        adapterService = AdapterService(arrayListOf(), viewModel)
        binding.bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        binding.searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapterService.filter.filter(newText)
                    return false
                }

            })
        }

        val observer = Observer<Service> { product ->
            lifecycleScope.launch {
                viewModel.delete(product).collectLatest { state ->
                    when (state) {
                        is State.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is State.Success -> {
                            load()
                        }
                        is State.Failed -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(activity, state.message,
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        viewModel.itemButtonClickEvent.observe(viewLifecycleOwner, observer)

        val observerEdit = Observer<Service> { product ->
            load()
        }
        viewModel.itemButtonClickEventEdit.observe(viewLifecycleOwner, observerEdit)
        activity?.run {
            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
        } ?: throw Throwable("invalid activity")
        viewModelMain.updateActionBarTitle(getString(R.string.menu_service))

        load()

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_add -> {
                product = Service()
                val dialog = product?.let {
                    DialogNewService(viewModel, it) {
                        view?.let { view ->
                            load()
                        }
                    }
                }
                dialog?.show(childFragmentManager, "dialog")
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun load() {
        lifecycleScope.launch {
            viewModel.getAll().collectLatest { state ->
                when (state) {
                    is State.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is State.Success -> {
                        adapterService  = AdapterService((state.data as MutableList<Service>).sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.name })), viewModel)
                        binding.recyclerView.apply {
                            adapter = adapterService
                            layoutManager = LinearLayoutManager(context)
                        }
                        binding.progressBar.visibility = View.GONE
                    }

                    is State.Failed -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(activity, state.message,
                                Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }




}