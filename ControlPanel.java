import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;  // Import the List interface
import java.io.*;



class ControlPanel extends JPanel {
    private CanvasPanel canvasPanel;
    private ControlPanel controlPanel = this;
    private JTextField xField, yField, widthField, heightField;
    private JTextField relativeRoomField, directionField, room1Field, room2Field, lengthField, fillerField;
    private JButton colorButton, addButton, addRelativeButton, addDoorButton, addWindowButton, savePlanButton, 
        loadPlanButton, clearButton, displayButton, clearAllButton, furnitureRotateButton;
    private Color selectedColor = Color.LIGHT_GRAY;
    private JComboBox<String> furnitureComboBox, roomNameComboBox;  
    private int rotateAngle;


    public ControlPanel(CanvasPanel canvasPanel) {
        this.canvasPanel = canvasPanel;
        rotateAngle = 0;
        setPreferredSize(new Dimension(400, 700));
	    setLayout(new GridLayout(18, 1));	
        add(new JLabel("X:"));
        xField = new JTextField(5);
        add(xField);

        add(new JLabel("Y:"));
        yField = new JTextField(5);
        add(yField);

        add(new JLabel("Width:"));
        widthField = new JTextField(5);
        add(widthField);

        add(new JLabel("Height:"));
        heightField = new JTextField(5);
        add(heightField);
        
        add(new JLabel("Room Name"));
        roomNameComboBox = new JComboBox<>(new String[]{"Bedroom", "Bathroom", "Living Room", "Kitchen", "Guest Room", "Study", "Garage", "Empty Room"});
        add(roomNameComboBox);

        colorButton = new JButton("Color Chooser");
        colorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color color = JColorChooser.showDialog(null, "Choose Room Color", selectedColor);
                if (color != null) {
                    selectedColor = color;
                }
            }
        });
        add(colorButton);

        addButton = new JButton("Add Room");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int x = Integer.parseInt(xField.getText());
                    int y = Integer.parseInt(yField.getText());
                    int width = Integer.parseInt(widthField.getText());
                    int height = Integer.parseInt(heightField.getText());
                    String name = (String)roomNameComboBox.getSelectedItem();
                    Room room = new Room(x, y, width, height, selectedColor, name);
                    canvasPanel.addRoom(room);
		    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ControlPanel.this, "Invalid input. Please check your values.");
                }	
            }
        });
        add(addButton);

        add(new JLabel("Relative Room ID:"));
        relativeRoomField = new JTextField(5);
        add(relativeRoomField);

        add(new JLabel("Direction (south, north, east, west):"));
        directionField = new JTextField(10);
        add(directionField);

        addRelativeButton = new JButton("Add Relative Room");
        addRelativeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int width = Integer.parseInt(widthField.getText());
                    int height = Integer.parseInt(heightField.getText());
                    String name = (String)roomNameComboBox.getSelectedItem();
                    Room newRoom = new Room(0, 0, width, height, selectedColor, name);
                    Room relativeRoom = findRoomById(relativeRoomField.getText());
                    if (relativeRoom != null) {
                        String direction = directionField.getText();
                        canvasPanel.addRoomRelative(newRoom, relativeRoom, direction);
			
                    } else {
                        JOptionPane.showMessageDialog(ControlPanel.this, "Relative room not found.");
                    }
                } catch (NumberFormatException ex) {
                    //ex.printStackTrace();
                    JOptionPane.showMessageDialog(ControlPanel.this, "Invalid input. Please check your values.");
                }
            }
        });
        add(addRelativeButton);

        
        displayButton = new JButton("Show Room ID");
        displayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                JFrame f1 = new JFrame("Room IDs with coordinates");
                //f1.setSize(1200, 800);
                f1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f1.setBounds(200,200,800,800);
                f1.setVisible(true);
                f1.setLayout(null);
                
                JTextArea t1 = new JTextArea("Rooms:");
                t1.setBounds(0,0,1000,1000);
                List<Room> rooms = canvasPanel.getRooms();
                for(Room room: rooms){
                    t1.append("\nRoom name: "+room.name+"\tRoom coordinates: ("+room.getX()+", "+room.getY()+
                        ")\t"+" Room ID: "+room.getID());
                }
                t1.setEditable(false);
                t1.setOpaque(true);
                t1.setVisible(true);
                t1.selectAll();
                JScrollPane s1 = new JScrollPane(t1);
                s1.setBounds(0,0,600,600);
                s1.setOpaque(true);
                s1.setVisible(true);
                
                s1.add(t1);
                f1.add(s1);

            }
        });
        add(displayButton);

        add(new JLabel("Room 1: "));
        room1Field = new JTextField(5);
        add(room1Field);

        add(new JLabel("Room 2 / Direction: "));
        room2Field = new JTextField(5);
        add(room2Field);

        add(new JLabel("Length: "));
        lengthField = new JTextField(5);
        add(lengthField);

        add(new JLabel("Filler"));
        fillerField = new JTextField(5);
        add(fillerField);

        // Add doors, windows, and furniture
        addDoorButton = new JButton("Add Door");
        addDoorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    List<Room> rooms = canvasPanel.getRooms();
                    if(rooms.size()==0){return;}
                    Room r1 = null, r2 = null;
                    String outside = "";
                    int dlength = 0;
                    int filler = 0;
                    try{
                        try{
                            r1 = rooms.get(Integer.parseInt(room1Field.getText()));
                        }catch(NumberFormatException ex){
                            JOptionPane.showMessageDialog(ControlPanel.this, "Enter an integer as room id");
                            return;
                        }
                        try{                      
                            r2 = rooms.get(Integer.parseInt(room2Field.getText()));
                        }catch(NumberFormatException ex){
                            outside = room2Field.getText().toLowerCase();//stores direction of wall
                        }
                        String dlength_dir = lengthField.getText();
                        try{
                            filler = Integer.parseInt(fillerField.getText());
                            if(filler<0){
                                JOptionPane.showMessageDialog(controlPanel, "Filler can't be negative");
                                return;
                            }
                        }catch(NumberFormatException ex){
                            if(dlength_dir.equals("w")){}
                            else{
                                JOptionPane.showMessageDialog(ControlPanel.this, "Enter an integer as filler value");
                                return;
                            }
                        }

                        
                        if(dlength_dir.length()==2){
                            dlength = dlength_dir.charAt(0)-'0';
                            if(!(0<=dlength && dlength<=9)){return;}
                        }
                        else{
                            dlength = Integer.parseInt(dlength_dir.substring(0, dlength_dir.length()-1));
                        }
                        if(dlength<0){
                            JOptionPane.showMessageDialog(ControlPanel.this, "Door length can't be negative. Please check your values.");
                            return;
                        }
                        if(dlength_dir.charAt(dlength_dir.length()-1)=='L' || dlength_dir.charAt(dlength_dir.length()-1)=='l' ||
                                dlength_dir.charAt(dlength_dir.length()-1)=='R' || dlength_dir.charAt(dlength_dir.length()-1)=='r' ||
                                dlength_dir.charAt(dlength_dir.length()-1)=='U' || dlength_dir.charAt(dlength_dir.length()-1)=='u' ||
                                dlength_dir.charAt(dlength_dir.length()-1)=='D' || dlength_dir.charAt(dlength_dir.length()-1)=='d'){
                            
                            if(r2==null && !outside.equals("")){
                                Door door = new Door(r1, dlength, dlength_dir.charAt(dlength_dir.length()-1), outside, filler);
                                /*for(Door d1: canvasPanel.getDoors()){
                                    Point p1 = new Point(d1.getX1(), d1.getY1());
                                    Point p2 = new Point(d1.getX2(), d1.getY2());
                                    if(door.contains(p1) || door.contains(p2)){
                                        JOptionPane.showMessageDialog(canvasPanel, "Door overlap!");
                                        return;
                                    }
                                }*/
                                canvasPanel.addDoor(door);
                            }
                            else{
                                Door door = new Door(r1, r2, dlength, dlength_dir.charAt(dlength_dir.length()-1), filler);
                                /*for(Door d1: canvasPanel.getDoors()){
                                    Point p1 = new Point(d1.getX1(), d1.getY1());
                                    Point p2 = new Point(d1.getX2(), d1.getY2());
                                    if(door.contains(p1) || door.contains(p2)){
                                        JOptionPane.showMessageDialog(canvasPanel, "Door overlap!");
                                        return;
                                    }
                                }*/
                                canvasPanel.addDoor(door);
                            }
                        }
                        else{
                            JOptionPane.showMessageDialog(ControlPanel.this, "Length string is invalid. Please check your values.");
                            return;
                        }
                    }catch (NumberFormatException ex) {
                        if(lengthField.getText().toLowerCase().equals("w") && r1!=null && r2!=null){
                            //dlength = -1;//to indicate that user wants wall length as door length
                            Door door = new Door(r1, r2, -1, lengthField.getText().charAt(lengthField.getText().length()-1), filler);
                            canvasPanel.addDoor(door);
                        }
                        else if(r2==null && lengthField.getText().toLowerCase().equals("w") && r1!=null){
                            Door door = new Door(r1, -1, 
                                lengthField.getText().charAt(lengthField.getText().length()-1), 
                                outside, filler);
                                /*for(Door d1: canvasPanel.getDoors()){
                                    Point p1 = new Point(d1.getX1(), d1.getY1());
                                    Point p2 = new Point(d1.getX2(), d1.getY2());
                                    if(door.contains(p1) || door.contains(p2)){
                                        JOptionPane.showMessageDialog(canvasPanel, "Door overlap!");
                                        return;
                                    }
                                }*/
                            canvasPanel.addDoor(door);
                        }
                        else{
                            JOptionPane.showMessageDialog(ControlPanel.this, "Invalid input. Please check your values.");
                            return;
                        }
                    }     
            }
        });
        add(addDoorButton);
        
        
        addWindowButton = new JButton("Add Window");
        addWindowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                List<Room> rooms = canvasPanel.getRooms();
                if(rooms.size()==0){return;}
                Room r1 = null;
                int wlength = 0;
                String wall_selector = room2Field.getText().toLowerCase();
                int filler = -1;
                try{
                    r1 = rooms.get(Integer.parseInt(room1Field.getText()));
                    String wlength_dir = lengthField.getText().toLowerCase();
                    //try catch block for filler
                    try{
                        filler = Integer.parseInt(fillerField.getText());
                        if(filler<0){
                            JOptionPane.showMessageDialog(controlPanel, "Filler can't be negative");
                            return;
                        }
                    }catch(NumberFormatException ex){
                        if(wlength_dir.equals("w")){}
                        else{
                            JOptionPane.showMessageDialog(controlPanel, "Enter an integer as filler value");
                            return;
                        }
                    }
                    if(wlength_dir.length()==2){
                        wlength = wlength_dir.charAt(0)-'0';
                        if(!(0<=wlength && wlength<=9)){return;}
                    }
                    else{
                        wlength = Integer.parseInt(wlength_dir.substring(0, wlength_dir.length()-1));
                    }
                    if(wlength<0){
                        JOptionPane.showMessageDialog(ControlPanel.this, "Window length can't be negative. Please check your values.");
                        return;
                    }
                    char dir = wlength_dir.charAt(wlength_dir.length()-1);
                    if(r1.getWall(wall_selector)==null){
                        JOptionPane.showMessageDialog(ControlPanel.this, "Invalid direction. Please check your values.");
                        return;
                    }
                    
                    Window w1 = new Window(r1, wlength, dir, wall_selector, filler);
                    if(w1.isWindowbetweenRooms(rooms)){
                        JOptionPane.showMessageDialog(ControlPanel.this, "Window is between rooms. Please check your values.");
                        return;
                    }
                    for(Window w: canvasPanel.getWindows()){
                        Point p1 = new Point(w.getX1(), w.getY1());
                        Point p2 = new Point(w.getX2(), w.getY2());
                        if(w1.contains(p1) || w1.contains(p2)){
                            JOptionPane.showMessageDialog(canvasPanel, "Window overlap!");
                            return;
                        }
                    }
                    
                    canvasPanel.addWindow(w1);
                }catch (NumberFormatException ex) {
                    if(lengthField.getText().toLowerCase().equals("w") && r1!=null){
                        wlength = -1;//to indicate that user wants wall length as door length
                        if(r1.getWall(wall_selector)==null){
                            JOptionPane.showMessageDialog(ControlPanel.this, "Invalid direction. Please check your values.");
                            return;
                        }
                        Window w = new Window(r1, wlength, 
                            lengthField.getText().toUpperCase().charAt(lengthField.getText().length()-1), 
                            wall_selector, 0);
                        if(w.isWindowbetweenRooms(rooms)){
                            JOptionPane.showMessageDialog(ControlPanel.this, "Window is between rooms. Please check your values.");
                            return;
                        }
                        for(Window w1: canvasPanel.getWindows()){
                            Point p1 = new Point(w1.getX1(), w1.getY1());
                            Point p2 = new Point(w1.getX2(), w1.getY2());
                            if(w.contains(p1) || w.contains(p2)){
                                JOptionPane.showMessageDialog(canvasPanel, "Window overlap!");
                                return;
                            }
                        }
                        
                        canvasPanel.addWindow(w);
                    }
                    else{
                        JOptionPane.showMessageDialog(ControlPanel.this, "Invalid input. Please check your values.");
                    }
                } 
            }
        });
        add(addWindowButton);
        

        furnitureComboBox = new JComboBox<>(new String[]{"Single Bed", "Dining Table","Double Bed", 
            "Kitchen Sink", "Shower", "Washbasin", "Sofa", "Triple Sofa", "Stove", "Toilet"});
        add(furnitureComboBox);


	
        savePlanButton = new JButton("Save Plan");
        savePlanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)  {
		        try {
		            canvasPanel.saveCurrentPlan();
		        }
		        catch(IOException ex) {
                    JOptionPane.showMessageDialog(controlPanel, "Failed to save plan!");
		            ex.printStackTrace();
		        }
            }
        });
        add(savePlanButton);
	
        loadPlanButton = new JButton("Load Plan");
        loadPlanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
		            canvasPanel.loadCurrentPlan();
		        }
		        catch(IOException ex) {
                    JOptionPane.showMessageDialog(controlPanel, "Failed to load plan!");
		            ex.printStackTrace();
		        }

            }
        });
        add(loadPlanButton);
        
        clearButton = new JButton("Clear Inputs");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xField.setText("");
	            yField.setText("");
	            widthField.setText("");
                heightField.setText("");
	            relativeRoomField.setText("");
               directionField.setText("");
            }
        });
        add(clearButton);

        clearAllButton = new JButton("Clear canvas");
        clearAllButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                canvasPanel.clearAll();
            }
        });
        add(clearAllButton);



    }

    private Room findRoomById(String id) {
        // Assume that ID is the index or some identifier for simplicity.
        int index = Integer.parseInt(id);
        List<Room> rooms = canvasPanel.getRooms();  // Ensure the import for List is present.
        return (index >= 0 && index < rooms.size()) ? rooms.get(index) : null;
    }
    public String getFurnitureType(){
        return (String) furnitureComboBox.getSelectedItem();
    }
}
