package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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

public class GameScreen implements Screen {
   private OrthographicCamera camera;
    private Array<Rectangle> raindrops;
    private long lastDropTime;
    private  SpriteBatch batch;
    private Texture dropImage;
    private Texture bucketImage;
    private Sound dropSound;
    private Music rainMusic;
    private Rectangle bucket;
    private Rectangle raindrop;
    private Drop game;
    private int dropsGathered;

    GameScreen (Drop game){
        this.game = game;

        dropImage = new Texture("droplet.png");
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, CONSTANTS.WIDTH, CONSTANTS.HEIGHT);



        bucket = new Rectangle();
        bucket.x = CONSTANTS.WIDTH/ 2 - CONSTANTS.BUCKETWIDTH / 2;
        bucket.y = 20;
        bucket.width = CONSTANTS.BUCKETWIDTH;
        bucket.height = CONSTANTS.BUCKETHEIGHT;

        raindrops = new Array<Rectangle>();
        spawnRaindrop();
    }

    @Override
    public void show() {
        rainMusic.setLooping(true);
        rainMusic.play();
    }

    private void spawnRaindrop() {
        raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, CONSTANTS.WIDTH - CONSTANTS.BUCKETWIDTH);
        raindrop.y = CONSTANTS.HEIGHT;
        raindrop.width = CONSTANTS.BUCKETWIDTH;
        raindrop.height = CONSTANTS.BUCKETHEIGHT;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Додждинок собрано: " + dropsGathered, 0, CONSTANTS.HEIGHT);
        game.batch.draw(bucketImage, bucket.x, bucket.y);

        for(Rectangle raindrop: raindrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }

        game.batch.end();

        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - CONSTANTS.BUCKETWIDTH / 2;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucket.x -= CONSTANTS.BUCKERSPEED* Gdx.graphics.getDeltaTime();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucket.x += CONSTANTS.BUCKERSPEED* Gdx.graphics.getDeltaTime();
        }

        if (bucket.x < 0) {
            bucket.x = 0;
        }

        if (bucket.x > CONSTANTS.WIDTH - CONSTANTS.BUCKETWIDTH) {
            bucket.x = CONSTANTS.WIDTH - CONSTANTS.BUCKETWIDTH;
        }

        if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
            spawnRaindrop();

        Iterator<Rectangle> iter = raindrops.iterator();

        while (iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= CONSTANTS.BUCKERSPEED * Gdx.graphics.getDeltaTime();


            if (raindrop.overlaps(bucket)) {
                dropsGathered++;
                dropSound.play();
                iter.remove();
            }
            if (raindrop.y + 64 < 0) {
                iter.remove();
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
    public void hide() {

    }

    @Override
    public void dispose() {
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }
}
