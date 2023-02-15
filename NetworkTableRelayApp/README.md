# Network Table Relay App

Simple java console app to pipe state of the grid game piece in the FRC 2023 game to an Arduino.
The state of the grid is held in a 27 length boolean array, and updated by a Limelight.

## Network Table Info

Functions as a client-side program fo rth network tables.

Subscribes to the BooleanArrayTopic "gridStates" on the table "buttonboard"
The "gridStates" topic is updated by a Limelight running a Python opencv script to process that state of the grid.

## Arduino Info

Parses boolean array received from network table topic "gridStates" into JSON to send to an Arduino