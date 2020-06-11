package com.f2h.f2h_buyer.utils


import android.widget.Button
import androidx.databinding.BindingAdapter
import com.f2h.f2h_buyer.screens.search_group.SearchGroupsItemsModel


@BindingAdapter("buttonVisibilityFormatted")
fun Button.setButtonVisibilityFormatted(data: SearchGroupsItemsModel){
    isEnabled = !data.isAlreadyMember
}
