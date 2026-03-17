import java.util.HashMap;

abstract class Room {
    protected int beds;
    protected int size;
    protected double price;

    public Room(int beds, int size, double price) {
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public abstract String getRoomType();

    public void displayDetails() {
        System.out.println("Room Type: " + getRoomType());
        System.out.println("Beds: " + beds);
        System.out.println("Size: " + size + " sq ft");
        System.out.println("Price: $" + price);
    }
}

class SingleRoom extends Room {
    public SingleRoom() { super(1, 200, 100); }
    public String getRoomType() { return "Single Room"; }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super(2, 350, 180); }
    public String getRoomType() { return "Double Room"; }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super(3, 500, 300); }
    public String getRoomType() { return "Suite Room"; }
}

class RoomInventory {
    private HashMap<String, Integer> inventory;

    public RoomInventory() { inventory = new HashMap<>(); }

    public void addRoomType(String roomType, int count) { inventory.put(roomType, count); }

    public int getAvailability(String roomType) { return inventory.getOrDefault(roomType, 0); }

    public void updateAvailability(String roomType, int count) {
        if (inventory.containsKey(roomType)) inventory.put(roomType, count);
    }

    public HashMap<String, Integer> getInventorySnapshot() {
        return new HashMap<>(inventory);
    }

    public void displayInventory() {
        System.out.println("Current Room Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + ": " + inventory.get(type) + " available");
        }
        System.out.println();
    }
}

class RoomSearch {
    private RoomInventory inventory;

    public RoomSearch(RoomInventory inventory) { this.inventory = inventory; }

    public void displayAvailableRooms(Room[] rooms) {
        System.out.println("Available Rooms for Guests:\n");
        for (Room room : rooms) {
            if (inventory.getAvailability(room.getRoomType()) > 0) {
                room.displayDetails();
                System.out.println("Available: " + inventory.getAvailability(room.getRoomType()) + "\n");
            }
        }
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("Welcome to Book My Stay");
        System.out.println("Hotel Booking System v4.1\n");

        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType(single.getRoomType(), 10);
        inventory.addRoomType(doubleRoom.getRoomType(), 5);
        inventory.addRoomType(suite.getRoomType(), 0); // Suite currently unavailable

        Room[] allRooms = { single, doubleRoom, suite };

        RoomSearch search = new RoomSearch(inventory);
        search.displayAvailableRooms(allRooms);

        inventory.displayInventory();
    }
}