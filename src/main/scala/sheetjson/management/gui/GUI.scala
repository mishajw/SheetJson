package sheetjson.management.gui

import java.awt.event.KeyListener
import javax.swing.JFrame

object GUI {
  private val frame = {
    val f = new JFrame("sheetjson")
    f.setSize(10, 10)
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    f.setContentPane(new View())
    f.setVisible(true)
    f
  }

  def addKeyListener(kl: KeyListener): Unit = {
    frame.addKeyListener(kl)
  }
}
