package game;

import java.awt.*;

public class Tile {
    public final int x;
    public final int y;
    public final int z;

    private final int radius;
    private final int width;
    private final int height;

    private final Point coordinates;

    private final Polygon hexagon;

    public int stone;

    public Tile(int x, int y, int z, int radius, int width, int height, int stone) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.width = width;
        this.height = height;
        this.coordinates = locationToCoordinates();
        this.stone = stone;
        this.hexagon = createTile();
    }

    private Polygon createTile() {
        Polygon polygon = new Polygon();
        double w = Math.sqrt(3) * radius;
        double h = 2 * radius;

        //if((location.y % 4) == 0){
            polygon.addPoint((int)(coordinates.x - 0.5*w),(int)(coordinates.y + 0.25*h));
            polygon.addPoint( (int)(coordinates.x),(int)(coordinates.y + 0.5*h));
            polygon.addPoint((int)(coordinates.x + 0.5*w),(int)(coordinates.y + 0.25*h));
            polygon.addPoint((int)(coordinates.x + 0.5*w),(int)(coordinates.y - 0.25*h));
            polygon.addPoint( (int)(coordinates.x),(int)(coordinates.y - 0.5*h));
            polygon.addPoint((int)(coordinates.x - 0.5*w),(int)(coordinates.y - 0.25*h));
        //}else{
        //    polygon.addPoint((int)(coordinates.x - 0.5*w),(int)(coordinates.y + 0.25*h));
        //    polygon.addPoint( (int)(coordinates.x),(int)(coordinates.y + 0.5*h));
        //    polygon.addPoint((int)(coordinates.x + 0.5*w),(int)(coordinates.y + 0.25*h));
        //    polygon.addPoint((int)(coordinates.x + 0.5*w),(int)(coordinates.y - 0.25*h));
        //    polygon.addPoint( (int)(coordinates.x),(int)(coordinates.y - 0.5*h));
        //    polygon.addPoint((int)(coordinates.x - 0.5*w),(int)(coordinates.y - 0.25*h));
        //}

        return polygon;
    }

    private Point locationToCoordinates(){
        Point coordinates = new Point(0,0);
        //The -1 is to convert it to normal axes
        coordinates.x = (int)((2*x + z) * 0.5 * Math.sqrt(3) * radius);
        coordinates.y = (int)(z * 0.75 * 2 * radius);

        coordinates.x += width/2;
        coordinates.y += height/2;
        return coordinates;
    }

    public int getRadius() {
        return radius;
    }

    public Polygon getTile() {
        return hexagon;
    }

}
