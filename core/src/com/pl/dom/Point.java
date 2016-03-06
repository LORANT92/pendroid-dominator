package com.pl.dom;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Point implements java.io.Serializable
{
    int x;
    int y;
    int id;
    Player owner;
    Sprite sprite;

    private static final long serialVersionUID = -5276940640259749850L;

    public Point() {
        this(0, 0);
    }

    public Point(Point p) {
        this(p.x, p.y);
        owner = p.owner;
        sprite = p.sprite;
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
        owner = Player.None;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Point getLocation() {
        return new Point(x, y);
    }

    public void setLocation(Point p) {
        setLocation(p.x, p.y);
        syncSprite();
    }


    public void setLocation(int x, int y) {
        move(x, y);
    }

    public void setLocation(double x, double y) {
        this.x = (int) Math.floor(x+0.5);
        this.y = (int) Math.floor(y+0.5);
        syncSprite();
    }

    public void move(int x, int y) {
        this.x = x;
        this.y = y;
        syncSprite();
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public void setSprite(Sprite s) {
        sprite = s;
        syncSprite();
    }

    public Vector2 getPos(){
        return new Vector2(x,y);
    }

    public void translate(int dx, int dy) {
        this.x += dx;
        this.y += dy;
        syncSprite();
    }

    public boolean equals(Object obj) {
        // if (obj instanceof Point) {
        //     Point pt = (Point)obj;
        //    return (x == pt.x) && (y == pt.y);
        // }
        // return super.equals(obj);
        if (!(obj instanceof Point)) return false;
        Point other = (Point) obj;
        return id == other.id;
    }

    public boolean isClickedOnSprite(float sx, float sy) {
        if (sprite == null) return false;
        Rectangle r = sprite.getBoundingRectangle();
        return r.contains(sx, sy);
    }

    public void syncSprite() {
        sprite.setCenter(x, y);
    }

    public String toString() {
        return String.format("Point: %d, [%d, %d]", id, x, y);
    }

}