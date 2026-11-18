public class Tanker {
    int id;
    String vehicleNumber;
    int capacity;
    int tripsUsed;

    Tanker(int id, String vehicleNumber, int capacity) {
        this.id = id;
        this.vehicleNumber = vehicleNumber;
        this.capacity = capacity;
    }

    int tripsLeft() {
        return 2 - tripsUsed;
    }
}
