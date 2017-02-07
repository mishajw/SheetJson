package sheetjson.management.gui

import java.awt.event.KeyListener
import javax.swing.JFrame

class GUI(controller: Controller) {
  private val frame = {
    val f = new JFrame("sheetjson")
    f.setSize(800, 600)
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    f.setContentPane(new View(controller))
    f.setVisible(true)
    f
  }

  def addKeyListener(kl: KeyListener): Unit = {
    frame.addKeyListener(kl)
  }
}
