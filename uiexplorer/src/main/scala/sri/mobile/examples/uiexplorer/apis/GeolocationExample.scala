package sri.mobile.examples.uiexplorer.apis

import org.scalajs.dom.raw.{Position, PositionError, PositionOptions}
import org.scalajs.dom.window
import sri.core._
import sri.mobile.examples.uiexplorer.components.UIExample
import sri.universal.components._
import sri.universal.styles.InlineStyleSheetUniversal

import scala.scalajs.js
import scala.scalajs.js.Dynamic.{literal => json}
import scala.scalajs.js.{JSON, undefined, UndefOr => U}

object GeolocationExample extends UIExample {

  case class State(initialPosition: String = "unknown",
                   lastPosition: String = "unknown")

  class Component extends ComponentS[State] {

    initialState(State())

    def render() = {
      ViewC(
        TextC(
          Text(style = styles.title)("Initial position:"),
          state.initialPosition
        ),
        TextC(
          Text(style = styles.title)("Current position:"),
          state.lastPosition
        )
      )
    }

    var watchId: js.UndefOr[Int] = undefined

    override def componentDidMount(): Unit = {
      val positionOptions =
        json(enableHighAccuracy = true, timeout = 20000, maximumAge = 1000)

      window.navigator.geolocation.getCurrentPosition(
        (position: Position) =>
          setState((state: State) =>
            state.copy(initialPosition = JSON.stringify(position))),
        (error: PositionError) => window.alert(error.message),
        positionOptions.asInstanceOf[PositionOptions]
      )

      watchId =
        window.navigator.geolocation.watchPosition((position: Position) => {
          setState((state: State) =>
            state.copy(lastPosition = JSON.stringify(position)))

        })
    }

    override def componentWillUnmount(): Unit = {
      if (watchId.isDefined)
        window.navigator.geolocation.clearWatch(watchId.get)
    }
  }

  object styles extends InlineStyleSheetUniversal {
    import dsl._
    val title = style(fontWeight := "500")
  }

  override val component = () => CreateElementNoProps[Component]()

  override def title: String = "Geolocation"

  override def description: String = "Examples of using the Geolocation API."
}
