package com.pl.dom;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenu implements Screen{
    private Music music;
    public Stage info;
    private Toast render_toast = new Toast(7, 6);
	SpriteBatch batch = new SpriteBatch();
	Stage stage = new Stage();
    Table table = new Table();
    Table infotable = new Table();
    TextureAtlas textureatlas, textureatlas2;
    Skin skin, infoskin, tableskin;
    Texture menuBackgndTexure;
    Sprite menuBackSprite;
    Slider slider;

    ImageButton PlayButton, ExitButton, InfoButton, HelpButton, PrevButton;

    private enum State{
        Running, Paused
    }

    private State state = State.Running;


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {

    	music = Gdx.audio.newMusic(Gdx.files.internal("sounds/main_sound.mp3"));
        music.setLooping(true);
        music.play();

    	textureatlas = new TextureAtlas(Gdx.files.internal("pack/button.pack"));
        textureatlas2 = new TextureAtlas(Gdx.files.internal("pack/backgrounds.pack"));
    	skin = new Skin(textureatlas);
    	info = new Stage();
        infoskin = new Skin(textureatlas2);
        tableskin = new Skin(Gdx.files.internal("skin.json"));


    	ImageButtonStyle PlayButtonStyle = new ImageButtonStyle();
    	PlayButtonStyle.up = skin.getDrawable("Button Next");
    	PlayButtonStyle.down = skin.getDrawable("Button Next");
    	PlayButtonStyle.pressedOffsetX = 1;
    	PlayButtonStyle.pressedOffsetY = -1;	
    	PlayButton = new ImageButton(PlayButtonStyle);
    	
    	ImageButtonStyle ExitButtonStyle = new ImageButtonStyle();		
    	ExitButtonStyle.up = skin.getDrawable("Button Close");
    	ExitButtonStyle.down = skin.getDrawable("Button Close");
    	ExitButtonStyle.pressedOffsetX = 1;
    	ExitButtonStyle.pressedOffsetY = -1;
    	ExitButton = new ImageButton(ExitButtonStyle);

        ImageButtonStyle InfoButtonStyle = new ImageButtonStyle();
        InfoButtonStyle.up = skin.getDrawable("Button Info");
        InfoButtonStyle.down = skin.getDrawable("Button Info");
        InfoButtonStyle.pressedOffsetX = 1;
        InfoButtonStyle.pressedOffsetY = -1;
        InfoButton = new ImageButton(InfoButtonStyle);

        ImageButtonStyle PrevButtonStyle = new ImageButtonStyle();
        PrevButtonStyle.up = skin.getDrawable("Button Previous");
        PrevButtonStyle.down = skin.getDrawable("Button Previous");
        PrevButtonStyle.pressedOffsetX = 1;
        PrevButtonStyle.pressedOffsetY = -1;
        PrevButton = new ImageButton(PrevButtonStyle);

        ImageButtonStyle HelpButtonStyle = new ImageButtonStyle();
        HelpButtonStyle.up = skin.getDrawable("Button Help");
        HelpButtonStyle.down = skin.getDrawable("Button Help");
        HelpButtonStyle.pressedOffsetX = 1;
        HelpButtonStyle.pressedOffsetY = -1;
        HelpButton = new ImageButton(HelpButtonStyle);

    	menuBackgndTexure = new Texture(Gdx.files.internal("title.png"));
    	menuBackSprite = new Sprite(menuBackgndTexure);
    	menuBackSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    	
    	 PlayButton.addListener(new ClickListener(){
             @Override
             public void clicked(InputEvent event, float x, float y) {
                 //render_toast.makeText("Starting game...", "font", Toast.COLOR_PREF.WHITE, Toast.STYLE.NORMAL, Toast.TEXT_POS.middle_right, Toast.TEXT_POS.middle_down, Toast.MED);
                 ((Game)Gdx.app.getApplicationListener()).setScreen(new TheGame());
             }
         });
    	 
    	 ExitButton.addListener(new ClickListener(){
             @Override
             public void clicked(InputEvent event, float x, float y) {
                 render_toast.makeText("Exiting...", "font", Toast.COLOR_PREF.GREEN, Toast.STYLE.ROUND, Toast.TEXT_POS.middle_right, Toast.TEXT_POS.middle_down, Toast.SHORT);
                 Gdx.app.exit();
             }
         });


        InfoButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new HighscoresDisplay());
                render_toast.makeText("Highscores...", "font", Toast.COLOR_PREF.GREEN, Toast.STYLE.ROUND, Toast.TEXT_POS.middle_right, Toast.TEXT_POS.middle_down, Toast.SHORT);

            }
        });

        PrevButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resume();
            }
        });


        HelpButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pause();
                render_toast.makeText("Informations...", "font", Toast.COLOR_PREF.GREEN, Toast.STYLE.ROUND, Toast.TEXT_POS.middle_right, Toast.TEXT_POS.middle_down, Toast.SHORT);

            }
        });

         table.padTop(30);
         table.add(PlayButton).size(79,79).padBottom(10).padRight(20);
         table.add(ExitButton).size(79,79).padBottom(10).padLeft(20);
         table.row();
         table.add(InfoButton).size(79,79).padBottom(10).padRight(20);
         table.add(HelpButton).size(79,79).padBottom(10).padLeft(20);

         infotable.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
         infotable.setBackground(infoskin.getDrawable("background_transparent"));
         infotable.add(new Label("Dominator", tableskin)).padBottom(10).center().colspan(2).row();
         infotable.add(new Label("This game represents graph domination algorithm", tableskin)).padBottom(10).center().colspan(2).row();
         infotable.add(new Label("in a funny way! Wifi or mobile broadband", tableskin)).padBottom(10).center().colspan(2).row();
         infotable.add(new Label("connection is recommended for maximal user experience.", tableskin)).padBottom(10).center().colspan(2).row();
         infotable.add(new Label("Have a good time!", tableskin)).padBottom(10).center().colspan(2).row();
         infotable.add(PrevButton).padBottom(10).colspan(2).center().size(80,80);

         table.setFillParent(true);

         info.addActor(infotable);
         stage.addActor(table);     
         
         Gdx.input.setInputProcessor(stage);
         Gdx.input.setCatchBackKey(true);
    }


    @Override
    public void render(float delta) {
        switch(state){
            case Running:
                update();
                break;
            case Paused:
                paused();
                break;
        }

    }

    private void update(){
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        menuBackSprite.draw(batch);
        batch.end();

        render_toast.toaster();

        stage.act();
        stage.draw();

    }


    private void paused(){
        info.act();
        info.draw();
    }

    @Override
    public void hide() {
        dispose();
    }

   @Override
   public void pause() {
       Gdx.input.setInputProcessor(info);
       this.state = State.Paused;
   }

   @Override
   public void resume() {
       Gdx.input.setInputProcessor(stage);
       this.state = State.Running;
   }

   @Override
   public void dispose() {
       stage.dispose();
       skin.dispose();
   }
}