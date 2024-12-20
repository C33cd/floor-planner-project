import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.*;

class CanvasPanel extends JPanel {
    private List<Room> rooms;
    private ControlPanel controlPanel;
    private Room selectedRoom;
    private Point dragStart;
    private List<Door> doors;
    private List<Window> windows;
    private Furniture selectedFurniture;

    public CanvasPanel() {
        controlPanel = null;
        rooms = new ArrayList<>();
        doors = new ArrayList<>();
        windows = new ArrayList<>();
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 700));
        CanvasPanel canvasPanel = this;
        //ToolTipManager.sharedInstance().setInitialDelay(10);
        //ToolTipManager.sharedInstance().setDismissDelay(2000);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
                selectRoomAt(e.getPoint());
                if(selectedRoom!=null){
                    if(e.getButton()==MouseEvent.BUTTON3){
                        //check if furniture already present
                        //if it is, then rotate it, else add furniture
                        // Code to add furniture to selected room
                        //Feature removed
                        
                        //boolean notRotated = true;
                        String type = controlPanel.getFurnitureType();
                        Furniture f = new Furniture(selectedRoom, type, e.getX(), e.getY());
                        for(Furniture f1 : selectedRoom.getFurnitures()){
                            if(f1.contains(e.getXOnScreen(), e.getYOnScreen())){
                                //f.rotateFurniture();
                                //repaint();
                                //notRotated = false;
                                //break;
                                JOptionPane.showMessageDialog(canvasPanel, "Furniture already exists at this location!");
                                return;
                            }
                            else if(f.overlaps(f1)){
                                JOptionPane.showMessageDialog(canvasPanel, "Furniture overlaps with already existing furniture!");
                                return;
                            }
                        }    
                        addFurniture(f);
                    }
                }
            }
            public void mouseReleased(MouseEvent e){
                if(e.getButton()==MouseEvent.BUTTON1){
                    if(selectedRoom!=null){
                        if(isOverlap()){
                            selectedRoom.setX(selectedRoom.getOriginalX());
                            selectedRoom.setY(selectedRoom.getOriginalY());
                            selectedRoom.setLocation(selectedRoom.getOriginalX(), selectedRoom.getOriginalY());
                            selectedRoom.setSize(selectedRoom.getWidth(), selectedRoom.getHeight());
                            selectedRoom = null;
                        }
                        else{
                            selectedRoom.updateOriginalCoord();
                            selectedRoom.setLocation(selectedRoom.getX(), selectedRoom.getY());
                            selectedRoom.setSize(selectedRoom.getWidth(), selectedRoom.getHeight());
                            //remove doors and windows
                            for(Door door: doors){
                                if(door.getR1()==selectedRoom){
                                    door.setforRemoval();
                                }
                            }
                            for(Window window: windows){
                                if(window.getRoom()==selectedRoom){
                                    window.setforRemoval();;
                                }
                                if(window.isWindowbetweenRooms(rooms)){
                                    window.setforRemoval();
                                }
                            }
                            //selectedRoom.setToolTipText("Room: "+selectedRoom.name+"\nRoom ID: "+selectedRoom.getID());
                            //selectedRoom.getToolTip().setLocation(selectedRoom.getX(), selectedRoom.getY());
                            selectedRoom=null;
                        }
                        repaint();
                    }
                    
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedRoom != null) {
                    int dx = e.getX() - dragStart.x;
                    int dy = e.getY() - dragStart.y;
                    if(selectedRoom!=null){
                        selectedRoom.setX(selectedRoom.getX() + dx);
                        selectedRoom.setY(selectedRoom.getY() + dy);  
                    }
                    dragStart = e.getPoint();
                    repaint();
                }
            }
        });
    }

    public void addRoom(Room room) {
        for (Room r : rooms) {
            if (r.overlaps(room)) {
                JOptionPane.showMessageDialog(this, "Room overlaps with an existing room.");
                return;
            }
        }
        rooms.add(room);
        room.setID(rooms.size()-1);
        //room.setToolTipText("Room: "+room.name+"\nRoom ID: "+room.getID());
        room.setBounds(room.getX(), room.getY(), room.getWidth(), room.getHeight());
        this.add(room);
        //patchfix implementation -> implement with List of windows in room later ->reduces no. of iterations
        for(Window w: windows){
            if(w.isWindowbetweenRooms(rooms)){
                w.setforRemoval();
            }
        }
        //this.add(room.display);
        repaint();
    }

    public void addRoomRelative(Room newRoom, Room relativeRoom, String direction) {
        /*switch (direction.toLowerCase()) {
            case "south":
                newRoom.setX(relativeRoom.getX());
                newRoom.setY(relativeRoom.getY() + relativeRoom.getHeight());
                newRoom.setID(rooms.size()-1);
                newRoom.updateOriginalCoord();
                break;
            case "north":
                newRoom.setX(relativeRoom.getX());
                newRoom.setY(relativeRoom.getY() - newRoom.getHeight());
                newRoom.setID(rooms.size()-1);
                newRoom.updateOriginalCoord();
                break;
            case "east":
                newRoom.setX(relativeRoom.getX() + relativeRoom.getWidth());
                newRoom.setY(relativeRoom.getY());
                newRoom.setID(rooms.size()-1);
                newRoom.updateOriginalCoord();
                break;
            case "west":
                newRoom.setX(relativeRoom.getX() - newRoom.getWidth());
                newRoom.setY(relativeRoom.getY());
                newRoom.setID(rooms.size()-1);
                newRoom.updateOriginalCoord();
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid direction.");
                return;
        }
        addRoom(newRoom);*/
        switch (direction.toLowerCase()) {
            case "south":
            case "south left":
                newRoom.setX(relativeRoom.getX());
                newRoom.setY(relativeRoom.getY() + relativeRoom.getHeight());
                newRoom.setID(rooms.size()-1);
                newRoom.updateOriginalCoord();
                break;
            case "south center":
            case "south centre":
                newRoom.setX(relativeRoom.getX()+(relativeRoom.getWidth()-newRoom.getWidth())/2);
                newRoom.setY(relativeRoom.getY() + relativeRoom.getHeight());
                newRoom.setID(rooms.size()-1);
                newRoom.updateOriginalCoord();
                break;
            case "south right":
                newRoom.setX(relativeRoom.getX() + (relativeRoom.getWidth()-newRoom.getWidth()));
                newRoom.setY(relativeRoom.getY() + relativeRoom.getHeight());
                newRoom.setID(rooms.size()-1);
                newRoom.updateOriginalCoord();
                break;
            case "north":
            case "north left":
                newRoom.setX(relativeRoom.getX());
                newRoom.setY(relativeRoom.getY() - newRoom.getHeight());
                newRoom.setID(rooms.size()-1);
                newRoom.updateOriginalCoord();
                break;
            case "north centre":
            case "north center":
                newRoom.setX(relativeRoom.getX()+(relativeRoom.getWidth()-newRoom.getWidth())/2);
                newRoom.setY(relativeRoom.getY() - newRoom.getHeight());
                newRoom.setID(rooms.size()-1);
                newRoom.updateOriginalCoord();
                break;
            case "north right":
                newRoom.setX(relativeRoom.getX() + (relativeRoom.getWidth()-newRoom.getWidth()));
                newRoom.setY(relativeRoom.getY() - newRoom.getHeight());
                newRoom.setID(rooms.size()-1);
                newRoom.updateOriginalCoord();
                break;
            case "east":
            case "east left":
                newRoom.setX(relativeRoom.getX() + relativeRoom.getWidth());
                newRoom.setY(relativeRoom.getY());
                newRoom.setID(rooms.size()-1);
                newRoom.updateOriginalCoord();
                break;
            case "east centre":
                newRoom.setX(relativeRoom.getX() + relativeRoom.getWidth());
                newRoom.setY(relativeRoom.getY()+ ( relativeRoom.getHeight() - newRoom.getHeight() )/2);
                newRoom.setID(rooms.size()-1);
                newRoom.updateOriginalCoord();
                break;
            case "east right":
                newRoom.setX(relativeRoom.getX() + relativeRoom.getWidth());
                newRoom.setY(relativeRoom.getY()+ ( relativeRoom.getHeight() - newRoom.getHeight() ));
                newRoom.setID(rooms.size()-1);
                newRoom.updateOriginalCoord();
                break;
            case "west":
            case "west left":
                newRoom.setX(relativeRoom.getX() - newRoom.getWidth());
                newRoom.setY(relativeRoom.getY());
                newRoom.setID(rooms.size()-1);
                newRoom.updateOriginalCoord();
                break;
            case "west centre":
            case "west center":
                newRoom.setX(relativeRoom.getX() - newRoom.getWidth());
                newRoom.setY(relativeRoom.getY()+( relativeRoom.getHeight() - newRoom.getHeight() )/2);
                newRoom.setID(rooms.size()-1);
                newRoom.updateOriginalCoord();
                break;
            case "west right":
                newRoom.setX(relativeRoom.getX() - newRoom.getWidth());
                newRoom.setY(relativeRoom.getY()+ ( relativeRoom.getHeight() - newRoom.getHeight() ));
                newRoom.setID(rooms.size()-1);
                newRoom.updateOriginalCoord();
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid direction.");
                return;
        }
        addRoom(newRoom);
    }

    public void addDoor(Door door) {
        doors.add(door);
        repaint();
    }

    public void addWindow(Window window) {
        windows.add(window);
        repaint();
    }

    public void addFurniture(Furniture furnitureItem) {
        selectedRoom.addFurniture(furnitureItem);
        repaint();
    }

    private void selectRoomAt(Point point) {
        selectedRoom = null;
        for (Room room : rooms) {
            if (room.contains(point)) {
                selectedRoom = room;
                break;
            }
        }
    }

    private boolean isOverlap() {
        for (Room room : rooms) {
            if (room != selectedRoom && room.overlaps(selectedRoom)) {
                JOptionPane.showMessageDialog(this, "Room overlaps with another room.");
                return true;
            }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Room room : rooms) {
            room.draw(g);
        }
        Iterator<Door> it_d = doors.iterator();
        while(it_d.hasNext()){
            Door door = it_d.next();
            if(door.getRemovalStatus()){
                it_d.remove();
            }
            else{
                door.draw(g);
            }
        }
        Iterator<Window> it_w = windows.iterator();
        while(it_w.hasNext()){
            Window window = it_w.next();
            if(window.getRemovalStatus()){
                it_w.remove();
            }
            else{
                window.draw(g);
            }
        }
    }

    public List<Room> getRooms() {
        return rooms;
    }
    public List<Door> getDoors(){
        return doors;
    }
    public List<Window> getWindows(){
        return windows;
    }

    public void setControlPanel(ControlPanel cp){this.controlPanel = cp;}
    
    //saving plan
    public void saveCurrentPlan() throws IOException{
	JFileChooser c=new JFileChooser();
	int rVal = c.showSaveDialog(this);
    if (rVal == JFileChooser.APPROVE_OPTION) {
        String filename = c.getCurrentDirectory().toString()+"\\"+c.getSelectedFile().getName();
        c.getSelectedFile().setExecutable(true);
        FloorPlanFileHandler.savePlan(rooms,doors,windows,filename);
    }
	
    }
    
    public void loadCurrentPlan() throws IOException{
	JFileChooser c = new JFileChooser();
	int rVal = c.showOpenDialog(this);
	
		if (rVal == JFileChooser.APPROVE_OPTION) {
        String filename=c.getCurrentDirectory().toString()+"\\"+c.getSelectedFile().getName();
       FloorPlanFileHandler.loadPlan(filename,rooms,doors,windows);
      }

        repaint();
    }

    public void clearAll(){
        rooms.clear();
        doors.clear();
        windows.clear();
        selectedRoom = null;
        repaint();
    }

}
