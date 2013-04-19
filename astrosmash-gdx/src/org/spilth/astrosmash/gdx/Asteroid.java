package org.spilth.astrosmash.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

@SuppressWarnings("serial")
public class Asteroid extends Rectangle {
	private int drift;
	public int points;
	
	public Asteroid() {
		x = MathUtils.random(0, 1280 - 64);
		y = 720;
		if (MathUtils.random(1,6) > 4) {
			width = 32;
			height = 32;
			points = 20;
		} else {
			width = 64;
			height = 64;
			points = 10;
		}
		drift = MathUtils.random(-100, 100);
	}
	
	public void update() {
		y -= 100 * Gdx.graphics.getDeltaTime();
		x += drift * Gdx.graphics.getDeltaTime();;
	}
}
