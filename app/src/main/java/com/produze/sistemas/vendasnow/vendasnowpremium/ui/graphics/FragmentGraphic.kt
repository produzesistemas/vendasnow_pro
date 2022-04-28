package com.produze.sistemas.vendasnow.vendasnowpremium.ui.graphics

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentGraphicsBinding
import com.produze.sistemas.vendasnow.vendasnowpremium.ui.adapters.AdapterGraphics
import com.produze.sistemas.vendasnow.vendasnowpremium.viewmodel.ViewModelMain

class FragmentGraphic : Fragment() {
    private lateinit var binding: FragmentGraphicsBinding
    private lateinit var viewModelMain: ViewModelMain
    private lateinit var adapterGraphics: AdapterGraphics
    private var lstGraphics: MutableList<String> = ArrayList()
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.fragment_graphics,
                container,
                false
        )
        return binding.root
    }


    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val res = resources
        lstGraphics = ArrayList()
        val graphicTitles = res.getStringArray(R.array.Graphics)
        graphicTitles.forEach {
            lstGraphics.add(it)
        }
        adapterGraphics = AdapterGraphics(lstGraphics)
        binding.recyclerView.apply {
            adapter = adapterGraphics
            layoutManager = LinearLayoutManager(context)
        }

        activity?.run {
            viewModelMain = ViewModelProvider(this).get(ViewModelMain::class.java)
        } ?: throw Throwable("invalid activity")
        viewModelMain.updateActionBarTitle(getString(R.string.menu_graphics))

    }


}