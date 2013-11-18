package unyo.plugin.lmntal

import java.awt.{Graphics,Graphics2D,Color}

import unyo.utility._
import unyo.utility.Geometry._
import unyo.gui.GraphicsContext

trait Renderer {
  implicit class GraphicsExt(val g: Graphics) {
    def drawLine(x1: Double, y1: Double, x2: Double, y2: Double) {
      g.drawLine(x1.toInt, y1.toInt, x2.toInt, y2.toInt)
    }
    def drawLine(p1: Point, p2: Point) {
      drawLine(p1.x, p1.y, p2.x, p2.y)
    }

    def fillOval(x: Double, y: Double, w: Double, h: Double) {
      g.fillOval(x.toInt, y.toInt, w.toInt, h.toInt)
    }
    def fillOval(p: Point, w: Double, h: Double) {
      fillOval(p.x, p.y, w, h)
    }
    def fillOval(p: Point, d: Dim) {
      fillOval(p, d.width, d.height)
    }
    def fillOval(r: Rect) {
      fillOval(r.point, r.dim)
    }
    def clearRect(r: Rect) {
      g.clearRect(r.point.x.toInt, r.point.y.toInt, r.dim.width.toInt, r.dim.height.toInt)
    }
    def drawRect(r: Rect) {
      g.drawRect(r.point.x.toInt, r.point.y.toInt, r.dim.width.toInt, r.dim.height.toInt)
    }
    def fillRect(r: Rect) {
      g.fillRect(r.point.x.toInt, r.point.y.toInt, r.dim.width.toInt, r.dim.height.toInt)
    }

    def drawString(s: String, p: Point) {
      g.drawString(s, p.x.toInt, p.y.toInt)
    }
  }
}

class DefaultRenderer extends LMNtalPlugin.Renderer with Renderer {

  import unyo.utility.model._

  var g: Graphics2D = null
  var gctx: GraphicsContext = null
  var vctx: ViewContext = null
  def renderAll(gg: Graphics, gctx: GraphicsContext, vctx: ViewContext) {
    g = gg.asInstanceOf[Graphics2D];
    this.gctx = gctx
    this.vctx = vctx

    renderGrid

    if (vctx == null) return
    if (vctx.graph == null) return

    for (node <- vctx.childNodesOf(vctx.graph.rootNode)) renderEdges(node)
    for (node <- vctx.childNodesOf(vctx.graph.rootNode)) renderNode(node)
  }

  def renderGrid {
    val bx = gctx.wCenter.x - gctx.wSize.width / 2
    val ex = gctx.wCenter.x + gctx.wSize.width / 2
    val by = gctx.wCenter.y - gctx.wSize.height / 2
    val ey = gctx.wCenter.y + gctx.wSize.height / 2
    g.setColor(new Color(127, 140, 141))
    for (x <- (bx.toInt/100*100).to(ex.toInt/100*100, 100)) {
      val p1 = Point(x, by)
      val p2 = Point(x, ey)
      g.drawLine(p1, p2)
    }

    for (y <- (by.toInt/100*100).to(ey.toInt/100*100, 100)) {
      val p1 = Point(bx, y)
      val p2 = Point(ex, y)
      g.drawLine(p1, p2)
    }
  }

  def renderNode(node: Node) {
    // if (node.isProxy) return

    val viewNode = vctx.viewOf(node)
    val rect = viewNode.rect

    if (viewNode.willDisappear) {
      val oldPaint = g.getPaint
      g.setPaint(new java.awt.RadialGradientPaint(rect.center.x.toInt, rect.center.y.toInt, 30, Array(0.0f, 1.0f), Array(Color.RED, new Color(255, 255, 255, 0))))
      g.fillOval(rect.pad(Padding(-30, -30, -30, -30)))
      g.setPaint(oldPaint)
    }

    node.attribute match {
      case Atom() => {
        g.setFont(new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 16))
        g.setColor(new Color(52, 152, 219))
        g.drawString(node.name, rect.point)
        g.fillOval(rect)
        g.setColor(Color.WHITE)
        g.fillOval(rect.pad(Padding(3, 3, 3, 3)))
      }
      case HLAtom() => {
        g.setFont(new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 16))
        g.setColor(new Color(52, 152, 219))
        g.drawString(node.name, rect.point)
        g.fillOval(rect)
      }
      case Mem() => {
        g.setColor(new Color(52, 152, 219))
        g.fillRect(rect)
        g.setColor(Color.WHITE)
        g.fillRect(rect.pad(Padding(3, 3, 3, 3)))
      }
      case _ =>
    }

    for (n <- vctx.childNodesOf(node)) renderEdges(n)
    for (n <- vctx.childNodesOf(node)) renderNode(n)
  }

  def renderEdges(node: Node) {
    // if (node.isProxy) return

    val view1 = vctx.viewOf(node)
    g.setColor(new Color(41, 128, 185))
    for (i <- 0 until vctx.neighborNodesOf(node).size) {
      var buddy = vctx.neighborNodesOf(node)(i)
      val view2 = vctx.viewOf(buddy)

      g.drawLine(
        view1.rect.center,
        view2.rect.center
      )
    }
  }
}
