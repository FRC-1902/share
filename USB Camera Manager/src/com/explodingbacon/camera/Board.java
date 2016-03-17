package com.explodingbacon.camera;

import org.opencv.videoio.Videoio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class Board extends JPanel implements ActionListener {

    private static BufferedImage i = null;
    private Timer t;

    public Board() {
        t = new Timer(25, this);
        t.start();
    }

    public void paint(Graphics graphics) {
        super.paint(graphics);
        Graphics2D g = (Graphics2D) graphics;

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, getWidth(), getHeight());

        if (i != null) {
            g.drawImage(i, 0, 0, null);
        }

        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public static void setImage(BufferedImage n) {
        i = n;
    }
}
