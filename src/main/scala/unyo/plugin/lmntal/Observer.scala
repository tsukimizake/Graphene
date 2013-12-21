package unyo.plugin.lmntal

import unyo.swing.scalalike._

import unyo.util._
import unyo.util.Geometry._

import unyo.model._

class Observer extends LMNtal.Observer {

  import java.awt.event.{KeyEvent}

  private def viewOptAt(wp: Point): Option[View] = {
    val graph = LMNtal.source.current
    graph.rootNode.allChildNodes.filter(_.childNodes.isEmpty).find(_.view.rect.contains(wp)).map(_.view)
  }

  var view: View = null
  var isNodeHandlable = false
  def listenOn(context: unyo.core.gui.GraphicsContext): Reactions.Reaction = {
    case MousePressed(_, p, _, _, _) => if (isNodeHandlable) {
      val pos = context.worldPointFrom(p)
      viewOptAt(pos) match {
        case Some(v) => { view = v; v.fixed = true }
        case None =>
      }
    }
    case MouseReleased(_, p, _, _, _) => if (isNodeHandlable) {
      view.fixed = false
      view = null
    }
    case MouseDragged(_, p, _) => if (isNodeHandlable) {
      if (view != null) {
        val wp = context.worldPointFrom(p)
        view.rect = Rect(wp, view.rect.dim)
      }
    }
    case KeyPressed(_, key, _, _) => if (key == KeyEvent.VK_Z) isNodeHandlable = true
    case KeyReleased(_, key, _, _) => if (key == KeyEvent.VK_Z) isNodeHandlable = false
    case _ =>
  }

    def canMoveScreen = !isNodeHandlable
}
