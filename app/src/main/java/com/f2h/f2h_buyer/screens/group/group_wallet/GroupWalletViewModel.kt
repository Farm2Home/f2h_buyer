package com.f2h.f2h_buyer.screens.group.group_wallet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.f2h.f2h_buyer.database.SessionDatabaseDao
import com.f2h.f2h_buyer.database.SessionEntity
import com.f2h.f2h_buyer.network.WalletApi
import com.f2h.f2h_buyer.network.models.Wallet
import com.f2h.f2h_buyer.network.models.WalletTransaction
import kotlinx.coroutines.*

class GroupWalletViewModel(val database: SessionDatabaseDao, application: Application) : AndroidViewModel(application) {
    private val _wallet = MutableLiveData<Wallet>()
    val wallet: LiveData<Wallet>
        get() = _wallet

    private val _isProgressBarActive = MutableLiveData<Boolean>()
    val isProgressBarActive: LiveData<Boolean>
        get() = _isProgressBarActive

    private var _visibleUiData = MutableLiveData<MutableList<WalletItemsModel>>()
    val visibleUiData: LiveData<MutableList<WalletItemsModel>>
        get() = _visibleUiData

    private var userSession = SessionEntity()
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getWalletInformation()
    }

    fun refreshFragmentData(){
        getWalletInformation()
    }

    private fun getWalletInformation() {
        _isProgressBarActive.value = true
        coroutineScope.launch {
            userSession = retrieveSession()
            try {

                val activeWalletData = WalletApi.retrofitService.getWalletDetails(userSession.groupId, userSession.userId).await()
                val walletData = activeWalletData.firstOrNull() ?: Wallet()
                val activeWalletTransactionData = WalletApi.retrofitService.getWalletTransactionDetails(walletData.walletId ?: -1).await()
                val walletTransactions: List<WalletTransaction> = activeWalletTransactionData
                _wallet.value = walletData
                _wallet.value!!.currency = userSession.groupCurrency
                val walletItemUiData =  ArrayList<WalletItemsModel>()
                walletTransactions.forEach { transaction ->
                    walletItemUiData.add(WalletItemsModel(
                            transaction.walletLedgerId ?: -1,
                            transaction.transactionDate ?: "",
                            transaction.transactionDescription?.trim() ?: "",
                        userSession.groupCurrency ?: "",
                            transaction.amount ?: 0.0
                            )
                    )
                }
                walletItemUiData.sortByDescending { it.transactionDate }
                _visibleUiData.value = walletItemUiData
            } catch (t:Throwable){
                println(t.message)
            }
            _isProgressBarActive.value = false
        }
    }


    private suspend fun retrieveSession() : SessionEntity {
        return withContext(Dispatchers.IO) {
            val sessions = database.getAll()
            var session = SessionEntity()
            if (sessions.size==1) {
                session = sessions[0]
                println(session.toString())
            } else {
                database.clearSessions()
            }
            return@withContext session
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}
