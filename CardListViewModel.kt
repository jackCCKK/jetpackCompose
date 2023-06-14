package com.example.jetpackcomposeviewtrackingdemo.viewtracking

import CardVisibilityTracker
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposeviewtrackingdemo.CardDataStructure
import com.example.jetpackcomposeviewtrackingdemo.DataProvider
import com.example.jetpackcomposeviewtrackingdemo.DataService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface CellVisibilityDelegate {
    fun visibleCellsChanged(visibleCells: List<Int>)
    fun cardAppeared(card: CardDataStructure)
    fun cardDisappeared(card: CardDataStructure)
}

open class CardListViewModel : CellVisibilityDelegate, ViewModel() {

    private val dataService: DataProvider = DataService()
    val cards: StateFlow<List<CardDataStructure>> = dataService.cards
    private val _validIds = MutableStateFlow<List<Int>>(emptyList())
    open val validIds: StateFlow<List<Int>> = _validIds

    private val cardVisibilityTracker = CardVisibilityTracker(scope = viewModelScope).also {
        it.cellVisibilityDelegate = this
    }

    override fun visibleCellsChanged(visibleCells: List<Int>) {
        _validIds.value = visibleCells
    }

    override fun cardAppeared(card: CardDataStructure) {
        cardVisibilityTracker.cardAppeared(card)
    }

    override fun cardDisappeared(card: CardDataStructure) {
        cardVisibilityTracker.cardDisappeared(card)
    }
}

