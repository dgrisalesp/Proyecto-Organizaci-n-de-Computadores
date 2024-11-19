
import java.util.ArrayList;

public class Register {
    private ArrayList<Point> path;
    private Point point;
    public Register(Point point, ArrayList<Point> path) {
        this.path = path;
        this.point = point;
    }
    public ArrayList<Point> getPath() {
        return path;
    }
    public Point getPoint() {
        return point;
    }
    public void setPath(ArrayList<Point> path) {
        this.path = path;
    }
    public void setPoint(Point point) {
        this.point = point;
    }
}
