public class Main {
    public static void main(String[] args) {
        AquaRouteSystem system = new AquaRouteSystem();
        System.out.println("===== AquaRoute Water Supply System =====");

        while (true) {
            System.out.println("\n---------- DAY " + system.day + " ----------");
            System.out.println("1. Add Water Request");
            System.out.println("2. Add Tanker");
            System.out.println("3. View All Requests");
            System.out.println("4. View Tankers");
            System.out.println("5. Schedule Today's Trips");
            System.out.println("6. View Today's Trips");
            System.out.println("7. End Day");
            System.out.println("8. Area Wise Report");
            System.out.println("0. Exit");

            int choice = system.readInt("Enter your choice: ");
            if (choice == 1) system.addRequest();
            else if (choice == 2) system.addTanker();
            else if (choice == 3) system.viewRequests();
            else if (choice == 4) system.viewTankers();
            else if (choice == 5) system.scheduleTrips();
            else if (choice == 6) system.viewTrips();
            else if (choice == 7) system.endDay();
            else if (choice == 8) system.areaReport();
            else if (choice == 0) {
                System.out.println("Thank you. Exiting...");
                break;
            } else {
                System.out.println("Invalid choice. Enter 0 to 8.");
            }
        }
    }
}
