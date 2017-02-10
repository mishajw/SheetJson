package sheetjson.management.gui

import java.awt.{Color, Graphics}
import java.util.{Observable, Observer}
import javax.swing.JPanel

import sheetjson.management.gui.View._
import sheetjson.player.{Playable, Player}
import sheetjson.util.Config

import scala.util.Random

class View(controller: Controller) extends JPanel with Observer {

  /**
    * The last time of a repaint
    */
  private var lastUpdated: Option[Long] = None

  // Observe model
  controller addObserver this

  private val readingsToShow = 1000

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
    val readings = controller.allReadings
      .filter { case (p, _) => p.spec.visible }

    if (readings.isEmpty) return

    val trimmedReadings = readings
      .map { case (p, ps) => ps synchronized {
        (p, ps.drop(ps.size - Config.displayAmount))
      }}

    val heightForPlayer: Int = getHeight / trimmedReadings.size

    trimmedReadings.toSeq.foreach { case (p, rs) =>
      g.setColor(colorForPlayer(p))
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
 *
    * @param player the `Player` where the readings come from
    * @param readings the readings
    * @param height the height of the frame to draw on
    */
  private def drawReadings( player: Player,
                            readings: Seq[Playable],
                            height: Int)(implicit g: Graphics): Unit = {

    g.drawString(s"${player.identifier}(${player.displayParameters.mkString(", ")})", 0, 20)

    if (readings.isEmpty) return

    def scaleX(i: Int): Int = {
      ((i.toDouble / readings.size.toDouble) * getWidth.toDouble).toInt
    }

    def scaleY(p: Playable): Int = {
      height - (((p.toDouble + 1) / 2) * height.toDouble).toInt
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
  }

  override def update(observable: Observable, o: scala.Any): Unit = repaint()
}

object View {
  /**
    * How often to update
    */
  private val updateTime = 1000 / 30

  /**
    * List of colors for `Player` visualisations
    */
  private val playerColors = Seq(
    new Color(255, 129, 129),
    new Color(129, 255, 129),
    new Color(129, 129, 255)
  )

  private def colorForPlayer(player: Player) = {
    val defaultColor = playerColors(player.path.size % playerColors.size)

    val random = new Random(player.hashCode())

    def randomize(i: Int): Int = Math.min(255, i + ((random.nextDouble() -0.5) * 75)).toInt

    new Color(
      randomize(defaultColor.getRed),
      randomize(defaultColor.getGreen),
      randomize(defaultColor.getBlue))
  }
}
