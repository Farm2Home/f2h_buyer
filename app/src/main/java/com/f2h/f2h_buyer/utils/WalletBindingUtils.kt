package com.f2h.f2h_buyer.utils

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.f2h.f2h_buyer.R
import com.f2h.f2h_buyer.network.models.Wallet
import com.f2h.f2h_buyer.screens.group.group_wallet.GroupWalletViewModel
import com.f2h.f2h_buyer.screens.group.group_wallet.WalletItemsModel
import java.text.DateFormat
import java.text.SimpleDateFormat


@BindingAdapter("walletBalanceFormatted")
fun TextView.setWalletBalanceFormatted(data: Wallet?) {
    text = String.format("Balance : %s%.0f", data?.currency, data?.balance)
}


@BindingAdapter("transactionAmountFormatted")
fun TextView.setPriceFormatted(data: WalletItemsModel?){
    data?.let {
        val colouredText = SpannableString(String.format("%s %.0f", data.currency, data.amount))

        if (data.amount > 0){
            colouredText.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.green_status)),0, colouredText.length,0)
        }

        if (data.amount < 0){
            colouredText.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.red_status)),0, colouredText.length,0)
        }

        text = colouredText
    }
}


@BindingAdapter("transactionDateFormatted")
fun TextView.settransactionDateFormatted(data: WalletItemsModel?) {
    data?.let {
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val df_out: DateFormat = SimpleDateFormat("dd-MMM-EEEE")
        if (data.transactionDate.isNullOrBlank()) {
            text = ""
            return
        }

        var formattedDate: String = df_out.format(df.parse(data.transactionDate))
        text = String.format("%s", formattedDate)
    }

}

