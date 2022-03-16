package com.produze.sistemas.vendasnow.vendasnowpremium.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentGraphicsBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentHomeBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterGraphics
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterMain
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelMain

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModelMain: ViewModelMain
    private lateinit var adapterMain: AdapterMain
    private var lstMain: MutableList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_home,
            container,
            false
        )
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val res = resources
        lstMain = ArrayList()
        val graphicTitles = res.getStringArray(R.array.Menu_Main)
        graphicTitles.forEach {
            lstMain.add(it)
        }
        adapterMain = AdapterMain(lstMain)
        binding.recyclerView.apply {
            adapter = adapterMain
            layoutManager = LinearLayoutManager(context)
        }

        activity?.run {
            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
        } ?: throw Throwable("invalid activity")
        viewModelMain.updateActionBarTitle(getString(R.string.app_name))

    }
}