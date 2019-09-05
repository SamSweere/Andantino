package game;

import java.awt.*;

public class Tile {
    private final int radius;

    public final Point location;

    private final Point coordinates;

    private final Polygon hexagon;

    public int stone;

    public Tile(Point location, int radius, int width, int height, int stone) {
        this.location = location;
        this.coordinates = locationToCoordinates(location,radius,width,height);
        this.radius = radius;
        this.stone = stone;
        this.hexagon = createTile();
    }

    private Polygon createTile() {
        Polygon polygon = new Polygon();
        double w = Math.sqrt(3) * radius;
        double h = 2 * radius;

        if((location.y % 4) == 0){
            polygon.addPoint((int)(coordinates.x - 0.5*w),(int)(coordinates.y + 0.25*h));
            polygon.addPoint((int)(coordinates.x),(int)(coordinates.y + 0.5*h));


            polygon.addPoint(coordinates.x - 0.5 radius,coordinates.y + (int) (Math.tan(Math.PI / 12) * radius));
            polygon.addPoint(coordinates.x,coordinates.y + (int) (radius/ Math.cos(Math.PI / 12) ));
            polygon.addPoint(coordinates.x + radius,coordinates.y + (int) (Math.tan(Math.PI / 12) * radius));
            polygon.addPoint(coordinates.x + radius,coordinates.y + (int) (-1*Math.tan(Math.PI / 12) * radius));
            polygon.addPoint(coordinates.x,coordinates.y + (int) (-1*radius/ Math.cos(Math.PI / 12) ));
            polygon.addPoint(coordinates.x-radius,coordinates.y + (int) (-1*Math.tan(Math.PI / 12) * radius));

        }else{
            for (int i = 0; i < 6; i++) {
                int xval = (int) (coordinates.x + radius
                        * Math.sin(i * 2 * Math.PI / 6D));
                int yval = (int) (coordinates.y + radius
                        * Math.cos(i * 2 * Math.PI / 6D));
                polygon.addPoint(xval, yval);
            }
        }

        return polygon;
    }

    private Point locationToCoordinates(Point location, int radius, int width, int height){
        Point coordinates = new Point(0,0);
        //The -1 is to convert it to normal axes
        coordinates.x = (int)(location.x * Math.sqrt(3) * radius);
        coordinates.y = (int)(-1 * location.y * 3/4 * radius);

        coordinates.x += width/2;
        coordinates.y += height/2;
        return coordinates;
    }

    public int getRadius() {
        return radius;
    }

    public Point getLocation() {
        return location;
    }

    public Polygon getTile() {
        return hexagon;
    }

}
