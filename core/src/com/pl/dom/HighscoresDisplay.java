package com.pl.dom;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;


public class HighscoresDisplay implements Screen {
    public Table scrollTable = new Table();
    Skin skin, buttonskin, infoskin;
    TextureAtlas buttons, textureatlas2;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Stage stage = new Stage();
    ImageButton PrevButton;
    public static final String HIGHSCORES_DISPLAY = "http://178.62.64.68/dominator/displayscore.php";

    @Override
    public void show()
    {
        Skin skin = new Skin(Gdx.files.internal("skin.json"));
        buttons = new TextureAtlas(Gdx.files.internal("pack/button.pack"));
        textureatlas2 = new TextureAtlas(Gdx.files.internal("pack/backgrounds.pack"));
        buttonskin = new Skin(buttons);
        infoskin = new Skin(textureatlas2);


        ImageButton.ImageButtonStyle PrevButtonStyle = new ImageButton.ImageButtonStyle();
        PrevButtonStyle.up = buttonskin.getDrawable("Button Previous");
        PrevButtonStyle.down = buttonskin.getDrawable("Button Previous");
        PrevButtonStyle.pressedOffsetX = 1;
        PrevButtonStyle.pressedOffsetY = -1;
        PrevButton = new ImageButton(PrevButtonStyle);

        PrevButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new MainMenu());
            }
        });

        scrollTable.add(new Label("Hall of fame", skin, "default")).padBottom(10).center().colspan(2).row();
        scrollTable.row();

        String jsonStr = "";
        try {
            URL url = new URL(HIGHSCORES_DISPLAY);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                jsonStr = readStream(in);
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e){
            Gdx.app.log( "HighScores", "Could not retrieve HighScores" + e.getMessage());
            jsonStr = "";
        }
        System.out.println("Diplay scores!");
        Json json = new Json();
        ArrayList highScores = json.fromJson(ArrayList.class, jsonStr);

        if (highScores != null){
            StringBuilder sb = null;
            Iterator<JsonValue> it = highScores.iterator();
            int pos = 1;

            while (it.hasNext()){

                JsonValue item = it.next();

                sb = new StringBuilder("");
                sb.append(pos).append(". ");
                sb.append(item.get("Score"));
                sb.append(" - ");
                sb.append(item.get("Name"));

                Label score = new Label( sb.toString(), skin );
                scrollTable.add(score);

                Date date = null;
                try {
                    date = sdf.parse(item.get("date").toString());
                } catch (Exception e){
                    date = null;
                }

                if (date != null){

                    Calendar newDate = Calendar.getInstance();
                    newDate.add(Calendar.DAY_OF_MONTH, -3);

                    if (date.after(newDate.getTime())){

                        Label newScore = new Label("  New", skin );
                        newScore.setColor(Color.RED);
                        scrollTable.add(newScore);
                    }
                }

                scrollTable.row();
                pos++;
            }
        }

        scrollTable.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        scrollTable.setBackground(infoskin.getDrawable("background_transparent"));
        scrollTable.row();
        scrollTable.add(PrevButton).padBottom(10).colspan(2).center().size(80,80);

        stage.addActor(scrollTable);
        Gdx.input.setInputProcessor(stage);
    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        is.close();
        return sb.toString();
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
        buttonskin.dispose();
        infoskin.dispose();
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