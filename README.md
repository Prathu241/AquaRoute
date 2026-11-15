# CP-072 AquaRoute

## Project Title

Priority-Based Water Supply Scheduling System

## Description

AquaRoute is a menu-driven Python program for managing water requests and tanker trips. Hospitals are served first, followed by schools and residential areas. Requests that are not fully completed remain pending for the next day.

## Requirements

- Python 3
- No external packages
- No database
- No GUI

## Run the Program

Open PowerShell or Command Prompt in the project folder and run:

```text
python aquaroute.py
```

If the `python` command does not work on Windows, use:

```text
py aquaroute.py
```

## Menu Options

1. Add Water Request
2. Add Tanker
3. View All Requests
4. View Tankers
5. Schedule Today's Trips
6. View Today's Trips
7. End Day
8. Area Wise Report
0. Exit

## Main Features

1. Water request IDs start from R101 automatically.
2. Tanker IDs start from T1 automatically.
3. Water requests accept 100 to 100000 litres.
4. Tanker capacities accept 1000 to 50000 litres.
5. Duplicate vehicle numbers are rejected.
6. Hospitals are served before schools and residential areas.
7. Older requests are served first when priorities are equal.
8. Each tanker can complete two trips per day.
9. Large requests can be divided between different trips.
10. Empty tanker space can serve another request from the same area.
11. Pending requests remain available on the next day.
12. The area report displays requested, supplied, pending, and average waiting days.

## Worked Example Result

```text
1. T1 (trip 1) -> Indiranagar : R102 10000L
2. T2 (trip 1) -> Indiranagar : R102 2000L, R104 3000L
3. T2 (trip 2) -> Jayanagar : R103 5000L, R101 1000L
4. T1 (trip 2) -> Jayanagar : R101 7000L
```

R105 remains pending after Day 1. It receives 9000 litres on Day 2. BTM Layout then has an average waiting time of 1.0 day.

## Files

- `aquaroute.py` - complete Python program
- `README.md` - project details and execution instructions

## Limitation

The program stores information only while it is running. Closing the program clears the current requests and tankers.
