import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
//import javax.swing.JLabel;
//import javax.swing.JToolTip;
//import javax.swing.SwingConstants;
//import javax.swing.event.MouseInputAdapter;
import javax.swing.JOptionPane;

class Room extends JComponent{
    private int x, y, width, height;
    private Color color;
    private int originalX, originalY;
    //private JToolTip t;
    public String name;
    private int id;
    private List<Furniture> furniture;

    public Room(int x, int y, int width, int height, Color color, String name) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.color = color;
        this.originalX = x;
        this.originalY = y;
        this.name = name;
        this.furniture = new ArrayList<>();
        /*t = this.createToolTip();
        Room r = this;
        this.addMouseListener(new MouseInputAdapter() {
            public void mouseEntered(MouseEvent e){
                t.setToolTipText("Room: "+r.name+", Room ID: "+r.getID());
                //System.out.println(t.isShowing());//for testing
            }
        });*/
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x, y, width, height);
        if(furniture.size()>0){
            Iterator<Furniture> it_f = furniture.iterator();
            while(it_f.hasNext()){
                Furniture f1 = it_f.next();
                f1.draw(g);
            }
        }
    }

    public boolean overlaps(Room other) {
        return x < other.x + other.width &&
               x + width > other.x &&
               y < other.y + other.height &&
               y + height > other.y;
    }

    public boolean contains(Point point) {
        return point.x >= x && point.x <= x + width &&
               point.y >= y && point.y <= y + height;
    }

    
    public void updateOriginalCoord(){
        this.originalX = x;
        this.originalY = y;
    }
    
    public void setX(int x) {this.x = x;}
    
    public void setY(int y) {this.y = y;}

    public int getX() {return x;}

    public int getY() {return y;}

    public int getWidth() { return width;}

    public int getHeight() { return height;}

    public int getOriginalX() { return originalX;}

    public int getOriginalY() { return originalY;}

    public Color getColor() { return color;}
    
    //public JToolTip getToolTip(){ return t;}
    
    public void setID(int i){ id = i;}
    
    public int getID(){ return id;}

    public void addFurniture(Furniture f){ furniture.add(f); }

    public List<Furniture> getFurnitures(){ return furniture;}
    
    public Point[] getWall(String selector){
        Point[] l = new Point[2];
        if(selector.equals("north")){
            l[0] = new Point(x, y);
            l[1] = new Point(x + width, y);
            return l;
        }
        else if(selector.equals("south")){
            l[0] = new Point(x, y + height);
            l[1] = new Point(x + width, y + height);
            return l;
        }
        else if(selector.equals("east")){
            l[0] = new Point(x + width, y);
            l[1] = new Point(x + width, y + height);
            return l;
        }
        else if(selector.equals("west")){
            l[0] = new Point(x, y);
            l[1] = new Point(x, y + height);
            return l;
        }
        else{
            return null;
        }
    }

    
    //to get the common edge between any two rooms:
    public Point[] getOverlappingEdge(Room r2){
        Room r1 = this;
        Point[] line = new Point[2];
        line[0] = new Point(-1,-1);
        line[1] = new Point(-1,-1);
        /*For Layout:
         *r1
         *r2
         */
        if(r1.getY()+r1.getHeight()==r2.getY()){
            line[0].y = r2.getY();
            line[1].y = line[0].y;
            if(r2.getX()<=r1.getX() && r1.getX()<=r2.getX()+r2.getWidth()){
                line[0].x = r1.getX();
                if(r1.getX()+r1.getWidth()<r2.getX()+r2.getWidth()){
                    line[1].x = r1.getX()+r1.getWidth();
                }
                else{
                    line[1].x = r2.getX()+r2.getWidth();
                } 
            }
            else if(r1.getX()<=r2.getX() && r2.getX()<=r1.getX()+r1.getWidth()){
                line[0].x = r2.getX();
                if(r2.getX()+r2.getWidth()<r1.getX()+r1.getWidth()){
                    line[1].x = r2.getX()+r2.getWidth();
                }
                else{
                    line[1].x = r1.getX()+r1.getWidth();
                } 
            }
        }
        /*For Layout:
         *r2
         *r1
         */
        else if(r1.getY()==r2.getY()+r2.getHeight()){
            //same code as above if with r1 and r2 switched
            line = r2.getOverlappingEdge(r1);
        }
        /*For Layout:
         * r2 r1
         */
        else if(r1.getX()==r2.getX()+r2.getWidth()){
            line[0].x = r1.getX();
            line[1].x = line[0].x;
            if(r2.getY()<=r1.getY() && r1.getY()<=r2.getY()+r2.getHeight()){
                line[0].y = r1.getY();
                if(r1.getY()+r1.getHeight()<r2.getY()+r2.getHeight()){
                    line[1].y = r1.getY()+r1.getHeight();
                }
                else{
                    line[1].y = r2.getY()+r2.getHeight();
                } 
            }
            else if(r1.getY()<=r2.getY() && r2.getY()<=r1.getY()+r1.getHeight()){
                line[0].y = r2.getY();
                if(r2.getY()+r2.getHeight()<r1.getY()+r1.getHeight()){
                    line[1].y = r2.getY()+r2.getHeight();
                }
                else{
                    line[1].y = r1.getY()+r1.getHeight();
                } 
            }
        }
        /*For Layout:
         * r1 r2
         */
        else if(r1.getX()+r1.getWidth()==r2.getX()){
            //same code as above with r1 and r2 interchanged
            line = r2.getOverlappingEdge(r1);
        }
        return line;
    }
}
