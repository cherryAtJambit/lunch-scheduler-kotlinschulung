package de.e2.misc

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe

// Generische Store Implementierung

typealias Reducer<State, Effect> = (state: State, effect: Effect) -> State

class Store<State, in Effect>(initState: State, val reducer: Reducer<State, Effect>) {
    private var stateHistory: MutableList<State> = mutableListOf(initState)

    val state: State get() = stateHistory.last()
    val history: List<State> get() = stateHistory

    fun dispatch(effect: Effect) {
        stateHistory.add(reducer(state, effect))
    }

    @ExperimentalStdlibApi
    fun goBackInTime(steps: Int) {
        require(stateHistory.size > steps)
        (1..steps).forEach { stateHistory.removeLast() }
    }
}

// Beispiel Klassen

sealed class MyEffect {
    object LoadingStarted : MyEffect()
    data class LoadingFinished(val imageUrls: List<String>) : MyEffect()
    object LoadingFailed : MyEffect()
}

data class MyState(val isLoading: Boolean = false, val data: Data = Data.Empty) {
    sealed class Data {
        object Empty : Data()
        data class Images(val urls: List<String>) : Data()
        object Error : Data()
    }
}

fun myReduce(state: MyState, effect: MyEffect): MyState = when (effect) {
    is MyEffect.LoadingStarted -> state.copy(isLoading = true)
    is MyEffect.LoadingFinished -> state.copy(isLoading = false, data = MyState.Data.Images(effect.imageUrls))
    is MyEffect.LoadingFailed -> state.copy(isLoading = false, data = MyState.Data.Error)
}

@ExperimentalStdlibApi
class FluxBeispiel : StringSpec({
    "Store erzeugen" {
        val store = Store(MyState(), ::myReduce)

        store.state shouldBe MyState()
    }

    "Store dispatch" {
        val store = Store(MyState(), ::myReduce)

        store.dispatch(MyEffect.LoadingStarted)

        store.state shouldBe MyState(isLoading = true)
        store.history.size shouldBe 2
        store.history.shouldContainInOrder(MyState(), MyState(isLoading = true))
    }

    "Store timetravel" {
        val store = Store(MyState(), ::myReduce)

        store.dispatch(MyEffect.LoadingStarted)
        store.dispatch(MyEffect.LoadingFailed)

        store.goBackInTime(2)

        store.state shouldBe MyState()
        store.history.size shouldBe 1
    }
})

