TRIPS_PER_DAY = 2


def make_system():
    return {
        "day": 1,
        "next_request_id": 101,
        "next_tanker_id": 1,
        "requests": [],
        "tankers": [],
        "today_trips": [],
    }


def clean_area(area):
    words = area.strip().split()
    return " ".join(words).title()


def priority_name(priority):
    if priority == 1:
        return "Hospital"
    if priority == 2:
        return "School"
    return "Residential"


def remaining(request):
    return request["litres"] - request["delivered"]


def add_request(system, area, litres, priority):
    area = clean_area(area)
    if area == "":
        raise ValueError("Area name cannot be empty.")
    if not area[0].isalpha():
        raise ValueError("Area name should start with a letter.")
    if litres < 100 or litres > 100000:
        raise ValueError("Litres must be between 100 and 100000.")
    if priority not in [1, 2, 3]:
        raise ValueError("Priority must be 1, 2 or 3.")

    request = {
        "id": system["next_request_id"],
        "area": area,
        "litres": litres,
        "delivered": 0,
        "priority": priority,
        "day_raised": system["day"],
        "day_served": None,
    }
    system["requests"].append(request)
    system["next_request_id"] += 1
    return request


def add_tanker(system, vehicle_number, capacity):
    vehicle_number = vehicle_number.strip().upper()
    if vehicle_number == "":
        raise ValueError("Vehicle number cannot be empty.")
    for tanker in system["tankers"]:
        if tanker["vehicle_number"] == vehicle_number:
            raise ValueError("This tanker is already added.")
    if capacity < 1000 or capacity > 50000:
        raise ValueError("Capacity must be between 1000 and 50000.")

    tanker = {
        "id": system["next_tanker_id"],
        "vehicle_number": vehicle_number,
        "capacity": capacity,
        "trips_used": 0,
    }
    system["tankers"].append(tanker)
    system["next_tanker_id"] += 1
    return tanker


def get_pending_sorted(system):
    pending = []
    for request in system["requests"]:
        if remaining(request) > 0:
            pending.append(request)

    for i in range(len(pending) - 1):
        for j in range(len(pending) - 1 - i):
            first = pending[j]
            second = pending[j + 1]
            should_swap = False
            if first["priority"] > second["priority"]:
                should_swap = True
            elif first["priority"] == second["priority"]:
                if first["day_raised"] > second["day_raised"]:
                    should_swap = True
                elif first["day_raised"] == second["day_raised"]:
                    if first["id"] > second["id"]:
                        should_swap = True
            if should_swap:
                pending[j], pending[j + 1] = pending[j + 1], pending[j]
    return pending


def find_tanker(system, needed):
    best = None
    biggest = None
    for tanker in system["tankers"]:
        if tanker["trips_used"] >= TRIPS_PER_DAY:
            continue
        if tanker["capacity"] >= needed:
            if best is None or tanker["capacity"] < best["capacity"]:
                best = tanker
        if biggest is None or tanker["capacity"] > biggest["capacity"]:
            biggest = tanker
    if best is not None:
        return best
    return biggest


def deliver(system, request, litres):
    request["delivered"] += litres
    if remaining(request) == 0:
        request["day_served"] = system["day"]


def schedule_trips(system):
    if len(system["tankers"]) == 0:
        return "No tankers added. Add a tanker first.", []

    pending = get_pending_sorted(system)
    if len(pending) == 0:
        return "No pending requests.", []
    if find_tanker(system, 1) is None:
        return "All tankers have finished today's trips. Please end the day.", []

    new_trips = []
    for request in pending:
        while remaining(request) > 0:
            tanker = find_tanker(system, remaining(request))
            if tanker is None:
                break

            load = min(tanker["capacity"], remaining(request))
            deliver(system, request, load)
            deliveries = [{"request_id": request["id"], "litres": load}]
            empty_space = tanker["capacity"] - load

            for other in pending:
                if empty_space == 0:
                    break
                if other is not request and other["area"] == request["area"]:
                    if remaining(other) > 0:
                        extra = min(empty_space, remaining(other))
                        deliver(system, other, extra)
                        deliveries.append({"request_id": other["id"], "litres": extra})
                        empty_space -= extra

            tanker["trips_used"] += 1
            trip = {
                "tanker_id": tanker["id"],
                "trip_number": tanker["trips_used"],
                "area": request["area"],
                "deliveries": deliveries,
            }
            system["today_trips"].append(trip)
            new_trips.append(trip)

    return str(len(new_trips)) + " trip(s) scheduled.", new_trips


def end_day(system):
    carried = []
    for request in get_pending_sorted(system):
        carried.append({
            "id": request["id"],
            "area": request["area"],
            "remaining": remaining(request),
        })
    system["day"] += 1
    system["today_trips"] = []
    for tanker in system["tankers"]:
        tanker["trips_used"] = 0
    return carried


def area_report(system):
    report = {}
    for request in system["requests"]:
        area = request["area"]
        if area not in report:
            report[area] = {
                "requested": 0,
                "supplied": 0,
                "pending": 0,
                "served_count": 0,
                "wait_total": 0,
                "average_wait": None,
            }
        row = report[area]
        row["requested"] += request["litres"]
        row["supplied"] += request["delivered"]
        if request["day_served"] is not None:
            row["served_count"] += 1
            row["wait_total"] += request["day_served"] - request["day_raised"]

    for row in report.values():
        row["pending"] = row["requested"] - row["supplied"]
        if row["served_count"] > 0:
            row["average_wait"] = row["wait_total"] / row["served_count"]
    return report


def request_status(request):
    if remaining(request) == 0:
        return "SERVED"
    if request["delivered"] > 0:
        return "PARTIAL"
    return "PENDING"


def read_number(message):
    while True:
        try:
            return int(input(message))
        except ValueError:
            print("Invalid input. Please enter a number.")


def add_request_from_menu(system):
    while True:
        area = input("Enter area name: ")
        if clean_area(area) == "":
            print("Area name cannot be empty.")
        elif not clean_area(area)[0].isalpha():
            print("Area name should start with a letter.")
        else:
            break
    while True:
        litres = read_number("Enter litres needed: ")
        if 100 <= litres <= 100000:
            break
        print("Litres must be between 100 and 100000.")
    print("1. Hospital  2. School  3. Residential")
    while True:
        priority = read_number("Enter priority: ")
        if priority in [1, 2, 3]:
            break
        print("Please enter 1, 2 or 3.")
    request = add_request(system, area, litres, priority)
    print("Request R" + str(request["id"]) + " added for " + request["area"] + ".")


def add_tanker_from_menu(system):
    vehicle = input("Enter vehicle number: ")
    capacity = read_number("Enter capacity in litres: ")
    try:
        tanker = add_tanker(system, vehicle, capacity)
        print("Tanker T" + str(tanker["id"]) + " added.")
    except ValueError as error:
        print(error)


def view_requests(system):
    if len(system["requests"]) == 0:
        print("No requests yet.")
    for request in system["requests"]:
        print("R{} | {} | {} | Needed: {} | Delivered: {} | {} | Day {}".format(
            request["id"], request["area"], priority_name(request["priority"]),
            request["litres"], request["delivered"], request_status(request),
            request["day_raised"]))


def view_tankers(system):
    if len(system["tankers"]) == 0:
        print("No tankers yet.")
    for tanker in system["tankers"]:
        print("T{} | {} | Capacity: {} | Trips left today: {}".format(
            tanker["id"], tanker["vehicle_number"], tanker["capacity"],
            TRIPS_PER_DAY - tanker["trips_used"]))


def format_trip(trip):
    parts = []
    for delivery in trip["deliveries"]:
        parts.append("R{} {}L".format(delivery["request_id"], delivery["litres"]))
    return "T{} (trip {}) -> {} : {}".format(
        trip["tanker_id"], trip["trip_number"], trip["area"], ", ".join(parts))


def view_today_trips(system):
    if len(system["today_trips"]) == 0:
        print("No trips scheduled today.")
        return
    print("Trips for Day " + str(system["day"]) + ":")
    for number, trip in enumerate(system["today_trips"], start=1):
        print("  " + str(number) + ". " + format_trip(trip))


def print_area_report(system):
    report = area_report(system)
    if len(report) == 0:
        print("No requests yet.")
        return
    print("{:<18}{:>11}{:>11}{:>10}{:>12}".format(
        "Area", "Requested", "Supplied", "Pending", "Avg Wait"))
    total_requested = 0
    total_supplied = 0
    for area, row in report.items():
        average = "-"
        if row["average_wait"] is not None:
            average = "{:.1f} days".format(row["average_wait"])
        print("{:<18}{:>11}{:>11}{:>10}{:>12}".format(
            area, row["requested"], row["supplied"], row["pending"], average))
        total_requested += row["requested"]
        total_supplied += row["supplied"]
    print("{:<18}{:>11}{:>11}{:>10}".format(
        "TOTAL", total_requested, total_supplied, total_requested - total_supplied))


def show_menu(system):
    print("\n---------- DAY " + str(system["day"]) + " ----------")
    print("1. Add Water Request")
    print("2. Add Tanker")
    print("3. View All Requests")
    print("4. View Tankers")
    print("5. Schedule Today's Trips")
    print("6. View Today's Trips")
    print("7. End Day")
    print("8. Area Wise Report")
    print("0. Exit")


def main():
    system = make_system()
    print("===== AquaRoute Water Supply System =====")
    while True:
        show_menu(system)
        choice = read_number("Enter your choice: ")
        if choice == 1:
            add_request_from_menu(system)
        elif choice == 2:
            add_tanker_from_menu(system)
        elif choice == 3:
            view_requests(system)
        elif choice == 4:
            view_tankers(system)
        elif choice == 5:
            message, trips = schedule_trips(system)
            print(message)
            if len(trips) > 0:
                view_today_trips(system)
                left = len(get_pending_sorted(system))
                if left > 0:
                    print(str(left) + " request(s) still pending. They will go to next day.")
        elif choice == 6:
            view_today_trips(system)
        elif choice == 7:
            carried = end_day(system)
            if len(carried) == 0:
                print("All requests served. Nothing to carry forward.")
            else:
                print("Carried to Day " + str(system["day"]) + ":")
                for item in carried:
                    print("  R{} {} - {}L remaining".format(
                        item["id"], item["area"], item["remaining"]))
            print("Day " + str(system["day"]) + " started.")
        elif choice == 8:
            print_area_report(system)
        elif choice == 0:
            print("Thank you. Exiting...")
            break
        else:
            print("Invalid choice. Enter 0 to 8.")


if __name__ == "__main__":
    main()
