package com.example.jetpackcomposeviewtrackingdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposeviewtrackingdemo.ui.theme.JetpackComposeViewTrackingDemoTheme
import com.example.jetpackcomposeviewtrackingdemo.viewtracking.CardListViewModel
import com.example.jetpackcomposeviewtrackingdemo.viewtracking.customModifier2
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: CardListViewModel by viewModels()

        setContent {
            JetpackComposeViewTrackingDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    RecyclerView(viewModel)
                }
            }
        }
    }
}

@Composable
fun RecyclerView(viewModel: CardListViewModel) {
    val cards by viewModel.cards.collectAsState()
    val validIds by viewModel.validIds.collectAsState()
    val state = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = cards) { card ->
            BasicCardView(isValidCard = validIds.contains(card.id),
                card = card,
                onVisible = { viewModel.cardAppeared(card) },
                onDisappear = { viewModel.cardDisappeared(card) })
        }
    }
}

typealias onVisible = (card: CardDataStructure) -> Unit
typealias onDisapear = (card: CardDataStructure) -> Unit

@Composable
fun BasicCardView(
    isValidCard: Boolean, card: CardDataStructure, onVisible: onVisible, onDisappear: onDisapear
) {
    val scale by animateFloatAsState(targetValue = if (isValidCard) 0.9f else 1f,
        animationSpec = infiniteRepeatable(animation = keyframes {
            durationMillis = 1000
            0.9f at 0
            1f at 500
            0.9f at 1000
        }))

    val borderColor by animateColorAsState(
        targetValue = if (isValidCard) Color.Green else Color.Transparent,
    )

    val backgroundColor = when (card) {
        is CardDataStructure.Card -> Color(56f / 255, 87f / 255, 242f / 255)
        CardDataStructure.Loading -> Color(223f / 255, 247f / 255, 7f / 255)
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
        .padding(horizontal = 26.dp)
        .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
        .shadow(0.dp, shape = RoundedCornerShape(8.dp))
        .customModifier2(onFullyVisible = {
            onVisible(card)
            println("cxj--------------------------hello, fullyVisible ${card.id}")
        }, onNotFullyVisible = {
            onDisappear(card)
            println("cxj--------------------------hello, NotFullyVisible ${card.id}")
        }, visibilityThreshold = 0.8f
        )
        .scale(scale)
        .border(2.dp, borderColor, shape = RoundedCornerShape(8.dp))

    ) {
        Column(
            modifier = Modifier
                .padding(start = 12.dp, top = 20.dp)
                .align(Alignment.TopStart)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.FillBounds
            )
            Text(text = card.title, color = Color.White, modifier = Modifier.padding(start = 16.dp))
            Text(text = "This is a Car card.", color = Color.White, modifier = Modifier.padding(start = 16.dp))
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

class PreviewCardListViewModel : CardListViewModel() {
    override val validIds = MutableStateFlow(listOf(1, 2))
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetpackComposeViewTrackingDemoTheme {
        RecyclerView(PreviewCardListViewModel())
    }
}

