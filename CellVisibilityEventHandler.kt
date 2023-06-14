import com.example.jetpackcomposeviewtrackingdemo.CardDataStructure
import com.example.jetpackcomposeviewtrackingdemo.viewtracking.CellVisibilityDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


class CardVisibilityTracker(private val scope: CoroutineScope) {

    private val visibleCards: MutableStateFlow<Set<CardDataStructure>> = MutableStateFlow(emptySet())
    var cellVisibilityDelegate: CellVisibilityDelegate? = null

    init {
        setupSubscription()
    }

    fun cardAppeared(card: CardDataStructure) {
        val current = visibleCards.value.toMutableSet()
        current.add(card)
        visibleCards.value = current
    }

    fun cardDisappeared(card: CardDataStructure) {
        val current = visibleCards.value.toMutableSet()
        current.remove(card)
        visibleCards.value = current
    }

    private fun setupSubscription() {
        scope.launch {
            visibleCards
                .debounce(500)
                .distinctUntilChanged()
                .collect { cards ->
                    cellVisibilityDelegate?.visibleCellsChanged(cards.map { it.id })
                }
        }
    }
}
