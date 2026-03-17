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

public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("Welcome to Book My Stay");
        System.out.println("Hotel Booking System v2.1\n");

        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        int singleAvailable = 10;
        int doubleAvailable = 5;
        int suiteAvailable = 2;

        single.displayDetails();
        System.out.println("Available Rooms: " + singleAvailable + "\n");

        doubleRoom.displayDetails();
        System.out.println("Available Rooms: " + doubleAvailable + "\n");

        suite.displayDetails();
        System.out.println("Available Rooms: " + suiteAvailable);
    }
}