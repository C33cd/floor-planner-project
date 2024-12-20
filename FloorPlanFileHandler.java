import java.io.*;
import java.awt.*;
import java.util.List;


class FloorPlanFileHandler extends FloorPlanApp{
    public static void savePlan(List<Room> rooms, List<Door> doors, List<Window> windows, String filePath) throws IOException {
        File f = new File(filePath);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(f))){
            //writer.write("Rooms");
            //writer.newLine();
            for (Room room : rooms) {
                writer.write("Room");
                writer.newLine();
                writer.write(room.getX() + "," + room.getY() + "," + room.getWidth() + "," + 
                    room.getHeight() + "," + Integer.toHexString(room.getColor().getRGB()).substring(2) + ","+
                    room.name);
                writer.newLine();
                writer.write("Furniture");
                for (Furniture item : room.getFurnitures()) {
                    //modify if furniture changed
                    //Properties:private int x, y, width, height, rotateAngle,  Room room, String type, Image img
                    writer.newLine();
                    writer.write(item.getFurnitureType()+","+item.getX()+","+item.getY()+","+
                    item.getRotateAngle());
                    
                }
                writer.newLine();
            }

            writer.write("Doors");
            writer.newLine();
            for (Door door : doors) {
                //private Room room1, room2, componentLength, direction, endpoints, isOpenDoor,  are door properties
                //some can be obtained from others
                writer.write(door.getX1()+","+door.getY1()+","+door.getX2()+","+door.getY2()+","+ 
                    door.isOpenDoor()+","+door.getFiller()+","+(door.getR1()).getID() + ",");
                if(!door.isOpenDoor()){writer.write((door.getR2()).getID()+",");}
                writer.write(door.getWallType());
                writer.newLine();
            }

            writer.write("Windows");
            writer.newLine();
            for (Window window : windows) {
                //Room r, int windowLength, char direction, Point[] wall, Point[] endPoints are the properties of window
                //Write r(room id only), endPoints, wall type in order
                writer.write((window.getRoom()).getID()+","+window.getX1()+","+
                    window.getY1()+","+window.getX2()+","+window.getY2()+","+
                    window.getWallType());
                writer.newLine();
            }
        }catch(FileNotFoundException e){
            return;
        }
    }

    
    public static void loadPlan(String filePath, List<Room> rooms, List<Door> doors, List<Window> windows) throws IOException {
        rooms.clear();
        doors.clear();
        windows.clear();
        if(!(new File(filePath)).exists()){
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String section = "";
            Room r = null;
            while ((line = reader.readLine()) != null) {
                if (line.equals("Room") || line.equals("Doors") || line.equals("Windows") || line.equals("Furniture")) {
                    section = line;
                } 
                else {
                    String[] parts = line.split(",");
                    Point[] endpoints = new Point[2]; 
                    switch (section) {
                        case "Room":
                            int x = Integer.parseInt(parts[0]);
                            int y = Integer.parseInt(parts[1]);
                            int width = Integer.parseInt(parts[2]);
                            int height = Integer.parseInt(parts[3]);
                            Color color = Color.decode("#" + parts[4]);
                            String name = parts[5];
                            r = new Room(x, y, width, height, color, name);
                            r.setID(rooms.size());
                            rooms.add(r);
                            break;
                        case "Doors":
                            endpoints[0] = new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                            endpoints[1] = new Point(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                            boolean isOpenDoor = Boolean.parseBoolean(parts[4]);
                            int filler = Integer.parseInt(parts[5]);
                            r = rooms.get(Integer.parseInt(parts[6]));
                            if(isOpenDoor){
                                String dir = parts[7];
                                doors.add(new Door(endpoints, r, dir, filler));
                            }
                            else{
                                Room r2 = rooms.get(Integer.parseInt(parts[7]));
                                doors.add(new Door(endpoints, r, r2, filler));
                            }
                            break;
                        case "Windows":
                            r = rooms.get(Integer.parseInt(parts[0]));
                            endpoints[0] = new Point(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                            endpoints[1] = new Point(Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
                            String wall_dir = parts[5];
                            windows.add(new Window(endpoints, r, wall_dir));
                            break;
                        case "Furniture":
                            Furniture f = new Furniture(r, parts[0], Integer.parseInt(parts[1])+r.getX(), 
                            Integer.parseInt(parts[2])+r.getY(), Integer.parseInt(parts[3]));
                            r.addFurniture(f);
                            break;
                    }
                }
            }
        }
    }
}
