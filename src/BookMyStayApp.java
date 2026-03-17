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

    public synchronized void addRoomType(String roomType, int count) { inventory.put(roomType, count); }

    public synchronized int getAvailability(String roomType) { return inventory.getOrDefault(roomType, 0); }

    public synchronized void decrementAvailability(String roomType) throws InvalidBookingException {
        if (!inventory.containsKey(roomType)) throw new InvalidBookingException("Invalid room type: " + roomType);
        if (inventory.get(roomType) <= 0) throw new InvalidBookingException("No available rooms for: " + roomType);
        inventory.put(roomType, inventory.get(roomType) - 1);
    }

    public synchronized void incrementAvailability(String roomType) {
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
    }

    public synchronized void displayInventory() {
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

class BookingService {
    private RoomInventory inventory;
    private HashMap<String, Set<String>> allocatedRooms;
    private Stack<String> rollbackStack;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
        allocatedRooms = new HashMap<>();
        rollbackStack = new Stack<>();
    }

    public synchronized String confirmBooking(Reservation r) {
        String type = r.getRoomType();
        try {
            inventory.decrementAvailability(type);
            allocatedRooms.putIfAbsent(type, new HashSet<>());
            String roomId = generateRoomId(type);
            allocatedRooms.get(type).add(roomId);
            rollbackStack.push(roomId);
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

    public synchronized void cancelBooking(String roomType, String roomId) {
        if (allocatedRooms.containsKey(roomType) && allocatedRooms.get(roomType).contains(roomId)) {
            allocatedRooms.get(roomType).remove(roomId);
            inventory.incrementAvailability(roomType);
            rollbackStack.remove(roomId);
            System.out.println("Booking Cancelled: " + roomId + " (" + roomType + ")");
        } else {
            System.out.println("Cancellation Failed: Room ID " + roomId + " not found or already cancelled.");
        }
    }

    public synchronized void displayAllocatedRooms() {
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

    public Service(String name, double cost) { this.name = name; this.cost = cost; }
    public String getName() { return name; }
    public double getCost() { return cost; }
}

class AddOnServiceManager {
    private HashMap<String, List<Service>> reservationServices;

    public AddOnServiceManager() { reservationServices = new HashMap<>(); }

    public synchronized void addService(String roomId, Service service) {
        reservationServices.putIfAbsent(roomId, new ArrayList<>());
        reservationServices.get(roomId).add(service);
    }

    public synchronized List<Service> getServicesForRoom(String roomId) {
        return reservationServices.getOrDefault(roomId, new ArrayList<>());
    }

    public synchronized void displayServices() {
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
    private boolean cancelled;

    public ReservationRecord(String guestName, String roomId, String roomType, List<Service> services) {
        this.guestName = guestName;
        this.roomId = roomId;
        this.roomType = roomType;
        this.services = services != null ? new ArrayList<>(services) : new ArrayList<>();
        this.cancelled = false;
    }

    public String getGuestName() { return guestName; }
    public String getRoomId() { return roomId; }
    public String getRoomType() { return roomType; }
    public List<Service> getServices() { return services; }
    public boolean isCancelled() { return cancelled; }
    public void cancel() { cancelled = true; }
}

class BookingHistory {
    private List<ReservationRecord> history;

    public BookingHistory() { history = new ArrayList<>(); }

    public synchronized void addRecord(ReservationRecord record) { history.add(record); }

    public synchronized void cancelRecord(String roomId) {
        for (ReservationRecord r : history) {
            if (r.getRoomId().equals(roomId)) {
                r.cancel();
                return;
            }
        }
    }

    public synchronized void displayHistory() {
        System.out.println("\nBooking History Report:");
        for (ReservationRecord r : history) {
            String status = r.isCancelled() ? "CANCELLED" : "ACTIVE";
            System.out.println("Guest: " + r.getGuestName() +
                    " | Room: " + r.getRoomType() +
                    " | ID: " + r.getRoomId() +
                    " | Status: " + status);
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

class ConcurrentBookingRequest extends Thread {
    private Reservation reservation;
    private BookingService bookingService;
    private AddOnServiceManager addOnManager;
    private BookingHistory bookingHistory;

    public ConcurrentBookingRequest(Reservation r, BookingService bs,
                                    AddOnServiceManager am, BookingHistory bh) {
        this.reservation = r;
        this.bookingService = bs;
        this.addOnManager = am;
        this.bookingHistory = bh;
    }

    @Override
    public void run() {
        synchronized (bookingService) {
            String roomId = bookingService.confirmBooking(reservation);
            if (roomId != null) {
                addOnManager.addService(roomId, new Service("Breakfast", 20));
                addOnManager.addService(roomId, new Service("Airport Pickup", 50));
                List<Service> services = addOnManager.getServicesForRoom(roomId);
                bookingHistory.addRecord(new ReservationRecord(
                        reservation.getGuestName(), roomId, reservation.getRoomType(), services));
            }
        }
    }
}

public class BookMyStayApp {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Welcome to Book My Stay - v11 (Concurrent Booking)\n");

        Room[] allRooms = { new SingleRoom(), new DoubleRoom(), new SuiteRoom() };

        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Double Room", 2);
        inventory.addRoomType("Suite Room", 1);

        RoomSearch search = new RoomSearch(inventory);
        search.displayAvailableRooms(allRooms);

        BookingService bookingService = new BookingService(inventory);
        AddOnServiceManager addOnManager = new AddOnServiceManager();
        BookingHistory bookingHistory = new BookingHistory();

        Reservation[] reservations = {
                new Reservation("Alice", "Single Room"),
                new Reservation("Bob", "Single Room"),
                new Reservation("Charlie", "Double Room"),
                new Reservation("David", "Double Room"),
                new Reservation("Eve", "Suite Room")
        };

        List<Thread> threads = new ArrayList<>();
        for (Reservation r : reservations) {
            Thread t = new ConcurrentBookingRequest(r, bookingService, addOnManager, bookingHistory);
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) t.join();

        bookingService.displayAllocatedRooms();
        addOnManager.displayServices();
        inventory.displayInventory();
        bookingHistory.displayHistory();

        System.out.println("\n--- Processing Cancellation ---\n");
        bookingService.cancelBooking("Single Room", "SI001");
        bookingHistory.cancelRecord("SI001");

        bookingService.displayAllocatedRooms();
        inventory.displayInventory();
        bookingHistory.displayHistory();
    }
}