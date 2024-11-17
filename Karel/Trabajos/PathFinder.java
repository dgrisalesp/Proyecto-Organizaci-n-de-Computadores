
import java.util.*;

public class PathFinder {
    public PathFinder() {
    }

    public ArrayList<Point> findPath(int[][] matrix, Point current, Point destination) {
        Queue<Register> queue = new LinkedList<>();
        Set<Point> visited = new HashSet<>();
        visited.add(current);
        ArrayList<Point> path = new ArrayList<>();
        Register register=new Register(current, path);
        queue.add(register);
        int[][] directions = {
            {0, 1},  // right
            {1, 0},  // down
            {0, -1}, // left
            {-1, 0}  // up
        };
        while (!queue.isEmpty()){
            Register currentRegister=queue.poll();
            ArrayList<Point> currentPath=currentRegister.getPath();
            Point currentPoint=currentRegister.getPoint();
            if (currentPoint.equals(destination)){
                return currentPath;
            }
            int x=currentPoint.getX();
            int y=currentPoint.getY();
            
            for (int[] direction: directions){
                int nx=x+direction[0];
                int ny=y+direction[1];
                if (nx>=0 && nx<matrix.length && ny>=0 && ny<matrix[0].length && matrix[nx][ny]!=0 && !visited.contains(new Point(nx,ny))){
                    visited.add(new Point(nx,ny));
                    ArrayList<Point> newPath=new ArrayList<>(currentPath);
                    newPath.add(new Point(nx,ny));
                    Register newRegister=new Register(new Point(nx,ny),newPath);
                    queue.add(newRegister);
                }
            }
        }
        return null;
    }
      
    public static void main(String[] args) {
        int[][] matrix = {
                { 1, 1, 1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 },
                { 0, 0, 0, 0, 0, 0, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5 },
                { 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 },
                { 5, 5, 5, 5, 5, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 },
                { 5, 5, 5, 5, 5, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 },
                { 5, 5, 5, 5, 5, 0, 0, 0, 5, 5, 5, 5, 5, 5, 5, 5 },
                { 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 },
                { 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 1, 5, 5 },
                { 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 5, 5, 5, 5, 5, 5 },
                { 5, 1, 5, 5, 5, 5, 5, 5, 5, 0, 5, 5, 5, 5, 5, 5 },
                { 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 0, 0, 0, 5, 5, 5 },
                { 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 },
                { 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 },
                { 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 },
                { 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 },
                { 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 },
        };
        Point current = new Point(0, 0);
        Point destination = new Point(15, 15);
        PathFinder pathFinder = new PathFinder();
        ArrayList<Point> path = pathFinder.findPath(matrix, current, destination);
        System.out.println(path.size());
        for (Point point : path) {
            System.out.println(point);
        }
    }
}