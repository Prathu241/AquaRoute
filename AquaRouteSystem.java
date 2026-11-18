import java.util.ArrayList;
import java.util.Scanner;

public class AquaRouteSystem {
    ArrayList<Request> requests = new ArrayList<Request>();
    ArrayList<Tanker> tankers = new ArrayList<Tanker>();
    ArrayList<String> todayTrips = new ArrayList<String>();
    Scanner scanner = new Scanner(System.in);
    int day = 1;
    int nextRequestId = 101;
    int nextTankerId = 1;

    int readInt(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException error) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    String readArea() {
        while (true) {
            System.out.print("Enter area name: ");
            String area = scanner.nextLine().trim().replaceAll(" +", " ");
            if (area.length() == 0) {
                System.out.println("Area name cannot be empty.");
            } else if (!Character.isLetter(area.charAt(0))) {
                System.out.println("Area name should start with a letter.");
            } else {
                return area.substring(0, 1).toUpperCase() + area.substring(1).toLowerCase();
            }
        }
    }

    void addRequest() {
        String area = readArea();
        int litres = readInt("Enter litres needed: ");
        while (litres < 100 || litres > 100000) {
            System.out.println("Litres must be between 100 and 100000.");
            litres = readInt("Enter litres needed: ");
        }

        System.out.println("1. Hospital  2. School  3. Residential");
        int priority = readInt("Enter priority: ");
        while (priority < 1 || priority > 3) {
            System.out.println("Please enter 1, 2 or 3.");
            priority = readInt("Enter priority: ");
        }

        Request request = new Request(nextRequestId, area, litres, priority, day);
        requests.add(request);
        nextRequestId++;
        System.out.println("Request R" + request.id + " added for " + area + ".");
    }

    void addTanker() {
        System.out.print("Enter vehicle number: ");
        String vehicle = scanner.nextLine().trim().toUpperCase();
        if (vehicle.length() == 0) {
            System.out.println("Vehicle number cannot be empty.");
            return;
        }

        for (Tanker tanker : tankers) {
            if (tanker.vehicleNumber.equals(vehicle)) {
                System.out.println("This tanker is already added.");
                return;
            }
        }

        int capacity = readInt("Enter capacity in litres: ");
        while (capacity < 1000 || capacity > 50000) {
            System.out.println("Capacity must be between 1000 and 50000.");
            capacity = readInt("Enter capacity in litres: ");
        }

        Tanker tanker = new Tanker(nextTankerId, vehicle, capacity);
        tankers.add(tanker);
        nextTankerId++;
        System.out.println("Tanker T" + tanker.id + " added with capacity " + capacity + " litres.");
    }

    ArrayList<Request> pendingRequests() {
        ArrayList<Request> pending = new ArrayList<Request>();
        for (Request request : requests) {
            if (request.remaining() > 0) pending.add(request);
        }

        for (int i = 0; i < pending.size() - 1; i++) {
            for (int j = 0; j < pending.size() - 1 - i; j++) {
                Request first = pending.get(j);
                Request second = pending.get(j + 1);
                boolean swap = first.priority > second.priority;
                if (first.priority == second.priority && first.dayRaised > second.dayRaised) swap = true;
                if (first.priority == second.priority && first.dayRaised == second.dayRaised
                        && first.id > second.id) swap = true;
                if (swap) {
                    pending.set(j, second);
                    pending.set(j + 1, first);
                }
            }
        }
        return pending;
    }

    Tanker findTanker(int needed) {
        Tanker best = null;
        Tanker biggest = null;
        for (Tanker tanker : tankers) {
            if (tanker.tripsLeft() == 0) continue;
            if (tanker.capacity >= needed && (best == null || tanker.capacity < best.capacity)) {
                best = tanker;
            }
            if (biggest == null || tanker.capacity > biggest.capacity) biggest = tanker;
        }
        if (best != null) return best;
        return biggest;
    }

    void deliver(Request request, int litres) {
        request.delivered += litres;
        if (request.remaining() == 0) request.dayServed = day;
    }

    void scheduleTrips() {
        if (tankers.size() == 0) {
            System.out.println("No tankers added. Add a tanker first.");
            return;
        }

        ArrayList<Request> pending = pendingRequests();
        if (pending.size() == 0) {
            System.out.println("No pending requests.");
            return;
        }
        if (findTanker(1) == null) {
            System.out.println("All tankers have finished today's trips. Please end the day.");
            return;
        }

        int count = 0;
        for (Request request : pending) {
            while (request.remaining() > 0) {
                Tanker tanker = findTanker(request.remaining());
                if (tanker == null) break;

                int load = Math.min(tanker.capacity, request.remaining());
                deliver(request, load);
                String trip = "T" + tanker.id + " (trip " + (tanker.tripsUsed + 1) + ") -> "
                        + request.area + " : R" + request.id + " " + load + "L";

                int space = tanker.capacity - load;
                for (Request other : pending) {
                    if (space == 0) break;
                    if (other != request && other.remaining() > 0 && other.area.equals(request.area)) {
                        int extra = Math.min(space, other.remaining());
                        deliver(other, extra);
                        space -= extra;
                        trip += ", R" + other.id + " " + extra + "L";
                    }
                }

                tanker.tripsUsed++;
                todayTrips.add(trip);
                count++;
            }
        }

        System.out.println(count + " trip(s) scheduled.");
        viewTrips();
        int left = pendingRequests().size();
        if (left > 0) System.out.println(left + " request(s) still pending. They will go to next day.");
    }

    void viewRequests() {
        if (requests.size() == 0) {
            System.out.println("No requests yet.");
            return;
        }
        for (Request request : requests) {
            System.out.println("R" + request.id + " | " + request.area + " | "
                    + request.priorityName() + " | Needed: " + request.litres
                    + " | Delivered: " + request.delivered + " | " + request.status()
                    + " | Day " + request.dayRaised);
        }
    }

    void viewTankers() {
        if (tankers.size() == 0) {
            System.out.println("No tankers yet.");
            return;
        }
        for (Tanker tanker : tankers) {
            System.out.println("T" + tanker.id + " | " + tanker.vehicleNumber
                    + " | Capacity: " + tanker.capacity + " | Trips left today: " + tanker.tripsLeft());
        }
    }

    void viewTrips() {
        if (todayTrips.size() == 0) {
            System.out.println("No trips scheduled today.");
            return;
        }
        System.out.println("Trips for Day " + day + ":");
        for (int i = 0; i < todayTrips.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + todayTrips.get(i));
        }
    }

    void endDay() {
        ArrayList<Request> pending = pendingRequests();
        if (pending.size() == 0) {
            System.out.println("All requests served. Nothing to carry forward.");
        } else {
            System.out.println("Carried to Day " + (day + 1) + ":");
            for (Request request : pending) {
                System.out.println("  R" + request.id + " " + request.area + " ("
                        + request.priorityName() + ") - " + request.remaining() + "L remaining");
            }
        }

        day++;
        todayTrips.clear();
        for (Tanker tanker : tankers) tanker.tripsUsed = 0;
        System.out.println("Day " + day + " started.");
    }

    void areaReport() {
        if (requests.size() == 0) {
            System.out.println("No requests yet.");
            return;
        }

        ArrayList<String> areas = new ArrayList<String>();
        for (Request request : requests) {
            if (!areas.contains(request.area)) areas.add(request.area);
        }

        System.out.println("Area            Requested   Supplied   Pending   Avg Wait");
        int totalRequested = 0;
        int totalSupplied = 0;

        for (String area : areas) {
            int requested = 0;
            int supplied = 0;
            int served = 0;
            int wait = 0;

            for (Request request : requests) {
                if (request.area.equals(area)) {
                    requested += request.litres;
                    supplied += request.delivered;
                    if (request.dayServed != -1) {
                        served++;
                        wait += request.dayServed - request.dayRaised;
                    }
                }
            }

            String average = served == 0 ? "-" : String.format("%.1f days", (double) wait / served);
            System.out.printf("%-15s %9d %10d %9d   %s%n",
                    area, requested, supplied, requested - supplied, average);
            totalRequested += requested;
            totalSupplied += supplied;
        }

        System.out.printf("%-15s %9d %10d %9d%n",
                "TOTAL", totalRequested, totalSupplied, totalRequested - totalSupplied);
    }
}
