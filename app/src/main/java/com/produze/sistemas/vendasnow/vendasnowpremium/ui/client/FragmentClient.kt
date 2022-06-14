package com.produze.sistemas.vendasnow.vendasnowpremium.ui.client

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
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
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentClientBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.model.Client
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterClient
import com.produze.sistemas.vendasnow.vendasnowpremium.utils.State
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelClient
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelMain
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.produze.sistemas.vendasnow.vendasnowpremium.MainActivity
import android.view.*
import android.view.MenuInflater

import androidx.annotation.NonNull
import android.app.SearchManager

import androidx.core.view.MenuItemCompat
import android.content.ActivityNotFoundException

import android.content.Intent
import android.net.Uri


class FragmentClient : Fragment() {

    private lateinit var viewModel: ViewModelClient
    private lateinit var binding: FragmentClientBinding
    private lateinit var client: Client
    private lateinit var adapterClient: AdapterClient
    private lateinit var viewModelMain: ViewModelMain
    private var mSearchItem: MenuItem? = null
    private var mHelp: MenuItem? = null
    private var sv: SearchView? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.fragment_client,
                container,
                false
        )
        return binding.root
    }


    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true);
        viewModel = ViewModelProvider(this).get(ViewModelClient::class.java)
        adapterClient = AdapterClient(arrayListOf(), viewModel)
        binding.bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val observer = Observer<Client> { client ->
            lifecycleScope.launch {
                viewModel.delete(client).collectLatest { state ->
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

        val observerEdit = Observer<Client> { client ->
            load()
        }
        viewModel.itemButtonClickEventEdit.observe(viewLifecycleOwner, observerEdit)

        activity?.run {
            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
        } ?: throw Throwable("invalid activity")
        viewModelMain.updateActionBarTitle(getString(R.string.menu_client))

        load()
        }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_add -> {
                client = Client()
                        val dialog = client?.let {
                            DialogNewClient(viewModel, it) {
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
                        adapterClient  = AdapterClient((state.data as MutableList<Client>).sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.name })), viewModel)
                        binding.recyclerView.apply {
                            adapter = adapterClient
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
                adapterClient.filter.filter(query)
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
            this.watchYoutubeVideo("xYE65jd9byQ")
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



