package main.java.gridStructure;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.Objects;

import javax.swing.border.Border;

/**
 * 
 * @author https://stackoverflow.com/questions/423950/rounded-swing-jbutton-using-java
 *
 */
public class RoundedBorder implements Border {

	private int radius;

	RoundedBorder(int radius) {
		this.radius = radius;
	}

	@Override
	public Insets getBorderInsets(Component c) {
		Objects.requireNonNull(c);
		return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Objects.requireNonNull(c);
		Objects.requireNonNull(g);
		g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
	}
}