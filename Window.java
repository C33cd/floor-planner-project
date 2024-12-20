import java.awt.*;
import java.util.List;

import javax.swing.JOptionPane;

public class Window extends FloorPlanApp{
    Room r;
    int windowLength;
    char direction;
    Point[] wall;//stores wall on which window is present
    String wall_selector;
    Point[] endPoints;//stores endpoints of window
    private boolean isToBeRemoved;
    private int filler;
    public Window(Room r, int windowLength, char direction, String wall_selector, int filler){
        this.r  = r;
        this.windowLength = windowLength;
        if(direction=='l'||direction=='r'||direction=='u'||direction=='d'){
            this.direction = (char)(direction - 32);
        }
        else{
            this.direction = direction;
        } 
        this.wall_selector = wall_selector;
        this.isToBeRemoved = false;
        this.filler = filler;
        //initialise endpoints and set them to required values
        endPoints = new Point[2];
        endPoints[0] = new Point(0,0); endPoints[1] = new Point(0,0);
        setEndpointsforNewWindow();//initialises wall as well
    }

    //Called when loading plan:
    public Window(Point[] endpoints, Room r, String dir_wall){
        this.endPoints = endpoints;
        this.r = r;
        this.wall = r.getWall(dir_wall.toLowerCase());
        //write filler code
        if(endpoints[0].x==endpoints[1].x){
            this.windowLength = Math.abs(endpoints[1].y-endpoints[0].y);
            if(endpoints[1].y>endpoints[0].y){
                this.direction = 'D';
            }
            else if(endpoints[1].y<endpoints[0].y) {
                this.direction = 'U';
            }
            else{
                return;
            }
        }
        else if(endpoints[0].y==endpoints[1].y){
            this.windowLength = Math.abs(endpoints[1].x-endpoints[0].x);
            if(endpoints[1].x>endpoints[0].x){
                this.direction = 'L';
            }
            else if(endpoints[1].x<endpoints[0].x) {
                this.direction = 'R';
            }
            else{
                return;
            }
        }
        else{
            return;
        }

    }
    
    public void draw(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        
        float[] dash;//to store dash array for stroke 
        
        if(endPoints[0].x!=endPoints[1].x && endPoints[0].y != endPoints[1].y){
            //JOptionPane.showMessageDialog(super.getContentPane(), "Hello");
            return;
        }
        else if(endPoints[0].x==endPoints[1].x && endPoints[0].y == endPoints[1].y){
            return;
        }
            
        /*t is defined below:
         *if  endPoints[1].y = endPoints[0].y, then t calculates difference b/w x cooridnates of endpoints
         * if endPoints[1].x = endPoints[0].x, then t calculates difference b/w y coordinates of endpoints
         * if neither are equal, then this logic fails -> that case handled above in if()
         */
        int t = Math.abs((endPoints[1].y-endPoints[0].y) + (endPoints[1].x-endPoints[0].x));
        //t stores no of pixels that line has to cover
        dash = new float[t/5 + t%5];// 5 is the pixel width for each stroke
        //loop to initialise dash array with {5,5,5,....}
        for(int i = 0; i<dash.length; i++){dash[i] = 5;}
            
        //draw dashed line:
            
        //setting stoke:
        Stroke dotted = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, dash, 0);
            
        //draw using graphics g
        g2.setColor(r.getColor());
        g2.setStroke(dotted);
        g2.drawLine(endPoints[0].x, endPoints[0].y, endPoints[1].x, endPoints[1].y); 
    } 
    
        
        
    public void setEndpointsforNewWindow(){
            this.wall = r.getWall(wall_selector);    
            if(wall==null){
                JOptionPane.showMessageDialog(super.getContentPane(), "Unable to get wall!");
                return;
            }
            //check for component length + filler exceeding wall length -> does not set endpoints if it is greater
            else if(windowLength+filler>Math.sqrt((wall[1].x-wall[0].x)*(wall[1].x-wall[0].x) + (wall[1].y-wall[0].y)*(wall[1].y-wall[0].y))){
                JOptionPane.showMessageDialog(super.getContentPane(), "Window length "+
                    "exceeds wall length of room. Please re-enter the values");
                return;
            }
            
            if(windowLength==-1){
                //if user wants to make a window span throughout the wall
                endPoints[0].x = wall[0].x;
                endPoints[0].y = wall[0].y;
                endPoints[1].x = wall[1].x;
                endPoints[1].y = wall[1].y;
            }
            else{
                switch(direction){
                    case 'L':
                        if(wall[0].y==wall[1].y){
                            endPoints[0].x = wall[0].x+filler;
                            endPoints[0].y = wall[0].y;
                            endPoints[1].x = wall[0].x+filler+windowLength;
                            endPoints[1].y = wall[0].y;
                        }
                        else{
                            JOptionPane.showMessageDialog(super.getContentPane(), "Wrong values. Please re-enter");
                            return;
                        }
                        break;
                    case 'R': 
                        if(wall[0].y==wall[1].y){
                            endPoints[0].x = wall[1].x-filler-windowLength;
                            endPoints[0].y = wall[1].y;
                            endPoints[1].x = wall[1].x-filler;
                            endPoints[1].y = wall[1].y;
                        }
                        else{
                            JOptionPane.showMessageDialog(super.getContentPane(), "Wrong values. Please re-enter");
                            return;
                        }
                        break;
                    case 'U': 
                    //from up to down
                        if(wall[0].x==wall[1].x){
                            endPoints[0].x = wall[0].x;
                            endPoints[0].y = wall[0].y+filler;
                            endPoints[1].x = wall[0].x;
                            endPoints[1].y = wall[0].y+filler+windowLength;  
                        }
                        else{
                            JOptionPane.showMessageDialog(super.getContentPane(), "Wrong values. Please re-enter");
                            return;
                        }
                        break;
                    case 'D': 
                        if(wall[0].x==wall[1].x){
                            endPoints[0].x = wall[1].x;
                            endPoints[0].y = wall[1].y-filler-windowLength;
                            endPoints[1].x = wall[1].x;
                            endPoints[1].y = wall[1].y-filler;    
                        }
                        else{
                            JOptionPane.showMessageDialog(super.getContentPane(), "Wrong values. Please re-enter");
                            return;
                        }
                        break;
                }
            }
        }
        

    public boolean isWindowbetweenRooms(List<Room> rooms){
        for(Room room : rooms){
            Point[] ov_edge = r.getOverlappingEdge(room);
            if(ov_edge==null){continue;}
            if(endPoints[0].y==endPoints[1].y && ov_edge[0].y==ov_edge[1].y && endPoints[0].y==ov_edge[0].y){
                if(ov_edge[0].x <= endPoints[0].x && endPoints[0].x <= ov_edge[1].x ||
                    ov_edge[0].x <= endPoints[1].x && endPoints[1].x <= ov_edge[1].x){
                        
                        return true;
                
                }
            }
            else if(endPoints[0].x==endPoints[1].x && ov_edge[0].x==ov_edge[1].x && endPoints[0].x==ov_edge[0].x){
                if(ov_edge[0].y <= endPoints[0].y && endPoints[0].y <= ov_edge[1].y || 
                    ov_edge[0].y <= endPoints[1].y && endPoints[1].y <= ov_edge[1].y){
                    return true;
                }
            }
            //System.out.println(endPoints[0].x+" "+endPoints[0].y+" "+endPoints[1].x+" "+endPoints[1].y+" "+
            //ov_edge[0].x+" "+ov_edge[0].y+" "+ov_edge[1].x+" "+ov_edge[1].y+" ");
            
        }
        return false;
    }

    public boolean contains(Point p){
        if(windowLength==-1){
            if(wall[0].x <= p.x && p.x <= wall[1].x && wall[0].y==wall[1].y && wall[1].y==p.y){return true;}
            else if(wall[0].y <= p.y && p.y <= wall[1].y && wall[0].x==wall[1].x && wall[1].x==p.x){return true;}
        }
        else{
            if(wall[0].x <= p.x && p.x <= wall[0].x+windowLength && wall[0].y==wall[1].y && wall[1].y==p.y){return true;}
            else if(wall[0].y <= p.y && p.y <= wall[0].y+windowLength && wall[0].x==wall[1].x && wall[1].x==p.x){return true;}
        }
        return false;
    }

    //getters and setters:
    public Room getRoom(){return this.r;}
    public int getX1(){return endPoints[0].x;}
    public int getY1(){return endPoints[0].y;}
    public int getX2(){return endPoints[1].x;}
    public int getY2(){return endPoints[1].y;}
    public int getFiller(){return filler;}
    public boolean getRemovalStatus(){return isToBeRemoved;}
    public void setforRemoval(){isToBeRemoved = true;}
    
    public String getWallType(){
        if(wall[0].y==wall[1].y && r.getX()==wall[0].x){
            if(r.getX()==wall[0].x && r.getY()==wall[0].y){return "north";}
            else if(r.getX()==wall[0].x && r.getY()+r.getHeight()==wall[0].y){return "south";}
            else{ return "";}
        }
        else if(wall[0].x==wall[1].x && r.getY()==wall[0].y){
            if(r.getY()==wall[0].y && r.getX()==wall[0].x){return "west";}
            else if(r.getY()==wall[0].y && r.getX()+r.getWidth()==wall[0].x){return "east";}
            else{ return "";}
        }
        else{
            return "";
        }
    }
    
}
