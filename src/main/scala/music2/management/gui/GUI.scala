package music2.management.gui

import java.awt.event.KeyListener
import javax.swing.JFrame

object GUI {
  private val frame = {
    val f = new JFrame("music2")
    f.setSize(10, 10)
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    f.setVisible(true)
    f.setContentPane(new View())
    f
  }

  def addKeyListener(kl: KeyListener): Unit = {
    frame.addKeyListener(kl)
  }
}
