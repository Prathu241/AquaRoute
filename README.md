# CP-072 AquaRoute - Java

## Project

Priority-Based Water Supply Scheduling System

This is a simple class-wise Java console project. Each class has one clear responsibility, making the code easier to understand and explain.

## Files

- `Request.java` - stores one water request and calculates remaining water, priority name, and status
- `Tanker.java` - stores one tanker and calculates remaining daily trips
- `AquaRouteSystem.java` - contains input, scheduling, reports, and daily operations
- `Main.java` - displays the menu and calls system methods
- `AquaRoute_CP072_Java_Classwise_Explanation.pdf` - class-wise explanation, test cases, worked example, and viva preparation

## Requirements

- JDK 8 or newer
- No external library
- No database or GUI

## Compile

Open PowerShell or Command Prompt in this folder:

```text
javac *.java
```

## Run

```text
java Main
```

## Core Features

1. Add water requests with automatic IDs from R101.
2. Add tankers with automatic IDs from T1.
3. Reject duplicate vehicle numbers and invalid input ranges.
4. Serve Hospital, School, and Residential requests in that order.
5. Serve older requests first when priority is equal.
6. Select the smallest suitable tanker, or the largest for a partial delivery.
7. Allow two trips per tanker each day.
8. Use spare capacity for another request in the same area.
9. Carry pending requests to the next day.
10. Show requested, supplied, pending, and average waiting values by area.

## Worked Example

```text
1. T1 (trip 1) -> Indiranagar : R102 10000L
2. T2 (trip 1) -> Indiranagar : R102 2000L, R104 3000L
3. T2 (trip 2) -> Jayanagar : R103 5000L, R101 1000L
4. T1 (trip 2) -> Jayanagar : R101 7000L
```

R105 carries 9000 litres to Day 2. After it is served, BTM Layout has an average waiting time of 1.0 day.

## Limitation

Data is stored only while the program is running.
