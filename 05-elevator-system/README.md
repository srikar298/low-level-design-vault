# 05 - Elevator Dispatch System

## 🎯 Problem Statement
Design an elevator control system for an $M$-floor building with $N$ elevators:
* Internal button panel inside the elevator (select destination floor).
* External button panel on each floor (press UP / DOWN).
* Elevator states: `IDLE`, `MOVING_UP`, `MOVING_DOWN`.
* Dispatcher schedules requests to minimize average wait time and passenger travel time.
* Handles edge cases: direction reversals, emergency stops, max capacity limits.

---

## 🏗 Class Diagram

```mermaid
classDiagram
    class ElevatorController {
        -List~Elevator~ elevators
        -DispatchStrategy strategy
        +handleExternalRequest(int floor, Direction direction) void
        +handleInternalRequest(int elevatorId, int destinationFloor) void
    }

    class Elevator {
        -int id
        -int currentFloor
        -Direction direction
        -ElevatorState state
        -TreeSet~Integer~ upRequests
        -TreeSet~Integer~ downRequests
        +move() void
        +addRequest(int floor) void
    }

    class ElevatorState {
        <<enumeration>>
        IDLE
        MOVING_UP
        MOVING_DOWN
    }

    class Direction {
        <<enumeration>>
        UP
        DOWN
        NONE
    }

    ElevatorController *-- Elevator
    Elevator *-- ElevatorState
    Elevator *-- Direction
```

---

## 🚀 Key Implementation Highlights
1. **State Pattern**: Encapsulates state transitions (`IDLE` to `MOVING_UP`, etc.) cleanly.
2. **LOOK / SCAN Elevator Algorithm**: Moves in one direction servicing all requests ahead before reversing, preventing starvation.
3. **Dispatch Strategy**: Pluggable algorithm (`ProximityDispatchStrategy`, `ZoneBasedDispatchStrategy`) to assign the optimal car.
