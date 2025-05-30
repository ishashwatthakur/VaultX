package com.shashrwat.vault.core.mvi.tea

import com.shashrwat.vault.core.DispatchersFacade
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class TeaStoreImpl<State : Any, Event : Any, UiEvent : Event, Command : Any, News : Any>(
  private val actors: List<Actor<Command, Event>>,
  private val reducer: Reducer<State, Event, Command, News>,
  initialState: State,
) : TeaStore<State, UiEvent, News> {
  
  private val commandsFlow = MutableSharedFlow<Command>(replay = Int.MAX_VALUE)
  
  private val eventsFlow = MutableSharedFlow<Event>(replay = Int.MAX_VALUE)
  
  override val state = MutableStateFlow(initialState)
  override val news = MutableSharedFlow<News>()
  
  override fun launch(coroutineScope: CoroutineScope, dispatchersFacade: DispatchersFacade) {
    actors.forEach { actor ->
      coroutineScope.launch(dispatchersFacade.IO) {
        try {
          actor.handle(commandsFlow)
              .collect(eventsFlow::emit)
        } catch (e: CancellationException) {
          Timber.d(e)
          throw e
        }
      }
    }
    coroutineScope.launch(dispatchersFacade.Default) {
      eventsFlow.collect { event ->
        val currentState = state.value
        val update = reducer.reduce(currentState, event)
        if (update.state != currentState) {
          withContext(dispatchersFacade.Main) {
            state.emit(update.state)
          }
        }
        update.commands.forEach { command ->
          commandsFlow.emit(command)
        }
        withContext(dispatchersFacade.Main) {
          update.news.forEach { newsItem ->
            news.emit(newsItem)
          }
        }
      }
    }
  }
  
  override suspend fun dispatch(event: UiEvent) {
    eventsFlow.emit(event)
  }
  
  override fun tryDispatch(event: UiEvent) {
    eventsFlow.tryEmit(event)
  }
}
