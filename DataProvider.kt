package com.example.jetpackcomposeviewtrackingdemo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface DataProvider {
    val cards: StateFlow<List<CardDataStructure>>
}

sealed class CardDataStructure {
    abstract val id: Int
    abstract val title: String

    object Loading : CardDataStructure() {
        override val id: Int = -1
        override val title: String = "Loading..."

    }

    data class Card(
        override val id: Int,
        val imageName: String,
        override val title: String
    ) : CardDataStructure()
}

class DataService : DataProvider {
    private val _cards = MutableStateFlow<List<CardDataStructure>>(emptyList())
    override val cards: StateFlow<List<CardDataStructure>>
        get() = _cards

    init {
        _cards.value = List(100) { index ->
            CardDataStructure.Card(id = index + 1, imageName = "star", title = "Card ${index + 1}")
        }
    }
}
