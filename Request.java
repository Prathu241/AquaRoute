public class Request {
    int id;
    String area;
    int litres;
    int delivered;
    int priority;
    int dayRaised;
    int dayServed = -1;

    Request(int id, String area, int litres, int priority, int dayRaised) {
        this.id = id;
        this.area = area;
        this.litres = litres;
        this.priority = priority;
        this.dayRaised = dayRaised;
    }

    int remaining() {
        return litres - delivered;
    }

    String priorityName() {
        if (priority == 1) return "Hospital";
        if (priority == 2) return "School";
        return "Residential";
    }

    String status() {
        if (remaining() == 0) return "SERVED";
        if (delivered > 0) return "PARTIAL";
        return "PENDING";
    }
}
