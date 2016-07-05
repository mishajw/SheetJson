package music2.management.gui

import java.awt.{Color, Graphics}
import java.util.{Observable, Observer}
import javax.swing.JPanel

import music2.management.gui.View._
import music2.player.{Playable, Player}

class View extends JPanel with Observer {

  /**
    * The last time of a repaint
    */
  private var lastUpdated: Option[Long] = None

  // Observe model
  Model addObserver this

  override def paint(g: Graphics): Unit = {
    // Return if not enough time has passed since last paint
    if (!shouldRepaint) return

    // Update to last updated time
    lastUpdated = Some(System.currentTimeMillis())

    // Make graphics implicit TODO: better way of doing this?
    implicit val _g: Graphics = g

    drawBackground()
    drawPlayers()
  }

  /**
    * Draw the players to the screen
    */
  private def drawPlayers()(implicit g: Graphics): Unit = {
    val readings = Model.allReadings

    if (readings.isEmpty) return

    val heightForPlayer: Int = getHeight / readings.size

    readings.toSeq.zipWithIndex.foreach { case ((p, rs), i) =>
      g.setColor(colorForIndex(i))
      drawReadings(p, rs.toSeq, heightForPlayer)
      g.translate(0, heightForPlayer)
    }
  }

  /**
    * Draw the background
    */
  private def drawBackground()(implicit g: Graphics): Unit = {
    g.setColor(new Color(40, 40, 40))
    g.fillRect(0, 0, getWidth, getHeight)
  }

  /**
    * Check if enough time has passed for a repaint
    */
  private def shouldRepaint: Boolean = lastUpdated match {
    case Some(t) if System.currentTimeMillis() - t > updateTime => true
    case None => true
    case _ => false
  }

  /**
    * Draw a set of readings
    * @param player the `Player` where the readings come from
    * @param readings the readings
    * @param height the height of the frame to draw on
    */
  private def drawReadings( player: Player,
                            readings: Seq[Playable],
                            height: Int)(implicit g: Graphics): Unit = {

    def scaleX(i: Int): Int = {
      ((i.toDouble / readings.size.toDouble) * getWidth.toDouble).toInt
    }

    def scaleY(p: Playable): Int = {
      (((p.toDouble + 1) / 2) * height.toDouble).toInt
    }

    // Scale the readings
    val scaledReadings =
      readings.zipWithIndex map {
        case (y, x) => (scaleY(y), scaleX(x))
      }

    // Pair the readings to make lines
    val pairedReadings =
      for (i <- scaledReadings.indices.tail) yield {
        (scaledReadings(i - 1), scaledReadings(i))
      }

    // Draw the paired readings
    pairedReadings.foreach {
      case ((y1, x1), (y2, x2)) =>
        g.drawLine(x1, y1, x2, y2)
    }

    g.drawString(player.toString, 0, 0)
  }

  override def update(observable: Observable, o: scala.Any): Unit = repaint()
}

object View {
  /**
    * How often to update
    */
  private val updateTime = 1000 / 15

  /**
    * List of colors for `Player` visualisations
    */
  private val playerColors = Seq(
    new Color(255, 129, 129),
    new Color(129, 255, 129),
    new Color(129, 129, 255)
  )

  /**
    * @param i the index of a player
    * @return the color to use to draw it
    */
  private def colorForIndex(i: Int) = playerColors(i % playerColors.size)
}
