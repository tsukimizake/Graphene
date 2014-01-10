package unyo.plugin.lmntal

import org.specs2.mutable._
import org.specs2.matcher.Matcher
import org.specs2.specification.Scope

import unyo.util._

class FastMoverSpec extends Specification {

  def beCloseTo(p: Point, margin: Double): Matcher[Point] = beLike { case Point(x, y) =>
    (x must beCloseTo(p.x, margin)) and (y must beCloseTo(p.y, margin))
  }

  "FastMover" should {
    "behave in the same way as DefaultMover" in new Graphs {
      val params = new ForceParams
      for (n <- graph1.allNodes) {
        val f1 = DefaultMover.forceFor(n, params)
        val f2 = FastMover.forceFor(n, params)
        f1 must beCloseTo(f2, 1e-3)
      }
    }
  }

}

trait Graphs extends Scope {
  import unyo.model._

  case class IntID(value: Int) extends ID

  /**
   * LMNtal code:
   * { a(X), b(Y) }, { c(X, Z), { d(Y, Z, W) } }, e(W).
   */
  val graph1 = {
    val root = Node(IntID(0), "")
    val g = new Graph(root)
    val r = Rect(Point(0, 0), Dim(400, 300))
    g.viewBuilder = (n: Node) => {
      new View(Rect(Point.randomPointIn(r), Dim(12, 12)), java.awt.Color.WHITE)
    }

    val a = Node(IntID(1), "a")
    val b = Node(IntID(2), "b")
    val c = Node(IntID(3), "c")
    val d = Node(IntID(4), "d")
    val e = Node(IntID(5), "e")

    val mem1 = Node(IntID(6), "")
    root.addChildNode(mem1)
    mem1.addChildNode(a)
    mem1.addChildNode(b)

    val mem2 = Node(IntID(7), "")
    root.addChildNode(mem2)
    mem2.addChildNode(c)
    mem2.addChildNode(d)

    root.addChildNode(e)

    a.addEdgeTo(Port(IntID(3), 0))
    b.addEdgeTo(Port(IntID(4), 0))
    c.addEdgeTo(Port(IntID(1), 0))
    c.addEdgeTo(Port(IntID(4), 1))
    d.addEdgeTo(Port(IntID(2), 0))
    d.addEdgeTo(Port(IntID(3), 1))
    d.addEdgeTo(Port(IntID(5), 0))
    e.addEdgeTo(Port(IntID(4), 2))

    g
  }
}
