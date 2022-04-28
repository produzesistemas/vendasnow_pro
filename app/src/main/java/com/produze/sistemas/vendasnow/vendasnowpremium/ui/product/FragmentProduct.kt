package com.produze.sistemas.vendasnow.vendasnowpremium.ui.product

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentProductBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Product
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterProduct
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelMain
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelProduct
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FragmentProduct : Fragment() {

    private lateinit var viewModel: ViewModelProduct
    private lateinit var binding: FragmentProductBinding
    private lateinit var product: Product
    private lateinit var viewModelMain: ViewModelMain
    private lateinit var adapterProduct: AdapterProduct
    private var mSearchItem: MenuItem? = null
    private var sv: SearchView? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.fragment_product,
                container,
                false
        )
        return binding.root
    }


    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true);
        viewModel = ViewModelProvider(this).get(ViewModelProduct::class.java)
        adapterProduct = AdapterProduct(arrayListOf(), viewModel)
        binding.bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val observer = Observer<Product> { product ->
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

        val observerEdit = Observer<Product> { product ->
            load()
        }
        viewModel.itemButtonClickEventEdit.observe(viewLifecycleOwner, observerEdit)

        activity?.run {
            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
        } ?: throw Throwable("invalid activity")
        viewModelMain.updateActionBarTitle(getString(R.string.menu_product))

        load()
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_add -> {
                product = Product()
                val dialog = product?.let {
                    DialogNewProduct(viewModel, it) {
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
                        adapterProduct  = AdapterProduct((state.data as MutableList<Product>).sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.name })), viewModel)
                        binding.recyclerView.apply {
                            adapter = adapterProduct
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_searchview, menu)

        mSearchItem = menu.findItem(R.id.action_search)
        sv = MenuItemCompat.getActionView(mSearchItem) as SearchView
        sv!!.isIconified = true
        sv!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                adapterProduct.filter.filter(query)
                return false
            }
        })


        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        val id = item!!.itemId
        //handle item clicks
        if (id == R.id.action_help){
            //do your action here, im just showing toast
            this.watchYoutubeVideo("zEJ9zLWU8sg")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun watchYoutubeVideo(id: String) {
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$id"))
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://www.youtube.com/watch?v=$id")
        )
        try {
            startActivity(appIntent)
        } catch (ex: ActivityNotFoundException) {
            startActivity(webIntent)
        }
    }

}

