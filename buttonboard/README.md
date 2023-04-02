# Driver Station Buttonboard App

This is an application meant to be run on the Driver Station.
It's a java app that primarily marshalls data between the Network Tables and Arduino over USB.

- The Network Tables implementation is NT4 and the RoboRIO is the server
- Each Arduino will be its own table, and each sensor value will be a topic
- This application will publish these topics, and it's expected the RoboRIO will subscribe
- This application will also subscribe to some topics from the RoboRIO in order to display status lights

# Button Board

- Device Name: "buttonboard"

### Status LEDs

There are two status LEDs on the button board

#### USB Status

- Yellow (slow flash): Waiting to connect to the Driver Station
- Green (occasional flash): Connected to DS, idle state
- Green (faster flash): Connected to DS, actively sending data
- Red (flashing): Error state

#### NetworkTables Status

- Yellow (slow flash): Waiting for NetworkTables connection confirmation
- Green (green): Confirmed that we can see a network table topic sent from the RoboRIO

### Alliance Buttons

- Blue button
  - Illuminated when blue alliance is selected
  - Flashing when no alliance selected
  - Can be pressed to select blue alliance
- Red button
  - Illuminated when red alliance is selected
  - Flashing when no alliance selected
  - Can be pressed to select red alliance

### Grid Buttons

The Grid buttons are a 9x3 array of buttons, each with an RGB LED.
Each button represents a node on the grid.

The color illumated on each button indicates:

- none: The node is empty
- yellow (solid): The node contains a cone
- purple (solid): The node contains a cube
- yellow (flashing): The node is selected as the next scoring target for a cone
- purple (flashing): The node is selected as the next scoring target for a cube
- white (flashing): The node, if scored, will complete a link

When a cone node button is pressed, the button will change state according to its current state:

- any state other than below: The node will enter the "yellow (flashing)" state, indicating it's the next target for a cone
- yellow(flashing): The node will enter the "none" state, indicating it's no longer the next target

When a cube node button is pressed, the button will change state according to its current state:

- any state other than below: The node will enter the "purple (flashing)" state, indicating it's the next target for a cube
- purple(flashing): The node will enter the "none" state, indicating it's no longer the next target

When a hybrid node button is pressed, the button will change state according to its current state:

- any state other than below: The node will enter the "yellow (flashing)" state, indicating it's the next target for a cone
- yellow(flashing): The node will enter the "purple (flashing)" state, indicating it's the next target for a cube
- purple(flashing): The node will enter the "none" state, indicating it's no longer the next target

If any button is in a yellow (flashing) or purple (flashing) state when another node is selected as target, that button
will return to its previous inactivated state.

Note that it is possible to select an already-scored node as the next target. This is to overcome any false positives in our
grid scanning. This software doesn't try to avoid this scenario, but enables it in case we have bad grid data.

### Manual Mechanism Controls

Each mechanism of the robot can be controlled via the button board
Picture the robot traveling from left to right for orientation

#### Intake Controls

- Intake deploy cylinder button
  - Green button, when illuminated indicates cylinder is active
  - Push button to toggle activation
- Intake Rotation Knob
  - Controls intake winch, clockwise is out, counterclockwise is stowed
- Intake Rotation Indicator
  - Indicates current status of intake rotation
- Intake Speed Knob
  - Controls intake speed, clockwise is faster, counterclockwise is slower
- Intake Speed Indicator
  - Indicates current speed of intake rollers

#### Arm Controls

- Arm Rotation Knob
  - Turn knob to rotate arm, clockwise is down, counterclockwise is up
- Arm Rotation Indicator
  - Indicates current rotational position of arm rotation
- Arm Extension Knob
  - Turn knob to extend arm, clockwise is out, counterclockwise is in
- Arm Extension Indicator
  - Indicates current linear position of arm extension

#### Claw Controls

- Wrist button
  - Yellow button, push to extend/contract, illuminated when extended
- Claw button
  - Orange button, push to open/close, illuminated when closed

## Driver Monitor

- Device Name: "drivermonitor"

The Driver Indicator is a separate Arduino connected to the Driver Station.
It indicates to the driver which game piece is needed for the next target on the grid.
It is a Seeeduino XIAO RP2040 with simple NEOPixel strips attached to the intake camera monitor.
Since it's a XIAO RP2040, it will have the on-board LED for status.

### Strip LED colors:

- All LEDs will show the same color
  - Yellow: A cone is the next target selected
  - Purple: A cube is the next target selected

## Test Modes

This application also has some test modes:

- `test:ntserver` creates a test server on "localhost" network
- `test:ntclient` creates a test client pointed to "localhost" server
- `test:buttons` creates a test that looks for arduino device "buttonboard" and displays button updates
- `test:leds` creates a test that looks for arduino device "buttonboard" and sends status updates for LEDs
- `test:driverleds` creates a test that hosts arduino device "drivermonitor" and sends status updates for LEDs

How to run on installed version

- Windows: `app.bat <test mode>`
- OSX/Linux: `./app <test mode>`

How to run in dev environment

- Windows: `gradlew.bat run --args="<test arg>"`
- OSX/Linux: `./gradlew run --args="<test arg>"`

## TODO

This is a list of things still to be done:

- Enumerate serial devices
  - Connect to each one to get device name
  - Create mapping of device name to port
- Create support for Arduino instance
  - Map table name to device name
  - Map topic name/type to sensor name/type
- Implement `test:buttons`
- Create support for Arduino receiving state
  - Determine how to map NT topics to Arduino device settings
  - Add subscribe to topics and send data to Arduino
- Implement `test:leds`
- Implement `test:driverleds`
- (in future): consider naming around Arduino "Sensor" as it's a bit more broad now
