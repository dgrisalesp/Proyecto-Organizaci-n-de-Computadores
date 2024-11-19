public class Point {
 public byte x;
 public byte y;
    public Point(int x, int y) {
        this.x = (byte)x;
        this.y = (byte)y;
    }
    public void setX(int x) {
        this.x = (byte)x;
    }
    public void setY(int y) {
        this.y = (byte) y;
    }
    public int getX() {
        return (int)this.x;
    }
    public int getY() {
        return (int)this.y;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point point = (Point) obj;
        return this.x == (byte)point.getX() && this.y == (byte)point.getY();
    }

    @Override
    public int hashCode() {
        return 100 * this.x + this.y;
    }
    @Override
    public String toString() {
        return this.x + " " + this.y;
    }

}
