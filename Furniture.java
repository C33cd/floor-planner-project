import java.awt.*;

//import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.*;
import javax.swing.JOptionPane;

//Furniture list: bed(single and double), table(dining), sofa(single and triple)
//Fixture list:  toilet, washbasin,shower,kitchen-sink and stove
//Apply proper scaling later

class Furniture extends FloorPlanApp{
    private int x, y;//x and y coordinates relative to room
    private Room room;
    private String type;
    private Image img;
    private int rotateAngle;
    private int width;
    private int height;

    public Furniture(Room room, String type, int x, int y) {
        this.x = x - room.getX();
        this.y = y - room.getY();
        this.type = type;
        this.room = room;
        this.rotateAngle = 0;
        try{
            switch (type) {
                case "Single Bed":
                    this.img = ImageIO.read(new File("single-bed-1.jpg"));
                    width = 25;
                    height = 50;
                    break;
                case "Double Bed":
                    this.img = ImageIO.read(new File("double-bed-2.jpg"));
                    width = 50;
                    height = 50;
                    break;
                case "Dining Table":
                    this.img = ImageIO.read(new File("dining-table.jpg"));
                    width = 100;
                    height = 20;
                    break;
                case "Sofa":
                    this.img = ImageIO.read(new File("sofa.jpg"));
                    width = 25;
                    height = 25;
                    break;
                case "Triple Sofa":
                    this.img = ImageIO.read(new File("triple-sofa.jpg"));
                    width = 75;
                    height = 25;
                    break;
                case "Toilet":
                    this.img = ImageIO.read(new File("toilet-1.jpg"));
                    width = 18;
                    height = 20;
                    break;
                case "Washbasin":
                    this.img = ImageIO.read(new File("sink-1.jpg"));
                    width = 10;
                    height = 20;
                    break;
                case "Kitchen Sink":
                    this.img = ImageIO.read(new File("kitchen-sink.jpg"));
                    width = 20;
                    height = 20;
                    break;
                case "Shower":
                    this.img = ImageIO.read(new File("shower.jpg"));
                    width = 20;
                    height = 20;
                    break;
                case "Stove":
                    this.img = ImageIO.read(new File("stove.jpg"));
                    width = 20;
                    height = 25;
                    break;
                default:
                    //should not be executed; executed in case some error with code
                    JOptionPane.showMessageDialog(super.getContentPane(), "Option does not exist!!");
                    return;
            }
            this.img = img.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        }catch(IOException e){
            JOptionPane.showMessageDialog(super.getContentPane(), "Image not found!");
            e.printStackTrace();
            return;
        }

    }

    public Furniture(Room room, String type, int x, int y, int rotateAngle) {
        this(room,type,x,y);
        this.setRotateAngle(rotateAngle);

    }

    public void draw(Graphics g) {
        /*Graphics2D g2d = (Graphics2D)g.create();
        if(img!=null){
            g2d.translate(room.getX()+x+(img.getWidth(null)/2), room.getY()+y+(img.getHeight(null)/2));
            g2d.rotate(Math.toRadians(rotateAngle));
            g2d.drawImage(img, -img.getWidth(null)/2, -img.getHeight(null)/2, null);
            g2d.dispose();

            
        }*/
        Graphics2D g2d = (Graphics2D)g.create();
        if(img!=null){
            g2d.translate(room.getX()+x+(img.getWidth(null)/2), room.getY()+y+(img.getHeight(null)/2));
            g2d.rotate(Math.toRadians(rotateAngle));
            g2d.drawImage(img, -img.getWidth(null)/2, -img.getHeight(null)/2, null);
            g2d.dispose();   
        }

    }

	public int getX() {return x;}

    public int getY() {return y;}

    public int getWidth(){return width;}
    
    public int getHeight(){return height;}
    
    public int getRotateAngle(){return rotateAngle;}

    public void setRotateAngle(int r){ this.rotateAngle = r;}
    
    public String getFurnitureType(){ return type;}
    
    public Room getRoom(){return room;}

    public void rotateFurniture(){
        rotateAngle += 90;
        if(rotateAngle==360){
            rotateAngle = 0;
        }
        repaint();
    }

    public boolean contains(int point_x, int point_y) {
        /*System.out.println(point_x+" "+point_y+" "+(x + room.getX())+" "+(y + room.getY()));
        System.out.println(point_x+" "+point_y+" "+(x + room.getX()+width)+" "+(y + room.getY()+height));*/
        //System.out.print(point_x >= x + room.getX() && point_x <= x + room.getX() + width);
        //System.out.println(" "+(point_y >= y + room.getY() && point_y <= y + room.getY() + height));
        return point_x >= x + room.getX() && point_x <= x + room.getX() + width &&
               point_y >= y + room.getY() && point_y <= y + room.getY() + height;
    }

    public boolean overlaps(Furniture other){
        if(other.getRoom().equals(room)){
            return x < other.x + other.width &&
                x + width > other.x &&
                y < other.y + other.height &&
                y + height > other.y;
        }
        else{
            return false;
        }
    }
}

