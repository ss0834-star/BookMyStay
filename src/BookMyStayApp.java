import java.util.*;

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

class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) { super(message); }
}

class RoomInventory {
    private HashMap<String, Integer> inventory;

    public RoomInventory() { inventory = new HashMap<>(); }

    public void addRoomType(String roomType, int count) { inventory.put(roomType, count); }

    public int getAvailability(String roomType) { return inventory.getOrDefault(roomType, 0); }

    public void decrementAvailability(String roomType) throws InvalidBookingException {
        if (!inventory.containsKey(roomType))
            throw new InvalidBookingException("Invalid room type: " + roomType);
        if (inventory.get(roomType) <= 0)
            throw new InvalidBookingException("No available rooms for: " + roomType);
        inventory.put(roomType, inventory.get(roomType) - 1);
    }

    public void displayInventory() {
        System.out.println("Current Room Inventory:");
        for (String type : inventory.keySet())
            System.out.println(type + ": " + inventory.get(type) + " available");
        System.out.println();
    }
}

class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

class BookingRequestQueue {
    private Queue<Reservation> queue;

    public BookingRequestQueue() { queue = new LinkedList<>(); }

    public void addRequest(Reservation r) { queue.offer(r); }

    public Reservation processNext() { return queue.poll(); }

    public boolean hasPendingRequests() { return !queue.isEmpty(); }
}

class BookingService {
    private RoomInventory inventory;
    private HashMap<String, Set<String>> allocatedRooms;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
        allocatedRooms = new HashMap<>();
    }

    public String confirmBooking(Reservation r) {
        String type = r.getRoomType();
        try {
            inventory.decrementAvailability(type);
            allocatedRooms.putIfAbsent(type, new HashSet<>());
            String roomId = generateRoomId(type);
            allocatedRooms.get(type).add(roomId);
            System.out.println("Reservation Confirmed for " + r.getGuestName() +
                    " in " + type + " (Room ID: " + roomId + ")");
            return roomId;
        } catch (InvalidBookingException e) {
            System.out.println("Booking Failed: " + e.getMessage());
            return null;
        }
    }

    private String generateRoomId(String roomType) {
        int id = allocatedRooms.get(roomType).size() + 1;
        return roomType.substring(0, 2).toUpperCase() + String.format("%03d", id);
    }

    public void displayAllocatedRooms() {
        System.out.println("\nAllocated Rooms:");
        for (String type : allocatedRooms.keySet())
            System.out.println(type + ": " + allocatedRooms.get(type));
        System.out.println();
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("Welcome to Book My Stay");
        System.out.println("Hotel Booking System v9.0\n");

        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();
        Room[] allRooms = { single, doubleRoom, suite };

        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType(single.getRoomType(), 2);
        inventory.addRoomType(doubleRoom.getRoomType(), 2);
        inventory.addRoomType(suite.getRoomType(), 1);

        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        bookingQueue.addRequest(new Reservation("Alice", "Single Room"));
        bookingQueue.addRequest(new Reservation("Bob", "Double Room"));
        bookingQueue.addRequest(new Reservation("Charlie", "Invalid Room")); // invalid input test

        BookingService bookingService = new BookingService(inventory);

        while (bookingQueue.hasPendingRequests()) {
            Reservation r = bookingQueue.processNext();
            bookingService.confirmBooking(r);
        }

        bookingService.displayAllocatedRooms();
        inventory.displayInventory();
    }
}