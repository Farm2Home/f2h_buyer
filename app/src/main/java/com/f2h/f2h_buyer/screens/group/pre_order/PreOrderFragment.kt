package com.f2h.f2h_buyer.screens.group.pre_order

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.database.F2HDatabase
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.databinding.FragmentPreOrderBinding


/**
 * A simple [Fragment] subclass.
 */
class PreOrderFragment : Fragment() {

    private lateinit var binding: FragmentPreOrderBinding
    private val application: Application by lazy { requireNotNull(this.activity).application }
    private val dataSource: SessionDatabaseDao by lazy { F2HDatabase.getInstance(application).sessionDatabaseDao }
    private val viewModelFactory: PreOrderViewModelFactory by lazy { PreOrderViewModelFactory(dataSource, application) }
    private val viewModel: PreOrderViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(
        PreOrderViewModel::class.java) }

    val args: PreOrderFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflate(inflater, R.layout.fragment_pre_order, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        //Call the API to fetch item data to populate page
        viewModel.setItemAndFarmer(args.item)
        viewModel.fetchOrderData()

        // Item list recycler view
        val adapter =
            PreOrderItemsAdapter(
                PreOrderItemClickListener { preOrderUiElement ->
                }, IncreaseButtonClickListener { preOrderUiElement ->
                    viewModel.increaseOrderQuantity(preOrderUiElement)
                }, DecreaseButtonClickListener { preOrderUiElement ->
                    viewModel.decreaseOrderQuantity(preOrderUiElement)
                })
        binding.preOrderItemRecyclerView.adapter = adapter
        viewModel.preOrderItems.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        })

        //Toast Message
        viewModel.toastMessage.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        })

        //Successful dialog Message
        viewModel.orderSuccessful.observe(viewLifecycleOwner, Observer { isSuccessful ->
            if(isSuccessful) {
                val dialog = SuccessDialogFragment()
                dialog.show(childFragmentManager, "Success Dialog")
            }
        })

        //Call Button
        binding.preOrderCallButton.setOnClickListener {
            startPhoneCall()
        }

        return binding.root
    }

    fun startPhoneCall() {
        requestPermissions(arrayOf(Manifest.permission.CALL_PHONE),42)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(grantResults[0] == PackageManager.PERMISSION_DENIED){
            Toast.makeText(activity, "Please accept permission request to continue", Toast.LENGTH_SHORT).show()
            return
        }
        if(viewModel.preOrderUiModel.value?.farmerMobile.isNullOrBlank()){
            Toast.makeText(activity, "Invalid mobile number", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + viewModel.preOrderUiModel.value?.farmerMobile))
        startActivity(intent)
    }

}
