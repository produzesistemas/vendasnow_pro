package com.produze.sistemas.vendasnow.vendasnowpremium.ui.about

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.produze.sistemas.vendasnow.vendasnowpremium.R
import com.produze.sistemas.vendasnow.vendasnowpremium.databinding.FragmentAboutBinding


class FragmentAbout : Fragment() {
    private lateinit var binding: FragmentAboutBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_about,
            container,
            false
        )
        binding.versaoTextView.text = getPackageVersion()

        return binding.root
    }

    private fun getPackageVersion(): String? {
        try {
            val pInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            return pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            //e.printStackTrace();
        }
        return "0.0"
    }
}