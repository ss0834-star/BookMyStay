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

class RoomInventory {
    private HashMap<String, Integer> inventory;

    public RoomInventory() { inventory = new HashMap<>(); }

    public void addRoomType(String roomType, int count) { inventory.put(roomType, count); }

    public int getAvailability(String roomType) { return inventory.getOrDefault(roomType, 0); }

    public void decrementAvailability(String roomType) {
        if (inventory.containsKey(roomType) && inventory.get(roomType) > 0) {
            inventory.put(roomType, inventory.get(roomType) - 1);
        }
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

    public void displayQueue() {
        System.out.println("Pending Booking Requests:");
        for (Reservation r : queue) {
            System.out.println(r.getGuestName() + " requests " + r.getRoomType());
        }
        System.out.println();
    }
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
        if (inventory.getAvailability(type) > 0) {
            inventory.decrementAvailability(type);

            allocatedRooms.putIfAbsent(type, new HashSet<>());
            String roomId = generateRoomId(type);
            allocatedRooms.get(type).add(roomId);

            System.out.println("Reservation Confirmed for " + r.getGuestName() +
                    " in " + type + " (Room ID: " + roomId + ")");
            return roomId;
        } else {
            System.out.println("No availability for " + type + " requested by " + r.getGuestName());
            return null;
        }
    }

    private String generateRoomId(String roomType) {
        int id = allocatedRooms.get(roomType).size() + 1;
        return roomType.substring(0, 2).toUpperCase() + String.format("%03d", id);
    }

    public void displayAllocatedRooms() {
        System.out.println("\nAllocated Rooms:");
        for (String type : allocatedRooms.keySet()) {
            System.out.println(type + ": " + allocatedRooms.get(type));
        }
        System.out.println();
    }
}

class Service {
    private String name;
    private double cost;

    public Service(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() { return name; }
    public double getCost() { return cost; }
}

class AddOnServiceManager {
    private HashMap<String, List<Service>> reservationServices;

    public AddOnServiceManager() { reservationServices = new HashMap<>(); }

    public void addService(String roomId, Service service) {
        reservationServices.putIfAbsent(roomId, new ArrayList<>());
        reservationServices.get(roomId).add(service);
    }

    public List<Service> getServicesForRoom(String roomId) {
        return reservationServices.getOrDefault(roomId, new ArrayList<>());
    }

    public void displayServices() {
        System.out.println("\nAdd-On Services:");
        for (String roomId : reservationServices.keySet()) {
            List<Service> services = reservationServices.get(roomId);
            double totalCost = 0;
            System.out.println("Room ID: " + roomId);
            for (Service s : services) {
                System.out.println("- " + s.getName() + ": $" + s.getCost());
                totalCost += s.getCost();
            }
            System.out.println("Total Add-On Cost: $" + totalCost + "\n");
        }
    }
}

class ReservationRecord {
    private String guestName;
    private String roomId;
    private String roomType;
    private List<Service> services;

    public ReservationRecord(String guestName, String roomId, String roomType, List<Service> services) {
        this.guestName = guestName;
        this.roomId = roomId;
        this.roomType = roomType;
        this.services = services != null ? new ArrayList<>(services) : new ArrayList<>();
    }

    public String getGuestName() { return guestName; }
    public String getRoomId() { return roomId; }
    public String getRoomType() { return roomType; }
    public List<Service> getServices() { return services; }
}

class BookingHistory {
    private List<ReservationRecord> history;

    public BookingHistory() { history = new ArrayList<>(); }

    public void addRecord(ReservationRecord record) { history.add(record); }

    public void displayHistory() {
        System.out.println("\nBooking History Report:");
        for (ReservationRecord r : history) {
            System.out.println("Guest: " + r.getGuestName() +
                    " | Room: " + r.getRoomType() +
                    " | ID: " + r.getRoomId());
            double totalServiceCost = 0;
            if (!r.getServices().isEmpty()) {
                System.out.println(" Add-On Services:");
                for (Service s : r.getServices()) {
                    System.out.println(" - " + s.getName() + ": $" + s.getCost());
                    totalServiceCost += s.getCost();
                }
                System.out.println(" Total Service Cost: $" + totalServiceCost);
            }
            System.out.println();
        }
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("Welcome to Book My Stay");
        System.out.println("Hotel Booking System v8.1\n");

        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();
        Room[] allRooms = { single, doubleRoom, suite };

        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType(single.getRoomType(), 2);
        inventory.addRoomType(doubleRoom.getRoomType(), 2);
        inventory.addRoomType(suite.getRoomType(), 1);

        RoomSearch search = new RoomSearch(inventory);
        search.displayAvailableRooms(allRooms);

        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        bookingQueue.addRequest(new Reservation("Alice", "Single Room"));
        bookingQueue.addRequest(new Reservation("Bob", "Double Room"));
        bookingQueue.addRequest(new Reservation("Charlie", "Suite Room"));
        bookingQueue.displayQueue();

        BookingService bookingService = new BookingService(inventory);
        AddOnServiceManager addOnManager = new AddOnServiceManager();
        BookingHistory bookingHistory = new BookingHistory();

        while (bookingQueue.hasPendingRequests()) {
            Reservation r = bookingQueue.processNext();
            String roomId = bookingService.confirmBooking(r);
            if (roomId != null) {
                addOnManager.addService(roomId, new Service("Breakfast", 20));
                addOnManager.addService(roomId, new Service("Airport Pickup", 50));

                List<Service> services = addOnManager.getServicesForRoom(roomId);
                bookingHistory.addRecord(new ReservationRecord(r.getGuestName(), roomId, r.getRoomType(), services));
            }
        }

        bookingService.displayAllocatedRooms();
        addOnManager.displayServices();
        inventory.displayInventory();
        bookingHistory.displayHistory();
    }
}