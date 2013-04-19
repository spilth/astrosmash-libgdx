package org.spilth.astrosmash.gdx;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Astrosmash implements ApplicationListener {
	Texture shipImage;
	Texture asteroidImage;
	Texture missileImage;

	OrthographicCamera camera;
	SpriteBatch batch;
	Rectangle spacecraft;
	Array<Asteroid> asteroids;
	Array<Rectangle> missiles;

	Music spaceMusic;
	Sound explosionSound, missileSound;
	
	long lastAsteroidTime;
	long lastMissileTime;
	
	int score;
	private BitmapFont font;

	@Override
	public void create() {
		shipImage = new Texture(Gdx.files.internal("spacecraft.png"));
		asteroidImage = new Texture(Gdx.files.internal("asteroid.png"));
		missileImage = new Texture(Gdx.files.internal("missile.png"));

		spaceMusic = Gdx.audio.newMusic(Gdx.files.internal("boop.mp3"));
		explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
		missileSound = Gdx.audio.newSound(Gdx.files.internal("laser_blast.wav"));
		
		font = new BitmapFont(Gdx.files.internal("font.fnt"),Gdx.files.internal("font.png"),false);
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1280, 720);
		batch = new SpriteBatch();

		spacecraft = new Rectangle();
		spacecraft.x = 1280 / 2 - 64 / 2;
		spacecraft.y = 16;
		spacecraft.width = 64;
		spacecraft.height = 64;

		asteroids = new Array<Asteroid>();
		missiles = new Array<Rectangle>();
		spawnAsteroid();
		
		spaceMusic.setLooping(true);
	    spaceMusic.play();
	    
	    score = 0;
	}

	private void spawnAsteroid() {
		Asteroid asteroid = new Asteroid();

		asteroids.add(asteroid);
		lastAsteroidTime = TimeUtils.nanoTime();
	}

	private void spawnMissile() {
		Rectangle missile = new Rectangle();
		missile.x = spacecraft.x;
		missile.y = spacecraft.y + 64;
		missile.width = 16;
		missile.height = 32;
		missiles.add(missile);
		missileSound.play(1.0f, 1.0f + MathUtils.random(-0.4f, 0.4f), 0);
		lastMissileTime = TimeUtils.nanoTime();
	}

	@Override
	public void render() {
		if (score < 1000) {
			Gdx.gl.glClearColor(0, 0, 0, 1);
		} else if (score < 5000) {
			Gdx.gl.glClearColor(0, 0, 1.0f, 1);			
		} else if ( score < 20000) {
			Gdx.gl.glClearColor(1.0f, 0, 1.0f, 1);						
		}
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(shipImage, spacecraft.x, spacecraft.y);
		for (Rectangle asteroid : asteroids) {
			batch.draw(asteroidImage, asteroid.x, asteroid.y, asteroid.width, asteroid.height);
		}
		for (Rectangle missile : missiles) {
			batch.draw(missileImage, missile.x, missile.y);
		}
		font.draw(batch, Integer.toString(score), 1024 , 32);
		batch.end();

		if (Gdx.input.isKeyPressed(Keys.LEFT))
			spacecraft.x -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
			spacecraft.x += 200 * Gdx.graphics.getDeltaTime();

		if (Gdx.input.isKeyPressed(Keys.SPACE)
				& TimeUtils.nanoTime() - lastMissileTime > 500000000) {
			spawnMissile();
		}

		if (spacecraft.x < 0)
			spacecraft.x = 0;
		if (spacecraft.x > 1280 - 64)
			spacecraft.x = 1280 - 64;

		if (TimeUtils.nanoTime() - lastAsteroidTime > 1000000000)
			spawnAsteroid();

		Iterator<Asteroid> iter = asteroids.iterator();
		while (iter.hasNext()) {
			Asteroid asteroid = iter.next();
			asteroid.update();

			if (asteroid.y < 0) {
				iter.remove();
				score -= asteroid.points / 2;
			}
			
			if (asteroid.x > 1280)
				iter.remove();
			
			if (asteroid.x < 0 - 64)
				iter.remove();
		}

		Iterator<Rectangle> iter2 = missiles.iterator();
		while (iter2.hasNext()) {
			Rectangle missile = iter2.next();
			missile.y += 200 * Gdx.graphics.getDeltaTime();
			if (missile.y > 768)
				iter2.remove();
			
			iter = asteroids.iterator();
			while(iter.hasNext()) {
				Asteroid asteroid = iter.next();
				if (missile.overlaps(asteroid)) {
					explosionSound.play(1.0f, 1.0f + MathUtils.random(-0.4f, 0.4f), 0);
					iter2.remove();
					iter.remove();
					score += asteroid.points;
				}
			}
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		shipImage.dispose();
		asteroidImage.dispose();
		missileImage.dispose();
		spaceMusic.dispose();
		explosionSound.dispose();
		missileSound.dispose();
		font.dispose();
		batch.dispose();
	}
}
