package com.pl.dom;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import controllers.InputController;

public class TheGame implements Screen {
    private Music drop, tap, wrongtap, shutdown;
    private SpriteBatch batch;
    private SpriteBatch batch2;
    private Texture img;
    private Texture img_y;
    private Texture img_d;
    private Texture img_s;
    private Texture uparrowimg;
    private Texture menubuttonimg;
    private ShapeRenderer sr;
    private OrthographicCamera camera;
    public static Texture backgroundTexture;
    public static Sprite backgroundSprite;
    public Skin skin;
    public Skin tableskin;
    public Skin dialogskin;
    public Skin buttonskin;
    private BitmapFont font;
    public Stage stage, dialog;
    ImageButton yes, no, ExitButton;
    ImageButton PlayMoveButton;
    ImageButton RefreshButton, LastButton;
    TextureAtlas textureatlas, textureatlas2, button2;

    private int moving = 1;
    public static int score = 0, score2 = 0;

    public List<Point> nodes;
    public List<Edge> edges;

    private enum State {
        Running, Paused
    }

    private State state = State.Running;
    private InputController inputController;
    private Toast render_toast = new Toast(7, 6);
    Table table = new Table();
    public Label scorelabel;
    public Sprite uparrow;
    public Sprite menubutton;
    public static long StartTime = System.currentTimeMillis();

    private static final int VIEWPORT_WIDTH = 600;
    private static final int VIEWPORT_HEIGHT = 480;
    static final int STANDARD_SIZE = 49;
    static final int STANDARD_SIZE_LITTLE = 25;
    static final int NODE_RADIUS = 30;
    static final int NODE_RADIUS_BIG = 30;


    private void setupCamera() {
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f);
        camera.update();
    }

    private void setupCamera2() {
        camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        camera.position.set(camera.viewportWidth / 2 - ((VIEWPORT_WIDTH - 400) / 2), camera.viewportHeight / 2 - ((VIEWPORT_HEIGHT - 400) / 2), 0f);
        camera.update();
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        batch2 = new SpriteBatch();
        sr = new ShapeRenderer();
        backgroundTexture = new Texture("background.jpg");
        backgroundSprite = new Sprite(backgroundTexture);

        drop = Gdx.audio.newMusic(Gdx.files.internal("sounds/drop.mp3"));
        tap = Gdx.audio.newMusic(Gdx.files.internal("sounds/tap.mp3"));
        wrongtap = Gdx.audio.newMusic(Gdx.files.internal("sounds/wrongtap.mp3"));
        shutdown = Gdx.audio.newMusic(Gdx.files.internal("sounds/shutdown.mp3"));

        img = new Texture(Gdx.files.internal("circle_x.png"));
        img_y = new Texture(Gdx.files.internal("circle_y.png"));
        img_d = new Texture(Gdx.files.internal("circle_d.png"));
        img_s = new Texture(Gdx.files.internal("circle_s.png"));
        uparrowimg = new Texture(Gdx.files.internal("uparrow.png"));
        menubuttonimg = new Texture(Gdx.files.internal("menubutton.png"));

        nodes = poissonDisc(400, 400, 100);
        edges = simpleGraph(nodes);

        System.out.println("nodes:");
        System.out.println("------");
        for (Point p : nodes) System.out.println(p);
        System.out.println("------");
        System.out.println("edges");
        System.out.println("-----");
        for (Edge e: edges) System.out.println(e);


        uparrow = new Sprite(uparrowimg);
        menubutton = new Sprite(menubuttonimg);
        uparrow.setSize(STANDARD_SIZE, STANDARD_SIZE);
        menubutton.setSize(STANDARD_SIZE, STANDARD_SIZE);

        stage = new Stage();
        dialog = new Stage();
        font = new BitmapFont(Gdx.files.internal("font/font.fnt"), false);

        textureatlas = new TextureAtlas(Gdx.files.internal("pack/button.pack"));
        button2 = new TextureAtlas(Gdx.files.internal("pack/buttonpack2.pack"));
        textureatlas2 = new TextureAtlas(Gdx.files.internal("pack/backgrounds.pack"));
        skin = new Skin(textureatlas);
        buttonskin = new Skin(button2);
        dialogskin = new Skin(textureatlas2);
        tableskin = new Skin(Gdx.files.internal("skin.json"));

        scorelabel = new Label("Score:", tableskin, "default");
        scorelabel.addAction(Actions.sequence(Actions.fadeOut(2f), Actions.fadeIn(2f)));
        scorelabel.setWrap(false);
        scorelabel.setWidth(100);
        scorelabel.setPosition(0, Gdx.graphics.getHeight() - (float) Math.sqrt(Gdx.graphics.getHeight()) * 3);
        scorelabel.setText("Game not started");

        ImageButtonStyle ExitButtonStyle = new ImageButtonStyle();
        ExitButtonStyle.up = skin.getDrawable("Button Close");
        ExitButtonStyle.down = buttonskin.getDrawable("close_gray");
        ExitButtonStyle.pressedOffsetX = 1;
        ExitButtonStyle.pressedOffsetY = -1;
        ExitButton = new ImageButton(ExitButtonStyle);
        ExitButton.setSize(STANDARD_SIZE, STANDARD_SIZE);
        ExitButton.setPosition(Gdx.graphics.getWidth() - (float) Math.sqrt(Gdx.graphics.getWidth()) * 3, Gdx.graphics.getHeight() - (float) Math.sqrt(Gdx.graphics.getHeight()) * 3);

        ImageButtonStyle NoButtonStyle = new ImageButtonStyle();
        NoButtonStyle.up = skin.getDrawable("Button Previous");
        NoButtonStyle.down = skin.getDrawable("Button Previous");
        NoButtonStyle.pressedOffsetX = 1;
        NoButtonStyle.pressedOffsetY = -1;
        no = new ImageButton(NoButtonStyle);
        no.setSize(STANDARD_SIZE_LITTLE, STANDARD_SIZE_LITTLE);

        ImageButtonStyle YesButtonStyle = new ImageButtonStyle();
        YesButtonStyle.up = skin.getDrawable("Button Next");
        YesButtonStyle.down = skin.getDrawable("Button Next");
        YesButtonStyle.pressedOffsetX = 1;
        YesButtonStyle.pressedOffsetY = -1;
        yes = new ImageButton(YesButtonStyle);
        yes.setSize(STANDARD_SIZE_LITTLE, STANDARD_SIZE_LITTLE);

/*
        ImageButtonStyle PlayMoveButtonStyle = new ImageButtonStyle();
        PlayMoveButtonStyle.up = skin.getDrawable("Button Play");
        PlayMoveButtonStyle.down = buttonskin.getDrawable("play_gray");
        PlayMoveButtonStyle.pressedOffsetX = 1;
        PlayMoveButtonStyle.pressedOffsetY = -1;
        PlayMoveButton = new ImageButton(PlayMoveButtonStyle);
        PlayMoveButton.setSize(STANDARD_SIZE, STANDARD_SIZE);
        PlayMoveButton.setPosition(Gdx.graphics.getWidth() - (float) Math.sqrt(Gdx.graphics.getWidth()) * 3, Gdx.graphics.getHeight() - (float) Math.sqrt(Gdx.graphics.getHeight()) * 3 - 100);
*/

        ImageButtonStyle RefreshButtonStyle = new ImageButtonStyle();
        RefreshButtonStyle.up = skin.getDrawable("Button Refresh");
        RefreshButtonStyle.down = buttonskin.getDrawable("refresh_gray");
        RefreshButtonStyle.pressedOffsetX = 1;
        RefreshButtonStyle.pressedOffsetY = -1;
        RefreshButton = new ImageButton(RefreshButtonStyle);
        RefreshButton.setSize(STANDARD_SIZE, STANDARD_SIZE);
        RefreshButton.setPosition(Gdx.graphics.getWidth() - (float) Math.sqrt(Gdx.graphics.getWidth()) * 3, Gdx.graphics.getHeight() - (float) Math.sqrt(Gdx.graphics.getHeight()) * 3 - 200);

        ImageButtonStyle LastButtonStyle = new ImageButtonStyle();
        LastButtonStyle.up = skin.getDrawable("Button Last");
        LastButtonStyle.down = skin.getDrawable("Button Last");
        LastButtonStyle.pressedOffsetX = 1;
        LastButtonStyle.pressedOffsetY = -1;
        LastButton = new ImageButton(LastButtonStyle);
        LastButton.setSize(STANDARD_SIZE, STANDARD_SIZE);
        LastButton.setPosition(Gdx.graphics.getWidth() - (float) Math.sqrt(Gdx.graphics.getWidth()) * 3, Gdx.graphics.getHeight() - (float) Math.sqrt(Gdx.graphics.getHeight()) * 3 - 100);

        table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        table.setBackground(dialogskin.getDrawable("background_transparent"));
        table.add(new Label("Really want to quit?", tableskin)).padBottom(10).center().colspan(2).row();
        table.add(no).size(80, 80).padRight(60);
        table.add(yes).size(80, 80).padLeft(60);

        dialog.addActor(table);
        stage.addActor(ExitButton);
        stage.addActor(LastButton);
        stage.addActor(RefreshButton);
        stage.addActor(scorelabel);

        setupCamera();

        Gdx.input.setCatchBackKey(true);
        Gdx.input.setCatchMenuKey(true);

        Vector3 touchPos = new Vector3();
        camera.unproject(touchPos);

        if (Gdx.input.isTouched()) {
            Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        }

        this.inputController = new InputController() {

            //------------------------------------------------------------------------------------
            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                boolean grab = false;
                float spriteX, spriteY;

                Vector3 touchPos = new Vector3();
                touchPos.set(screenX, screenY, 0);
                camera.unproject(touchPos);

                for (Point p : nodes) {
                    if (p.isClickedOnSprite(touchPos.x, touchPos.y) && moving == 1) {
                        grab = true;
                        p.x = (int) touchPos.x;
                        p.y = (int) touchPos.y;
                        p.syncSprite();
                        break;
                    }
                }
                return super.touchDragged(screenX, screenY, pointer);
            }

            //------------------------------------------------------------------------------------
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {

                float pointX, pointY;

                Vector3 touchPos = new Vector3();
                touchPos.set(screenX, screenY, 0);
                camera.unproject(touchPos);

                if (Gdx.input.isTouched()) {
                    Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                    camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

                }

                for (Point p : nodes) {
                    if (p.isClickedOnSprite(touchPos.x, touchPos.y)) {
                        System.out.println("clicked on point " + p.id);
                        if (p.owner != Player.None) break;
                        p.owner = Player.Human;
                        p.sprite.setTexture(img_y);

                        List<Point> list = getAdjacencyList(p, true);

                        for (Point ak : list) {
                            if (ak.owner == Player.None) {
                                ak.owner = Player.Human;
                                Sprite s = ak.sprite;
                                s.setTexture(img_d);
                            }
                        }
                        Vector3 score = determineScore();
                        scoreupdate(score);
                        if (score.z == 1) {
                            // end of game!
                            ((Game)Gdx.app.getApplicationListener()).setScreen(new HighscoresAdd());
                            break;
                        }
                        else {
                            enemyaction();
                        }
                        score = determineScore();
                        scoreupdate(score);
                        if (score.z == 1) {
                            // end of game!
                            ((Game)Gdx.app.getApplicationListener()).setScreen(new HighscoresAdd());
                            break;
                        }
                    }
                }

                endofgame();

                return super.touchDown(screenX, screenY, pointer, button);
            }

            //-------------------------------------------------------------------------------------------
            @Override
            public boolean keyDown(int keycode) {
                if ((keycode == Keys.M) || (keycode == Keys.MENU)) {

/*                    PlayMoveButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if (moving == 0) {
                                moving = 1;
                                Gdx.input.setInputProcessor(inputController);
                            } else {
                                moving = 0;
                                Gdx.input.setInputProcessor(inputController);
                            }

                        }
                    });*/

                    RefreshButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            render_toast.makeText("Generating...", "font", Toast.COLOR_PREF.RED, Toast.STYLE.ROUND, Toast.TEXT_POS.middle_right, Toast.TEXT_POS.middle_down, Toast.LONG);
                            wrongtap.play();
                            score=0;
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new TheGame());
                        }
                    });

                    ExitButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            Gdx.input.setInputProcessor(dialog);
                            pause();

                        }
                    });

                    LastButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            ((Game)Gdx.app.getApplicationListener()).setScreen(new HighscoresAdd());

                        }
                    });

                    no.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            Gdx.input.setInputProcessor(inputController);
                            resume();
                        }
                    });

                    yes.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            shutdown.play();
                            Gdx.app.exit();
                        }
                    });

                    Gdx.input.setInputProcessor(stage);

                } else if ((keycode == Keys.ESCAPE) || (keycode == Keys.BACK)) {
                    pause();

                    no.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {

                            resume();
                        }
                    });

                    yes.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            shutdown.play();
                            Gdx.app.exit();
                        }
                    });

                    //render_toast.makeText("Exiting...", "font", Toast.COLOR_PREF.GREEN, Toast.STYLE.ROUND, Toast.TEXT_POS.middle_right, Toast.TEXT_POS.middle_down, Toast.LONG);

                    Gdx.input.setInputProcessor(dialog);

                }

                return super.keyDown(keycode);
            }

        };

        Gdx.input.setInputProcessor(inputController);
        StartTime = System.currentTimeMillis();
    }

    //-------------------------------------------------------------------------------------------
    private Point getPointWithMinimumAdj() {
        int minimum = 1000000;
        Point minPoint = null;
        for (Point p: nodes) {
            if (p.owner != Player.None) continue;
            List<Point> temp = getAdjacencyList(p, true);
            if (temp.size() < minimum) {
                minimum = temp.size();
                minPoint = p;
            }
        }
        return minPoint;
    }

    //-------------------------------------------------------------------------------------------
    public void enemyaction() {

        Point p = getPointWithMinimumAdj();
        if (p == null) {
            // game over
            ((Game)Gdx.app.getApplicationListener()).setScreen(new HighscoresAdd());
            return;
        }
        List<Point> list = getAdjacencyList(p, true);

        p.owner = Player.Computer;
        p.sprite.setTexture(img_s);
        for (Point ak: list) {
            ak.owner = Player.Computer;
            ak.sprite.setTexture(img_s);
        }
    }

    //-------------------------------------------------------------------------------------------
    protected Vector3 determineScore() {
        int human = 0, comp = 0;
        boolean gameOver = true;
        for (Point p : nodes) {
            if (p.owner == Player.Human) human++;
            else if (p.owner == Player.Computer) comp++;
            else {

                gameOver = false;
            }
        }
        int third;
        if (gameOver) {
            third = 1;
            // HighscoresAdd.java
            TheGame.score = human;
        }
        else third = 0;
        return new Vector3(human, comp, third);
    }

    //-------------------------------------------------------------------------------------------
    private List<Point> getAdjacencyList(Point p, boolean onlyUnsigned) {
        List<Point> temp = new ArrayList<Point>();
        for (Edge edge: edges) {
            if (edge.contains(p)) {
                if (onlyUnsigned) {
                    if (edge.giveTheOtherPoint(p).owner == Player.None) temp.add(edge.giveTheOtherPoint(p));
                }
                else {
                    temp.add(edge.giveTheOtherPoint(p));
                }
            }
        }
        return temp;
    }

    //-------------------------------------------------------------------------------------------
    public void endofgame(){
        System.out.println("ERTEK: " + score+score2);
    }

    //-------------------------------------------------------------------------------------------
    public void scoreupdate(Vector3 score){
        String s = score.z == 1 ? "Game Over! " : "";
        String t = String.format(s + " Player: %d , Enemy: %d", (int) score.x, (int) score.y);
        scorelabel.setText(t);
    }

    //-------------------------------------------------------------------------------------------
    @Override
    public void render(float delta) {
        switch (state) {
            case Running:
                update();
                break;
            case Paused:
                paused();
                break;
        }

    }
    //-------------------------------------------------------------------------------------------
    private void update() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        backgroundSprite.draw(batch);
        batch.end();

        batch2.begin();
        uparrow.setPosition(Gdx.graphics.getWidth() - (float) Math.sqrt(Gdx.graphics.getWidth()) * 3, Gdx.graphics.getHeight() - (float) Math.sqrt(Gdx.graphics.getHeight()) * 3 - 350);
        menubutton.setPosition(Gdx.graphics.getWidth() - (float) Math.sqrt(Gdx.graphics.getWidth()) * 3, Gdx.graphics.getHeight() - (float) Math.sqrt(Gdx.graphics.getHeight()) * 3 - 400);
        menubutton.draw(batch2);
        uparrow.draw(batch2);
        batch2.end();

        render_toast.toaster();

        sr.setColor(Color.MAROON);
        sr.setProjectionMatrix(camera.combined);

        sr.begin(ShapeType.Line.Filled);
        for (Edge e : edges) {
            sr.rectLine(e.p1.x, e.p1.y, e.p2.x, e.p2.y, 5);
        }
        sr.end();

        sr.begin(ShapeType.Filled);
        int j = 0;
        for (Point p : nodes) {
            batch.begin();
            batch.setProjectionMatrix(camera.combined);
            Sprite s = p.sprite;
            s.draw(batch);
            batch.end();
        }


        sr.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    //-------------------------------------------------------------------------------------------
    private void paused() {
        dialog.act();
        dialog.draw();
    }

    //-------------------------------------------------------------------------------------------
    public static List<Edge> simpleGraph(List<Point> nodes) {
        int numEdges = nodes.size() * 2;
        List<Edge> edges = new ArrayList<Edge>(numEdges);
        for (int i = 0; i < numEdges; i++) {
            Point p1 = nodes.get((int) (Math.random() * nodes.size()));
            Point p2 = nodes.get((int) (Math.random() * nodes.size()));
            if (!p2.equals(p1)) edges.add(new Edge(p1, p2));
        }

        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            if (e.p1.equals(e.p2))
                edges.remove(i);
        }

        for (int i = 0; i < edges.size(); i++) {
            Edge e = edges.get(i);
            for (int q = 0; q < edges.size(); q++) {
                if (i == q)
                    continue;
                Edge e2 = edges.get(q);
                if (e.equals(e2))
                    edges.remove(q);
            }
        }

        return edges;
    }

    //-------------------------------------------------------------------------------------------
    public List<Point> poissonDisc(int width, int height, int minDist) {
        final int try_points = 100;
        final Rectangle bounds = new Rectangle(0, 0, width, height);

        Random r = new Random();
        List<Point> samplePoints = new ArrayList<Point>();

        double cellSize = minDist / Math.sqrt(2);
        Point[][] grid = new Point[(int) Math.ceil(height / cellSize)][(int) Math.ceil(width / cellSize)];
        List<Point> processList = new ArrayList<Point>();
        Point firstPoint = new Point(r.nextInt(width), r.nextInt(height));
        processList.add(firstPoint);
        samplePoints.add(firstPoint);
        addToCell(firstPoint, cellSize, grid);

        while (!processList.isEmpty()) {
            Point p = processList.remove(r.nextInt(processList.size()));
            for (int i = 0; i < try_points; i++) {
                Point newPoint = genAround(p, minDist);
                if (bounds.contains(newPoint.x, newPoint.y) && !inNeighborhood(grid, newPoint, minDist, cellSize)) {
                    processList.add(newPoint);
                    samplePoints.add(newPoint);
                    addToCell(newPoint, cellSize, grid);
                }
            }
        }


        int i = 0;
        for (Point p : samplePoints) {
            p.setID(i);
            Sprite s = new Sprite(img);
            s.setSize(NODE_RADIUS, NODE_RADIUS);
            p.setSprite(s);
            Rectangle rect = s.getBoundingRectangle();
            i++;
        }
        return samplePoints;
    }

    //-------------------------------------------------------------------------------------------
    public static boolean inNeighborhood(Point[][] grid, Point point, double mindist, double cellSize) {
        int gridX = (int) (point.x / cellSize);
        int gridY = (int) (point.y / cellSize);

        ArrayList<Point> cellsAroundPoint = new ArrayList<Point>();
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                int xc = gridX + x;
                int yc = gridY + y;
                if (xc >= 0 && yc >= 0 && xc < grid.length && yc < grid[0].length)
                    cellsAroundPoint.add(new Point(xc, yc));
            }
        }

        for (int i = 0; i < cellsAroundPoint.size(); i++) {
            Point p = cellsAroundPoint.get(i);
            if (grid[p.y][p.x] != null && Math.hypot(grid[p.y][p.x].x - point.x, grid[p.y][p.x].y - point.y) < mindist)
                return true;
        }
        return false;
    }

    public static Point genAround(Point point, double mindist) {
        double r1 = Math.random();
        double r2 = Math.random();
        double radius = mindist * (r1 + 1);
        double angle = 2 * Math.PI * r2;
        int newX = (int) (point.x + radius * Math.cos(angle));
        int newY = (int) (point.y + radius * Math.sin(angle));
        return new Point(newX, newY);
    }

    public static void addToCell(Point p, double cellSize, Point[][] grid) {
        int x = (int) (p.x / cellSize);
        int y = (int) (p.y / cellSize);
        grid[y][x] = p;
    }

    public static class Edge {

        String name;
        final Point p1, p2;

        Edge(Point q1, Point q2) {
            p1 = q1;
            p2 = q2;
        }

        public boolean equals(Edge e) {
            return (    (e.p1.equals(p1) && e.p2.equals(p2)) ||
                    (e.p1.equals(p2) && e.p2.equals(p1))
            );
        }

        public boolean contains(Point p) {
            return p1.equals(p) || p2.equals(p);
        }

        public Point giveTheOtherPoint(Point p) {
            if (p1.equals(p)) return p2;
            else if (p2.equals(p)) return p1;
            else return null;
        }

        @Override
        public String toString() {
            String s = "Edge: p1 = " + p1.toString() + "|| p2 = " + p2.toString();
            return s;
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {
        this.state = State.Paused;
    }

    @Override
    public void resume() {
        this.state = State.Running;
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

}