package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;


public class Drop extends ApplicationAdapter {
	OrthographicCamera camera;
	Array<Rectangle> raindrops;
	long lastDropTime;
	SpriteBatch batch;
	Texture dropImage;
	Texture bucketImage;
	Sound dropSound;
	Music rainMusic;
	Rectangle bucket;
	Rectangle rain;
	Texture img;
    Rectangle raindrop;

	@Override
	public void create () {
		raindrops = new Array<Rectangle>();
		spawnRaindrop();
		camera = new	 OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();
		dropImage = new Texture("droplet.png");
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);
        rainMusic.play();

		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;
	}

	private void spawnRaindrop() {
		raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800-64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void render () {
	    Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();
		rain = new Rectangle();
		Iterator<Rectangle> iterator = raindrops.iterator();

		while(iterator.hasNext()) {
			Rectangle raindrop = iterator.next();
			if(raindrop.overlaps(bucket)) {
				iterator.remove();
			}
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(raindrop.y + 64 < 0) iterator.remove();
		}

		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);

		for(Rectangle raindrop: raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		batch.end();



		if(Gdx.input.isTouched()) {
			Vector3 vectorTouchPos = new Vector3();
			vectorTouchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(vectorTouchPos);
			bucket.x = vectorTouchPos.x - 64 / 2;
		}

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
