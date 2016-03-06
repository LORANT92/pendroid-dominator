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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.actions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import controllers.InputController;

public class TheGameDemo implements Screen {
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
    ImageButton RefreshButton;
    TextureAtlas textureatlas, textureatlas2, button2;
    public ArrayList<ArrayList<Integer>> adjLists = new ArrayList<ArrayList<Integer>>();
    public Map<Point, List<Point>> Adjacency_List;

    public int[] coordinates = new int[64];
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
    public Sprite[] vertexSprite;
    public int vertexnum = 8;
    public int adjanum = 0;
    public static long StartTime = System.currentTimeMillis();

    private static final int VIEWPORT_WIDTH = 600;
    private static final int VIEWPORT_HEIGHT = 480;
    static final int STANDARD_SIZE = 49;
    static final int STANDARD_SIZE_LITTLE = 25;
    static final int NODE_RADIUS = 30;
    static final int NODE_RADIUS_BIG = 30;
    private static final int MAX_VERTEX = 100;


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


        nodes = poissonDisc(400, 400, 100);
        edges = simpleGraph(nodes);

        img = new Texture(Gdx.files.internal("circle_x.png"));
        img_y = new Texture(Gdx.files.internal("circle_y.png"));
        img_d = new Texture(Gdx.files.internal("circle_d.png"));
        img_s = new Texture(Gdx.files.internal("circle_s.png"));
        uparrowimg = new Texture(Gdx.files.internal("uparrow.png"));
        menubuttonimg = new Texture(Gdx.files.internal("menubutton.png"));

        uparrow = new Sprite(uparrowimg);
        menubutton = new Sprite(menubuttonimg);
        uparrow.setSize(STANDARD_SIZE, STANDARD_SIZE);
        menubutton.setSize(STANDARD_SIZE, STANDARD_SIZE);

        vertexSprite = new Sprite[MAX_VERTEX];

        for (int i = 0; i < MAX_VERTEX; i++) {
            vertexSprite[i] = new Sprite(img);
            vertexSprite[i].setSize(NODE_RADIUS, NODE_RADIUS);

        }

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
        scorelabel.setText("Score:" + score);

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

        ImageButtonStyle PlayMoveButtonStyle = new ImageButtonStyle();
        PlayMoveButtonStyle.up = skin.getDrawable("Button Play");
        PlayMoveButtonStyle.down = buttonskin.getDrawable("play_gray");
        PlayMoveButtonStyle.pressedOffsetX = 1;
        PlayMoveButtonStyle.pressedOffsetY = -1;
        PlayMoveButton = new ImageButton(PlayMoveButtonStyle);
        PlayMoveButton.setSize(STANDARD_SIZE, STANDARD_SIZE);
        PlayMoveButton.setPosition(Gdx.graphics.getWidth() - (float) Math.sqrt(Gdx.graphics.getWidth()) * 3, Gdx.graphics.getHeight() - (float) Math.sqrt(Gdx.graphics.getHeight()) * 3 - 100);


        ImageButtonStyle RefreshButtonStyle = new ImageButtonStyle();
        RefreshButtonStyle.up = skin.getDrawable("Button Refresh");
        RefreshButtonStyle.down = buttonskin.getDrawable("refresh_gray");
        RefreshButtonStyle.pressedOffsetX = 1;
        RefreshButtonStyle.pressedOffsetY = -1;
        RefreshButton = new ImageButton(RefreshButtonStyle);
        RefreshButton.setSize(STANDARD_SIZE, STANDARD_SIZE);
        RefreshButton.setPosition(Gdx.graphics.getWidth() - (float) Math.sqrt(Gdx.graphics.getWidth()) * 3, Gdx.graphics.getHeight() - (float) Math.sqrt(Gdx.graphics.getHeight()) * 3 - 200);

        table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        table.setBackground(dialogskin.getDrawable("background_transparent"));
        table.add(new Label("Really want to quit?", tableskin)).padBottom(10).center().colspan(2).row();
        table.add(no).size(80, 80).padRight(60);
        table.add(yes).size(80, 80).padLeft(60);

        dialog.addActor(table);
        stage.addActor(ExitButton);
        stage.addActor(PlayMoveButton);
        stage.addActor(RefreshButton);
        stage.addActor(scorelabel);

        setupCamera();

        arraytopup();

        for (Edge e : edges) {

            System.out.println("SZOMSZEDOK: x1 " + e.p1.x + "  y1 " + e.p1.y + "   x2 " + e.p2.x + "   y2 " + e.p2.y);

        }


        Gdx.input.setCatchBackKey(true);
        Gdx.input.setCatchMenuKey(true);

        Vector3 touchPos = new Vector3();
        camera.unproject(touchPos);

        if (Gdx.input.isTouched()) {
            Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        }

        int j = 0;
        for (Point p : nodes) {
            for (Edge e : edges) {
                if ((p.x == e.p1.x && p.y == e.p1.y) || (p.x == e.p2.x && p.y == e.p2.y)) {
                    j++;
                    break;
                }
            }
        }

        //vertexnum = j;

        this.inputController = new InputController() {

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                boolean grab = false;
                float spriteX, spriteY;

                Vector3 touchPos = new Vector3();
                touchPos.set(screenX, screenY, 0);
                camera.unproject(touchPos);

                int k = 0;
                for (Point p : nodes) {
                    for (Edge e : edges) {
                        if ((p.x == e.p1.x && p.y == e.p1.y) || (p.x == e.p2.x && p.y == e.p2.y)) {
                            k++;

                            if (touchPos.x <= vertexSprite[k].getX() + NODE_RADIUS && touchPos.x >= vertexSprite[k].getX() && touchPos.y <= vertexSprite[k].getY() + NODE_RADIUS && touchPos.y >= vertexSprite[k].getY() && moving == 1) {

                                grab = true;
                                p.x = (int) touchPos.x;
                                p.y = (int) touchPos.y;
                            }
                            break;
                        }

                    }
                }

                return super.touchDragged(screenX, screenY, pointer);
            }

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

                int t = 0;
                int k = 0;
                for (Point p : nodes) {
                    for (Edge e : edges) {
                        if ((p.x == e.p1.x && p.y == e.p1.y) || (p.x == e.p2.x && p.y == e.p2.y)) {
                            k++;
                            if (touchPos.x <= vertexSprite[k].getX() + NODE_RADIUS && touchPos.x >= vertexSprite[k].getX() && touchPos.y <= vertexSprite[k].getY() + NODE_RADIUS && touchPos.y >= vertexSprite[k].getY() /*&& moving == 0*/) {
                                for(int l=0; l<vertexnum; l++){
                                    if(adjLists.get(l).get(0) == p.x && adjLists.get(l).get(1) == p.y) {
                                        for(int h=0; h<adjLists.get(l).size(); h+=3) {
                                            for (int g = 0; g < vertexnum; g++){
                                                if((vertexSprite[g].getX() + vertexSprite[g].getWidth() / 2) == adjLists.get(l).get(h) && (vertexSprite[g].getY() + vertexSprite[g].getHeight() / 2) == adjLists.get(l).get(h+1)){
                                                    vertexSprite[g].setTexture(img_d);
                                                    adjLists.get(l).set(2, 1);

                                                    //megvaltozott csucskoordinatak frissitese osszes listaban
                                                    for(int lj=0; lj<vertexnum; lj++){
                                                        for(int lk=0; lk<adjLists.get(lj).size(); lk+=3){
                                                            if(adjLists.get(lj).get(lk) == adjLists.get(l).get(h) && adjLists.get(lj).get(lk+1) == adjLists.get(l).get(h+1)) {
                                                                adjLists.get(lj).set(lk+2, 1);
                                                            }
                                                        }
                                                    }

                                                    for(int v=0; v<vertexnum; v++){
                                                        System.out.println("Szomszedsagok frissitve: " + adjLists.get(v));
                                                    }

                                                    score++;
                                                    scoreupdate();
                                                    System.out.println("SCORE: " + score);
                                                    //enemyaction();
                                                }
                                            }
                                        }
                                        break;
                                    }
                                }

                                vertexSprite[k].setSize(NODE_RADIUS_BIG, NODE_RADIUS_BIG);
                                vertexSprite[k].setTexture(img_y);
                                tap.play();
                                break;
                            }

                            break;

                        }
                    }

                }



                endofgame();

                return super.touchDown(screenX, screenY, pointer, button);
            }

            @Override
            public boolean keyDown(int keycode) {

                if ((keycode == Keys.M) || (keycode == Keys.MENU)) {

                    PlayMoveButton.addListener(new ClickListener() {
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
                    });


                    RefreshButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            render_toast.makeText("Generating...", "font", Toast.COLOR_PREF.RED, Toast.STYLE.ROUND, Toast.TEXT_POS.middle_right, Toast.TEXT_POS.middle_down, Toast.LONG);
                            wrongtap.play();
                            ((Game) Gdx.app.getApplicationListener()).setScreen(new TheGameDemo());
                        }
                    });

                    ExitButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            Gdx.input.setInputProcessor(dialog);
                            pause();

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

        for (Edge e : edges) {

            System.out.println("SZOMSZEDOK: x1 " + e.p1.x + "  y1 " + e.p1.y + "   x2 " + e.p2.x + "   y2 " + e.p2.y);
            adjanum++;
        }

        arrays();
        StartTime = System.currentTimeMillis();
    }

    public void enemyaction(){
        int shortestlist_size=adjLists.get(0).size();
        int shortestindex=0;
        int k = 0;

        for (Point p : nodes) {
            for (Edge e : edges) {
                if ((p.x == e.p1.x && p.y == e.p1.y) || (p.x == e.p2.x && p.y == e.p2.y)) {
                    k++;
                    for(int l=0; l<vertexnum; l++) {
                        if(adjLists.get(l).size() < shortestlist_size) {
                            shortestlist_size = adjLists.get(l).size();
                            l = shortestindex;
                        }
                    }

                    for(int h=0; h<adjLists.get(shortestindex).size(); h+=3) {
                        for (int g = 0; g < vertexnum; g++){
                            if((vertexSprite[g].getX() + vertexSprite[g].getWidth() / 2) == adjLists.get(shortestindex).get(h) && (vertexSprite[g].getY() + vertexSprite[g].getHeight() / 2) == adjLists.get(shortestindex).get(h+1)){
                                vertexSprite[g].setTexture(img_s);
                                adjLists.get(shortestindex).set(2, 1);
                                score2++;
                                System.out.println("SCORE2: " + score2);
                                for(int v=0; v<vertexnum; v++){
                                    System.out.println("Szomszedsagok frissitve: " + adjLists.get(v));
                                }

                            }
                        }

                    }


                }
            }
        }


    }


    public void endofgame(){
        int count=0;

        System.out.println("ERTEK: " + score+score2);

        /*//mĂĄs megoldĂĄs// if(score + score2 == vertexnum){
            ((Game)Gdx.app.getApplicationListener()).setScreen(new HighscoresAdd());
        }*/

        for(int f=0; f < vertexnum; f++){
            if(adjLists.get(f).get(2) != 0){
                count++;
                if(count == vertexnum){
                    ((Game)Gdx.app.getApplicationListener()).setScreen(new HighscoresAdd());
                }
            }
        }


    }

    public void scoreupdate(){
        scorelabel.setText("Score:" + score);
    }

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
            for (Edge e : edges) {
                if ((p.x == e.p1.x && p.y == e.p1.y) || (p.x == e.p2.x && p.y == e.p2.y)) {
                    j++;
                    batch.begin();
                    batch.setProjectionMatrix(camera.combined);
                    vertexSprite[j].setPosition(p.x - vertexSprite[j].getWidth() / 2, p.y - vertexSprite[j].getHeight() / 2);
                    vertexSprite[j].draw(batch);
                    batch.end();

                    break;
                }
            }
        }

        sr.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private void paused() {
        dialog.act();
        dialog.draw();
    }

    public static List<Edge> simpleGraph(List<Point> nodes) {
        int numEdges = 17;
        List<Edge> edges = new ArrayList<Edge>(numEdges);
        for (int i = 0; i < numEdges; i++) {
            Point p1 = nodes.get((int) (Math.random() * nodes.size()));
            Point p2 = nodes.get((int) (Math.random() * nodes.size()));
            edges.add(new Edge(p1, p2));
        }
/*
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
*/
        return edges;
    }

    public List<Point> poissonDisc(int width, int height, int minDist) {
        final int try_points = 10;
        final Rectangle bounds = new Rectangle(0, 0, width, height);

        Random r = new Random();
        List<Point> samplePoints = new ArrayList<Point>();

        double cellSize = minDist / Math.sqrt(2);
        Point[][] grid = new Point[(int) Math.ceil(height / cellSize)][(int) Math.ceil(width / cellSize)];
        List<Point> processList = new ArrayList<Point>();

        coordinates[0]=5;
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
        return samplePoints;
    }

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
            return e.p1.equals(p1) && e.p2.equals(p2);
        }
    }


    public void arraytopup(){

        coordinates[0]=71;
        coordinates[1]=108;
        coordinates[2]=397;
        coordinates[3]=36;

        coordinates[4]=59;
        coordinates[5]=208;
        coordinates[6]=278;
        coordinates[7]=106;

        coordinates[8]=132;
        coordinates[9]=390;
        coordinates[10]=59;
        coordinates[11]=208;

        coordinates[12]=59;
        coordinates[13]=208;
        coordinates[14]=71;
        coordinates[15]=108;

        coordinates[16]=132;
        coordinates[17]=390;
        coordinates[18]=366;
        coordinates[19]=221;

        coordinates[20]=177;
        coordinates[21]=29;
        coordinates[22]=71;
        coordinates[23]=108;

        coordinates[24]=295;
        coordinates[25]=0;
        coordinates[26]=366;
        coordinates[27]=221;

        coordinates[28]=397;
        coordinates[29]=36;
        coordinates[30]=71;
        coordinates[31]=108;

        coordinates[32]=366;
        coordinates[33]=221;
        coordinates[34]=397;
        coordinates[35]=36;

        coordinates[36]=397;
        coordinates[37]=36;
        coordinates[38]=366;
        coordinates[39]=221;

        coordinates[40]=59;
        coordinates[41]=208;
        coordinates[42]=132;
        coordinates[43]=390;

        coordinates[44]=59;
        coordinates[45]=208;
        coordinates[46]=177;
        coordinates[47]=29;

        coordinates[48]=278;
        coordinates[49]=106;
        coordinates[50]=397;
        coordinates[51]=36;

        coordinates[52]=278;
        coordinates[53]=106;
        coordinates[54]=366;
        coordinates[55]=221;

        coordinates[56]=59;
        coordinates[57]=208;
        coordinates[58]=295;
        coordinates[59]=0;

        coordinates[60]=295;
        coordinates[61]=0;
        coordinates[62]=59;
        coordinates[63]=208;

        ArrayList<Integer> myCoordinates = new ArrayList<Integer>(64);

        for(int i=0; i<64; i++){
            myCoordinates.add(coordinates[i]);
            System.out.println("KKKK: " + myCoordinates.get(i));
        }

        System.out.println("MÉRETE: " + edges.size());

        //System.out.println("ÁTTÖLT: x1 " + e.p1.x + "  y1 " + edge.p1.y + "   x2 " + edge.p2.x + "   y2 " + edge.p2.y);

        int j;
        for (int i = 0; i < 16; i++) {
            j = i*4;
            Edge edge = edges.get(i);
            edge.p1.x = myCoordinates.get(j);
            edge.p1.y = myCoordinates.get(j+1);
            edge.p2.x = myCoordinates.get(j+2);
            edge.p2.y = myCoordinates.get(j+3);
            edges.set(i, edge);
            System.out.println("ÁTTÖLT: x1 " + edge.p1.x + "  y1 " + edge.p1.y + "   x2 " + edge.p2.x + "   y2 " + edge.p2.y);
        }

    }

    public void arrays() {
        for(int i=0; i<vertexnum; i++){
            adjLists.add(new ArrayList<Integer>());
        }

        adjLists.get(0).add(0, 278);
        adjLists.get(0).add(1, 106);
        adjLists.get(0).add(2, 0);
        adjLists.get(0).add(3, 397);
        adjLists.get(0).add(4, 36);
        adjLists.get(0).add(5, 0);
        adjLists.get(0).add(6, 59);
        adjLists.get(0).add(7, 208);
        adjLists.get(0).add(8, 0);
        adjLists.get(0).add(9, 366);
        adjLists.get(0).add(10, 221);
        adjLists.get(0).add(11, 0);

        adjLists.get(1).add(0, 366);
        adjLists.get(1).add(1, 221);
        adjLists.get(1).add(2, 0);
        adjLists.get(1).add(3, 397);
        adjLists.get(1).add(4, 36);
        adjLists.get(1).add(5, 0);
        adjLists.get(1).add(6, 132);
        adjLists.get(1).add(7, 390);
        adjLists.get(1).add(8, 0);
        adjLists.get(1).add(9, 278);
        adjLists.get(1).add(10, 106);
        adjLists.get(1).add(11, 0);
        adjLists.get(1).add(9, 295);
        adjLists.get(1).add(10, 0);
        adjLists.get(1).add(11, 0);

        adjLists.get(2).add(0, 59);
        adjLists.get(2).add(1, 208);
        adjLists.get(2).add(2, 0);
        adjLists.get(2).add(3, 278);
        adjLists.get(2).add(4, 106);
        adjLists.get(2).add(5, 0);
        adjLists.get(2).add(6, 71);
        adjLists.get(2).add(7, 108);
        adjLists.get(2).add(8, 0);
        adjLists.get(2).add(9, 177);
        adjLists.get(2).add(10, 29);
        adjLists.get(2).add(11, 0);
        adjLists.get(2).add(9, 295);
        adjLists.get(2).add(10, 0);
        adjLists.get(2).add(11, 0);

        adjLists.get(3).add(0, 177);
        adjLists.get(3).add(1, 29);
        adjLists.get(3).add(2, 0);
        adjLists.get(3).add(3, 71);
        adjLists.get(3).add(4, 108);
        adjLists.get(3).add(5, 0);
        adjLists.get(3).add(6, 59);
        adjLists.get(3).add(7, 208);
        adjLists.get(3).add(8, 0);

        adjLists.get(4).add(0, 132);
        adjLists.get(4).add(1, 390);
        adjLists.get(4).add(2, 0);
        adjLists.get(4).add(3, 59);
        adjLists.get(4).add(4, 208);
        adjLists.get(4).add(5, 0);
        adjLists.get(4).add(6, 366);
        adjLists.get(4).add(7, 221);
        adjLists.get(4).add(8, 0);

        adjLists.get(5).add(0, 71);
        adjLists.get(5).add(1, 108);
        adjLists.get(5).add(2, 0);
        adjLists.get(5).add(3, 397);
        adjLists.get(5).add(4, 36);
        adjLists.get(5).add(5, 0);
        adjLists.get(5).add(6, 59);
        adjLists.get(5).add(7, 208);
        adjLists.get(5).add(8, 0);
        adjLists.get(5).add(9, 177);
        adjLists.get(5).add(10, 29);
        adjLists.get(5).add(11, 0);

        adjLists.get(6).add(0, 397);
        adjLists.get(6).add(1, 36);
        adjLists.get(6).add(2, 0);
        adjLists.get(6).add(3, 71);
        adjLists.get(6).add(4, 108);
        adjLists.get(6).add(5, 0);
        adjLists.get(6).add(6, 366);
        adjLists.get(6).add(7, 221);
        adjLists.get(6).add(8, 0);
        adjLists.get(6).add(9, 278);
        adjLists.get(6).add(10, 106);
        adjLists.get(6).add(11, 0);

        adjLists.get(7).add(0, 295);
        adjLists.get(7).add(1, 0);
        adjLists.get(7).add(2, 0);
        adjLists.get(7).add(3, 366);
        adjLists.get(7).add(4, 221);
        adjLists.get(7).add(5, 0);
        adjLists.get(7).add(6, 59);
        adjLists.get(7).add(7, 208);
        adjLists.get(7).add(8, 0);

        for(int v=0; v<vertexnum; v++){
            System.out.println("Szomszedsagok rendezve: " + adjLists.get(v));
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