package com.produze.sistemas.vendasnow.vendasnowpremium.ui.client

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentClientBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterClient
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelMain
import android.view.*
import android.view.MenuInflater
import androidx.core.view.MenuItemCompat
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import com.produze.sistemas.vendasnow.vendasnowpremium.LoginActivity
import com.produze.sistemas.vendasnow.vendasnowpremium.database.DataSourceUser
import com.produze.sistemas.vendasnow.vendasnowpremium.model.*
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ClientViewModel

class FragmentClient : Fragment() {

    private lateinit var clientViewModel: ClientViewModel
    private lateinit var binding: FragmentClientBinding
    private lateinit var client: Client
    private lateinit var adapterClient: AdapterClient
    private lateinit var viewModelMain: ViewModelMain
    private var mSearchItem: MenuItem? = null
    private var sv: SearchView? = null
    private var datasource: DataSourceUser? = null
    private lateinit var token: Token
    private var filter = FilterDefault()

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
        datasource = context?.let { DataSourceUser(it) }
        token = datasource?.get()!!
        if (token.token == "") {

        }
        clientViewModel = ViewModelProvider(this).get(ClientViewModel::class.java)

        adapterClient = AdapterClient(arrayListOf(), clientViewModel)

        binding.bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val observer = Observer<Client> { client ->
                clientViewModel.delete(client, token.token)
        }
        clientViewModel.itemButtonClickEvent.observe(viewLifecycleOwner, observer)

        val observerEdit = Observer<ResponseBody> {
//            filter.sizePage = 10
//            clientViewModel.getPagination(token.token, filter)
//            load()
        }
        clientViewModel.itemButtonClickEventEdit.observe(viewLifecycleOwner, observerEdit)

        activity?.run {
            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
        } ?: throw Throwable("invalid activity")
        viewModelMain.updateActionBarTitle(getString(R.string.menu_client))

        clientViewModel.lst.observe(this) {
            adapterClient  = AdapterClient((it).sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.name })), clientViewModel)
            binding.recyclerView.apply {
                adapter = adapterClient
                layoutManager = LinearLayoutManager(context)
            }
            binding.progressBar.visibility = View.GONE
        }

        clientViewModel.errorMessage.observe(this) {
//            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        clientViewModel.loading.observe(this, Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })

        clientViewModel.complete.observe(this, Observer {
            if (it) {
                clientViewModel.getAll(token.token)
            }
        })

        clientViewModel.getAll(token.token)
        }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_add -> {
                client = Client()
                        val dialog = DialogNewClient(clientViewModel, client)
                        dialog?.show(childFragmentManager, "dialog")
                return@OnNavigationItemSelectedListener true
            }
        }
        false
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
//                adapterClient.filter.filter(query)
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item!!.itemId
        if (id == R.id.action_help){
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

    private fun changeActivity() {
        activity?.let{
            val intent = Intent (it, LoginActivity::class.java)
            it.startActivity(intent)
        }
    }

}



