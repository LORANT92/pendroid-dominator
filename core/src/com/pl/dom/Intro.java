package com.pl.dom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Intro implements Screen{
	
	private SpriteBatch batch;
	private Sprite splash;
	private float alpha;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(alpha < 1)
			alpha += delta/2;
		else
			((Game)Gdx.app.getApplicationListener()).setScreen(new MainMenu());
		
		splash.setAlpha(alpha);
		
		batch.begin();
		splash.draw(batch);
		batch.end();
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		batch = new SpriteBatch();
		
		Texture splashTexture = new Texture("pendroidlogo.png");
		
		splash = new Sprite(splashTexture);
		splash.setSize(Gdx.graphics.getHeight(),Gdx.graphics.getHeight());
		splash.setPosition((Gdx.graphics.getWidth()-Gdx.graphics.getHeight())/2,0);

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}