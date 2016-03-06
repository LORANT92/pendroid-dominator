package com.pl.dom;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

public class HighscoresAdd implements Screen {
    public Table table = new Table();
    int score = 0;
    Texture gameover;
    TextureAtlas textureatlas, buttons;
    Stage stage = new Stage();
    Skin tableskin, infoskin, skin;
    public TextField nameText;
    public String name;
    Label scoreLabel;
    Table infotable = new Table();
    ImageButton PrevButton, AddButton;
    Image gameover_image;

    public static final String HIGHSCORES_ADD = "http://178.62.64.68/dominator/addscore.php";
    public static final String PARAM_NAME = "?name=";
    public static final String PARAM_SCORE = "&score=";
    public static final String PARAM_HASH = "&hash=";
    public static final String HASH_SALT = "mySecretKey";

    @Override
    public void show()
    {
        gameover = new Texture(Gdx.files.internal("gameover.png"));
        Image gameover_image = new Image(gameover);
        gameover_image.setSize(233, 174);
        textureatlas = new TextureAtlas(Gdx.files.internal("pack/backgrounds.pack"));
        tableskin = new Skin(Gdx.files.internal("uiskin.json"));
        infoskin = new Skin(textureatlas);
        buttons = new TextureAtlas(Gdx.files.internal("pack/button.pack"));
        skin = new Skin(buttons);
        nameText = new TextField("", tableskin, "default");
        nameText.setSize(300, 100);

        score = TheGame.score;
        System.out.println("KÜLDENDŐ SCORE: " + score);

        ImageButton.ImageButtonStyle PrevButtonStyle = new ImageButton.ImageButtonStyle();
        PrevButtonStyle.up = skin.getDrawable("Button Previous");
        PrevButtonStyle.down = skin.getDrawable("Button Previous");
        PrevButtonStyle.pressedOffsetX = 1;
        PrevButtonStyle.pressedOffsetY = -1;
        PrevButton = new ImageButton(PrevButtonStyle);

        ImageButton.ImageButtonStyle AddButtonStyle = new ImageButton.ImageButtonStyle();
        AddButtonStyle.up = skin.getDrawable("Button Add");
        AddButtonStyle.down = skin.getDrawable("Button Add");
        AddButtonStyle.pressedOffsetX = 1;
        AddButtonStyle.pressedOffsetY = -1;
        AddButton = new ImageButton(AddButtonStyle);

        PrevButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new MainMenu());
            }
        });


        AddButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String hash = "";
                String name = nameText.getText();
                try{
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    md.update((name + score + HASH_SALT).getBytes());
                    byte byteData[] = md.digest();

                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < byteData.length; i++) {
                        sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
                    }
                    hash = sb.toString();
                } catch (Exception e) {
                    hash = "";
                }


                try {
                    String urlParameters = PARAM_NAME + name + PARAM_SCORE + score + PARAM_HASH + hash;

                    URL url = new URL(HIGHSCORES_ADD + urlParameters);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.getInputStream();
                    urlConnection.disconnect();
                } catch (Exception e){
                    Gdx.app.log( "HighScores", "Could not submit score" + e.getMessage());
                } finally {
                    ((Game)Gdx.app.getApplicationListener()).setScreen(new MainMenu());
                }

            }
        });

        infotable.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        infotable.setBackground(infoskin.getDrawable("background_transparent"));
        infotable.add(gameover_image).center().colspan(2).row();
        //infotable.add(new Label("This is the end!", tableskin, "default")).padBottom(10).center().colspan(2).row();
        System.out.println("SCORE: " + String.valueOf(score));
        infotable.add(new Label("Your score:  " + String.valueOf(score), tableskin)).padBottom(10).center().colspan(2).row();
        infotable.add(new Label("Elapsed time:  " + String.valueOf((System.currentTimeMillis() - TheGame.StartTime) / 1000), tableskin)).padBottom(10).center().colspan(2).row();
        infotable.add(nameText).padBottom(10).colspan(2).center().size(300, 50).row();
        infotable.add(PrevButton).size(79,79).padBottom(10).padRight(20);
        infotable.add(AddButton).size(79,79).padBottom(10).padLeft(20);

        stage.addActor(infotable);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void resize(int width, int height) {
    }
}
