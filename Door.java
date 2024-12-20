import java.awt.*;

import javax.swing.JOptionPane;

/*Doors are added, popup? */
//up == L, down ==R
public class Door extends FloorPlanApp{
    private Room room1, room2;
    private int componentLength;
    private char direction;
    private int filler;
    private Point[] endpoints;
    private Point[] wall;//stores wall on which door is present
    private boolean isOpenDoor;//false -> door is b/w rooms   true -> door is b/w room and outside
    private String dir_wall;//stores which wall door is on, if it is open door
    private boolean isToBeRemoved;

    public Door(Room r1, Room r2, int componentLength, char direction, int filler){
        this.room1 = r1;
        this.room2 = r2;
        this.componentLength = componentLength;
        this.filler = filler;
        this.isOpenDoor = false;
        this.dir_wall = "";
        if(direction=='l'||direction=='r'||direction=='u'||direction=='d'){
            this.direction = (char)(direction - 32);
        }
        else{
            this.direction = direction;
        } 
        //initialise endpoints and set them to required values
        endpoints = new Point[2];
        endpoints[0] = new Point(0,0); endpoints[1] = new Point(0,0);
        setEndpointsforNewDoor();
         
    }

    //Called if door is door to outside
    public Door(Room r1, int componentLength, char direction, String dir_wall, int filler){
        this.room1 = r1;
        this.room2 = null;
        this.componentLength = componentLength;
        this.filler = filler;
        this.isOpenDoor = true;
        this.dir_wall = dir_wall;
        if(direction=='l'||direction=='r'||direction=='u'||direction=='d'){
            this.direction = (char)(direction - 32);
        }
        else{
            this.direction = direction;
        } 
        //initialise endpoints and set them to required values
        endpoints = new Point[2];
        endpoints[0] = new Point(0,0); endpoints[1] = new Point(0,0);
        setEndpointsforNewDoor();
    }
    
    //Constructor called while loading plan:
    public Door(Point[] endpoints, Room r1, Room r2, int filler){
        this.endpoints = endpoints;
        //Initialise this.room1, this.room2
        this.room1 = r1;
        this.room2 = r2;
        this.filler = filler;
        this.isOpenDoor = false;
        this.dir_wall = "";
        //initialise componentLength and direction:
        if(endpoints[0].x==endpoints[1].x){
            this.componentLength = Math.abs(endpoints[1].y-endpoints[0].y);
            if(endpoints[1].y>endpoints[0].y){
                this.direction = 'D';
            }
            else if(endpoints[1].y<endpoints[0].y) {
                this.direction = 'U';
            }
            else{
                JOptionPane.showMessageDialog(super.getContentPane(), "Door is of 0 length. Please re-enter the values.");
                return;
            }
        }
        else if(endpoints[0].y==endpoints[1].y){
            this.componentLength = Math.abs(endpoints[1].x-endpoints[0].x);
            if(endpoints[1].x>endpoints[0].x){
                this.direction = 'L';
            }
            else if(endpoints[1].x<endpoints[0].x) {
                this.direction = 'R';
            }
            else{
                JOptionPane.showMessageDialog(super.getContentPane(), "Door is of 0 length. Please re-enter the values.");
                return;
            }
        }
        else{
            return;
        }
        
    }

    //Constructor called while loading plan:
    public Door(Point[] endpoints, Room r1, String dir_wall, int filler){
        this.endpoints = endpoints;
        this.room1 = r1;
        this.room2 = null;
        this.isOpenDoor = true;
        this.dir_wall = dir_wall;
        this.filler = filler;
        if(endpoints[0].x==endpoints[1].x){
            this.componentLength = Math.abs(endpoints[1].y-endpoints[0].y);
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
            this.componentLength = Math.abs(endpoints[1].x-endpoints[0].x);
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
    
    public void draw(Graphics g) {
        g.setColor(room1.getColor());
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(endpoints[0].x, endpoints[0].y, endpoints[1].x, endpoints[1].y);//draws door
    }
    public boolean contains(Point p){
        return (endpoints[0].x <= p.x && p.x <= endpoints[1].x && endpoints[0].y==endpoints[1].y && endpoints[1].y==p.y) ||
            (endpoints[0].y <= p.y && p.y <= endpoints[1].y && endpoints[0].x==endpoints[1].x && endpoints[1].x==p.x);
    }
    
    //to set endpoints for a new door by getting overlapping edges
    public void setEndpointsforNewDoor(){
        //logic for open door
        if(isOpenDoor){
            wall = room1.getWall(dir_wall.toLowerCase());
        }
        else{
            wall = room1.getOverlappingEdge(room2);
        }
        
        //if wall is not initalised, nothing happens
        if(wall==null){
            return;
        }
        //checking if door length + filler > line length
        else if(componentLength+filler>Math.sqrt((wall[1].x-wall[0].x)*(wall[1].x-wall[0].x) + (wall[1].y-wall[0].y)*(wall[1].y-wall[0].y))){
            
            JOptionPane.showMessageDialog(super.getContentPane(), "Door length is more than wall length. "+
                "Please re-enter the values");
            return;
        }

        if(isOpenDoor){
            if(room1.name.equals("Bedroom") || room1.name.equals("Bathroom")){
                JOptionPane.showMessageDialog(super.getContentPane(), "Bedroom/Bathroom can't have "+
                    "door to the ouside. Please re-check.");
                return;
            }
        }
        
        if(componentLength==-1){
            //if user wants to make room open, no intervening walls
            endpoints[0].x = wall[0].x;
            endpoints[0].y = wall[0].y;
            endpoints[1].x = wall[1].x;
            endpoints[1].y = wall[1].y;
        }
        
        else{
            switch(direction){
                case 'L':
                    if(wall[0].y==wall[1].y){
                        endpoints[0].x = wall[0].x+filler;
                        endpoints[0].y = wall[0].y;
                        endpoints[1].x = wall[0].x+filler+componentLength;
                        endpoints[1].y = wall[0].y;
                    }
                    else{
                        JOptionPane.showMessageDialog(super.getContentPane(), "Wrong values. Please re-enter");
                        return;
                    }
                    break;
                case 'R': 
                    if(wall[0].y==wall[1].y){
                        endpoints[0].x = wall[1].x-filler-componentLength;
                        endpoints[0].y = wall[1].y;
                        endpoints[1].x = wall[1].x-filler;
                        endpoints[1].y = wall[1].y;
                    }
                    else{
                        JOptionPane.showMessageDialog(super.getContentPane(), "Wrong values. Please re-enter");
                        return;
                    }
                    break;
                case 'U': 
                    if(wall[0].x==wall[1].x){
                        endpoints[0].x = wall[0].x;
                        endpoints[0].y = wall[0].y+filler;
                        endpoints[1].x = wall[0].x;
                        endpoints[1].y = wall[0].y+filler+componentLength;  
                    }
                    else{
                        JOptionPane.showMessageDialog(super.getContentPane(), "Wrong values. Please re-enter");
                        return;
                    }
                    break;
                case 'D': 
                    if(wall[0].x==wall[1].x){
                        endpoints[0].x = wall[1].x;
                        endpoints[0].y = wall[1].y-filler-componentLength;
                        endpoints[1].x = wall[1].x;
                        endpoints[1].y = wall[1].y-filler;   
                    }
                    else{
                        JOptionPane.showMessageDialog(super.getContentPane(), "Wrong values. Please re-enter");
                        return;
                    }
                    break;
            }
        }
    }
    
    
    
    //getters and setters
    public Room getR1(){return room1;}
    public Room getR2(){return room2;}
    public int getFiller(){return filler;}
    public int getcomponentLength(){return componentLength;}
    public void setEndpoints(int x1, int y1, int x2, int y2){
        endpoints[0].x = x1;
        endpoints[0].y = y1;
        endpoints[1].x = x2;
        endpoints[1].y = y2;
    }
    public boolean isOpenDoor(){return isOpenDoor;}
    public String getWallType(){return dir_wall;}
    public int getX1(){return endpoints[0].x;}
    public int getY1(){return endpoints[0].y;}
    public int getX2(){return endpoints[1].x;}
    public int getY2(){return endpoints[1].y;}
    public boolean getRemovalStatus(){return isToBeRemoved;}
    public void setforRemoval(){isToBeRemoved = true;}

}
