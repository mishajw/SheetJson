package music2.management.gui

import java.awt.{Color, Graphics}
import java.util.{Observable, Observer}
import javax.swing.JPanel

import music2.player.{Playable, Player}

class View extends JPanel with Observer {

  private var lastUpdated: Option[Long] = None
  private val updateTime = 1000 / 15

  // Observe model
  Model addObserver this

  override def paint(g: Graphics): Unit = {
    // Return if not enough time has passed since last paint
    lastUpdated match {
      case Some(t) if System.currentTimeMillis() - t > updateTime =>
      case None =>
      case _ => return
    }

    lastUpdated = Some(System.currentTimeMillis())

    implicit val _g: Graphics = g

    drawBackground()

    val readings = Model.allReadings

    val heightForPlayer: Int = getWidth / readings.size

    readings.foreach { case (p, rs) =>
      g.translate(0, heightForPlayer)
      drawReadings(p, rs.toSeq, heightForPlayer)
    }

    g.setColor(Color.black)
    g.drawString(Model.allReadings.map { case (p, rs) => rs.size }.toString(), 10, 10)
  }

  private def drawBackground()(implicit g: Graphics): Unit = {
    g.setColor(Color.white)
    g.fillRect(0, 0, getWidth, getHeight)
  }

  private def drawReadings( player: Player,
                            readings: Seq[Playable],
                            height: Int)(implicit g: Graphics): Unit = {

    g.setColor(Color.green)

    def scaleX(i: Int): Int = {
      ((i.toDouble / readings.size.toDouble) * getWidth.toDouble).toInt
    }

    def scaleY(p: Playable): Int = {
      (((p.toDouble + 1) / 2) * height.toDouble).toInt
    }

    val scaledReadings = readings.zipWithIndex map { case (y, x) => (scaleY(y), scaleX(x)) }

    scaledReadings.foreach
      { case (y, x) =>
        g.drawRect(x, y, 1, 1)
      }
  }

  override def update(observable: Observable, o: scala.Any): Unit = repaint()
}
