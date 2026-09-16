
# Ticket Tracking System

## Summary

This is a simulator for a ticket management system in an application. The commands are executed day by day and the rules are applied depending on the current simulation date, and side effects are triggered automatically.
In my implementation, I used the following design patterns: Visitor, Command, Builder, Observer and Strategy.    


### Design Patterns:

Visitor - the Visitor design pattern is paired with the Command design pattern, the main use being to separate each command execution depending on the user type. Each user type executes the command differently and has access to different fields.

Observer - the Observer design pattern is used to send notifications about each event change in order to add the notifications to a list that can be printed.

Command - the Command pattern is used to separate each command and orchestrate the execution of each command. If the command can be undone, the boolean `undo` is set and it redirects the execution to its undo version.

Builder - the Builder design pattern is used to construct the tickets, each ticket type containing a specific builder because the tickets are complex objects and their construction can be difficult.

Strategy - the Strategy design pattern is used for applying filters, each filter representing a different "strategy" of search.

---

## Classes

### AppSystem

`AppSystem` is the central class of the entire simulation. This class holds the "global state" (aka lists for each type of user, tickets, milestones, commands etc), also maintains the current simulation date (aka `now`), executes daily updates and makes iterations for each command in the command list. 

Key methods:

-   `setCommands`: sets a command list, the current date and the starting date
    
-   `AppSystem`: basic constructor
- `addTicket`: adds ticket to ticket list
- `nextTicketId`: is set to the ticket after the current one
- `subtractId`: subtracts from `ticketId`
- `iterate`: updates, goes trough every command and executes
- `daysBetween`: calculates the number of days between 2 dates
- `testingPhaseDone`: sets boolean `testingPhase` to false , therefore ending a testing phase
- `getTicketById`: looks trough all the tickets and finds the one with that specific id
-  `getDevById`: looks trough all the devs and finds the one with that username
-  `getMilestoneByName`: looks trough all the milestones and finds the one with that name
- `hasManager`:  looks trough all the managers and finds the one with that username
- `update`: updates priorities and critical from milestones

----------

## User classes

### User (abstract)

Base class for all system users.

Fields:

-   `username`
    
-   `email`
    
-   `role`
    

Methods:

-   `accept(CommandVisitor, AppSystem)` – enables visitor dispatch
   

----------

### UserDeveloper

`UserDeveloper` represents a developer user inside the system. This class extends `User` and models all developer-specific data and behavior. Developers are  responsible for handling and resolving tickets during the simulation.
Attributes:

-   `hireDate`: the date when the developer was hired
    
-   `expertiseArea`: the main technical area of the developer  
    (`FRONTEND`, `BACKEND`, `DEVOPS`, `DESIGN`, `DB`, `FULLSTACK`)
    
-   `seniority`: experience level of the developer  
    (`JUNIOR`, `MID`, `SENIOR`)
    
-   `performanceScore`: numeric score reflecting developer performance over time
    
-   `notifications`: list of notifications received by the developer
    
-   `tickets`: list of currently assigned and active tickets
    
-   `allTickets`: list of all tickets the developer has ever worked on
    

Methods:

-   `UserDeveloper`: constructor that initializes identity, hire date, expertise area, and seniority
    
-   `addTicket`: assigns a new active ticket to the developer
    
-   `removeTicket`: removes a ticket from the active ticket list
    
-   `addTicketToAll`: stores a ticket in the developer’s full ticket history
    
-   `accept`: accepts a `CommandVisitor` and allows commands to act on the developer with the Visitor pattern
    
----------

### UserReporter

`UserReporter` represents a reporter user in the system. This type of user is responsible for reporting issues, bugs, or feature requests, but does not directly work on tickets.


Methods:

-   `UserReporter`: constructor that initializes the reporter’s identity
    
-   `accept`: accepts a `CommandVisitor`, allowing reporter commands to be executed


----------

### UserManager

`UserManager` represents a manager user within the system. This class extends `User` and manages developers workflow.

Attributes:

-   `hireDate`: the date when the manager was hired
    
-   `subordinates`: list of usernames representing developers managed by this manager
    

Methods:

-   `UserManager`: constructor that initializes identity, hire date, and subordinate list
    
-   `accept`: accepts a `CommandVisitor`, allowing management-related commands to be applied


----------

## Tickets

### Ticket

`Ticket` is the abstract base class for all ticket types in the system.

Attributes:

- `id`: unique identifier of the ticket
- `title`: short descriptive title
- `description`: detailed description of the issue or request
- `reportedBy`: username of the user who reported the ticket
- `type`: ticket type (`BUG`, `UI_FEEDBACK`, `FEATURE_REQUEST`)
- `businessPriority`: business priority (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`)
- `status`: current lifecycle status (`OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`)
- `expertiseArea`: technical area required to resolve the ticket
- `milestone`: associated milestone
- `developer`: currently assigned developer
- `comments`: list of comments added to the ticket
- `history`: list of actions performed on the ticket
- `createdAt`, `assignedAt`, `solvedAt`: important timestamps
- `observers`: list of observers

Methods:

- `assign`: assigns a developer, updates the status to `IN_PROGRESS`, sets timestamps, and records actions
- `unassign`: removes the assigned developer and reverts the ticket to `OPEN`
- `setStatus`: updates the ticket status, preventing changes once closed
- `increasePriority`: gradually increases the business priority
- `criticalPriority`: forces the priority to `CRITICAL` if the ticket is not closed
- `addComment` / `removeComment`: manages ticket comments
- `setMilestone`: associates the ticket with a milestone
- `notifyObservers`: sends notifications to all registered observers
- `getRequiredSeniorities`: returns the allowed developer seniority levels for assignment
- `canAssign`: checks if a developer can be assigned based on expertise compatibility
- `calculateCustomerImpact`: computes customer impact (abstract)
- `getTicketRisk`: computes ticket risk (abstract)
- `getEfficiency`: computes resolution efficiency (abstract)

Tickets are constructed using the Builder pattern.
----------

### TicketBug

`TicketBug` represents a bug report and extends the abstract `Ticket` class. 

Attributes:

-   `expectedBehavior`: expected system behavior
    
-   `actualBehavior`: observed incorrect behavior
    
-   `frequency`: how often the bug occurs (`RARE`, `OCCASIONAL`, `FREQUENT`, `ALWAYS`)
    
-   `severity`: severity level of the bug (`MINOR`, `MODERATE`, `SEVERE`)
    
-   `environment`: environment in which the bug appears
    
-   `errorCode`: associated error code
    

Methods:

-   `calculateCustomerImpact`: computes customer impact based on frequency, severity, and business priority
    
-   `getTicketRisk`: evaluates bug risk using frequency and severity
    
-   `getEfficiency`: calculates resolution efficiency relative to resolution time and complexity
    

Instances of `TicketBug` are created using the `BuilderBug`, which extends the generic ticket builder.

----------

### TicketFeatureRequest

`TicketFeatureRequest` represents a feature request ticket. 

Attributes:

-   `businessValue`: business value of the feature (`S`, `M`, `L`, `XL`)
    
-   `customerDemand`: level of customer demand (`LOW`, `MEDIUM`, `HIGH`, `VERY_HIGH`)
    

Methods:

-   `calculateCustomerImpact`: determines impact based on business value and customer demand
    
-   `getTicketRisk`: evaluates implementation risk
    
-   `getEfficiency`: computes implementation efficiency relative to resolution time
    

Ticket creation is handled through the `BuilderFeature`.

----------

### TicketUIFeedback

`TicketUIFeedback` represents feedback related to the user interface. 

Attributes:

-   `uiElementId`: identifier of the affected UI element
    
-   `businessValue`: business importance of the feedback (`S`, `M`, `L`, `XL`)
    
-   `usabilityScore`: numeric usability score
    
-   `screenshotUrl`: URL of a screenshot
    
-   `suggestedFix`: fix or improvement
    

Methods:

-   `calculateCustomerImpact`: computes impact using business value and usability score
    
-   `getTicketRisk`: evaluates risk based on poor usability and business importance
    
-   `getEfficiency`: calculates efficiency relative to resolution time
    

Instances are created with `BuilderUIFeedback`.

----------

### Action

Represents a historical event applied to a ticket.

Fields:

-   `milestone`
    
-   `by`
    
-   `timestamp`
    
-   `action`
    
-   `from`
    
-   `to`
    

Actions are appended in chronological order and are never removed.

----------

### Comment

Represents user feedback on a ticket.

Fields:

-   `author`
    
-   `content`
    
-   `createdAt`
    

----------

## Milestone 


### Milestone

`Milestone` acts as a coordination unit that changes ticket behavior over time and notifies observers about critical events.

The class implements the `Subject` interface and uses the Observer pattern to broadcast milestone-related notifications.

Attributes:

-   `name`: unique milestone name
    
-   `blockingFor`: list of milestone names that are blocked by this milestone
    
-   `timeCreated`: date when the milestone was created
    
-   `due`: due date of the milestone
    
-   `status`: current milestone status (`ACTIVE`, `COMPLETED`)
    
-   `isBlocked`: flag indicating whether the milestone is currently blocked
    
-   `tickets`: list of tickets associated with the milestone
    
-   `assignedDevs`: usernames of developers assigned to this milestone
    
-   `devs`: list of `UserDeveloper`s participating in the milestone
    
-   `userCreator`: username of the user who created the milestone
    
-   `completedNow`: date when the milestone was completed
    
-   `unblockedAt`: date when the milestone became unblocked
    
-   `observers`: list of observers watching the milestone updates
    
-   `checkUpdate`: internal timestamp used for priority updates
    

Methods:

-   `addPriority`: periodically increases the priority of open tickets if the milestone is not blocked and enough time has passed
    
-   `allClosed`: triggered when all tickets are closed; sends notifications, updates priorities, and handles late completion
    
-   `isAllClosed`: checks whether all tickets in the milestone are closed
    
-   `becomeCritical`: marks the milestone as critical one day before the due date and escalates ticket priorities
    
-   `getCompletionPercentage`: returns the percentage of closed tickets within the milestone
    
-   `repartition`: generates a JSON structure showing how milestone tickets are distributed across developers
    
-   `notifyObservers`: sends notifications to all subscribed observers
    

-   `checkAndKickDev`: removes a developer from a ticket if the ticket priority exceeds the developer’s seniority level and records the action in ticket history
    

----------

## Notifications

### Notification

Represents system-generated alerts:

-   Milestone becoming critical
    
-   Milestone unblocked late

-   Milestone created
    
-   All tickets closed
    

Notifications are delivered with the Observer pattern.

----------

## Commands

### Command (abstract)

Base class for all user-issued commands.

Fields:

-   `user`
    
-   `timestamp`
    
-   `type`
    

Methods:

-   `execute(AppSystem)`
    
-   `canUndo()`
    

----------


### AssignTicket

`AssignTicket` extends the abstract `Command` class and follows the Command pattern, with execution logic done through the Visitor pattern.

This command supports undo operations, allowing ticket assignments to be reverted under specific conditions.

Attributes:

-   `ticketId`: identifier of the ticket to be assigned or unassigned
    
-   `developer`: the `UserDeveloper` involved in the assignment
    
-   `undo`: flag indicating whether the command should be executed normally or as an undo operation
    

Methods:

-   `AssignTicket`: constructor that initializes the command with user, timestamp, ticket ID, developer, and undo flag
    
-   `execute`: performs the assignment or undo logic depending on the `undo` flag
    
-   `canUndo`: indicates that this command supports undo operations
    
-   `undo`: reverses the assignment by unassigning the developer from the ticket
    
-   In normal execution mode (`undo == false`), the command:
    
    -   Retrieves the ticket by ID from the system
        
    -   Creates an `AssignTicketVisitor`
        
    -   Returns an error object if validation fails, otherwise returns `null`
        
-   In undo mode (`undo == true`), the command:
    
    -   Verifies that the ticket is in the `IN_PROGRESS` state
        
    -   Unassigns the developer from the ticket
        
    -   Restores the ticket to an unassigned state
        
    -   Returns an error object if undo conditions are not met
    
`AssignTicketVisitor` is a visitor used to apply role-specific logic when assigning tickets. It implements the `CommandVisitor` interface.

----------

### ReportTicket

`ReportTicket` is a command used to create and register a new ticket in the system. It extends the `Command` class and represents the entry point for reporting bugs, feature requests, or UI feedback during testing phases.

It:

-   Converts input data (`TicketInput`) into a concrete `Ticket` instance
    
-   Automatically assigns `LOW` priority to anonymous bug reports
    
-   Adds the ticket to the system if all validations pass
    

The command does not support undo operations.

`ReportTicketVisitor` is a visitor used to apply role-specific logic when reporting tickets. It implements the `CommandVisitor` interface.

----------

### AddComment

Allows developers to comment only on tickets assigned to them.

----------

### ViewTicketHistory

Returns complete ticket histories.


-   Developers see all their historical tickets
    
-   Managers see tickets for milestones they created
    
-   Actions are printed in order
    
-   Stops printing after DE-ASSIGNED
    

----------

### ViewMilestones

Displays full milestone information:

-   Tickets
    
-   Assigned developers
    
-   Completion statistics
    
-   Repartition
    

----------

### ViewTickets

Displays full list of system tickets.

---
    
### ViewNotifications

Displays full list of notifications.


---
The visitors for the last 4 mentioned commands all do the same thing: apply role-specific logic when viewing tickets/milestone/history. They implement the `CommandVisitor` interface.

----------
### GenerateCustomerImpactReport, GeneratePerformanceReport, GenerateResolutionEfficiencyReport, GenerateTicketRiskReport
---

These classes generate several reports calculated differently and the main logic for each of them is implemented in the respective visitor class, that also creates the output node and separates the user types.

----------

### CreateMilestone


`CreateMilestone` is a command that allows a manager to create a milestone and assign multiple tickets to it. It first checks that the user has manager privileges and that the system is not in a testing phase. It marks any dependent milestones as blocked and ensures that the specified tickets are not already part of another milestone. If all checks pass, the tickets are linked to the milestone, their histories are updated, and a `CreateMilestoneVisitor` is applied to finalize the operation. This command cannot be undone.

--------

### ChangeStatus

`ChangeStatus` is a command that lets a developer update the status of a ticket in a workflow: `IN_PROGRESS` → `RESOLVED` → `CLOSED`. Each status change updates the ticket’s `solvedAt` timestamp and records the action in the ticket’s history. When a ticket is closed, the command checks if its milestone is now fully completed and, if so, updates the milestone status and notifies dependent milestones. The command uses the `ChangeStatusVisitor` to handle the status change logic through the Visitor pattern and only the assigned developer can perform the update. Managers or reporters attempting the command receive an error. Undo is supported, allowing the status change to be reverted using the `UndoChangeStatusVisitor`.


--------

### LostInvestors

This command uses its visitor to stop the testing phase if investors are lost. Command is only available for `UserManager`.

-------

### Search

This command lets users search tickets or developers based on multiple criteria. When executed, it retrieves the relevant ticket and delegates the actual filtering and result formatting to the `SearchVisitor`, which applies the Visitor pattern to handle each user type appropriately. Managers can search both tickets in the system and their subordinate developers, while developers can search only tickets assigned to them. The visitor applies filters such as ticket type, business priority, creation dates, keywords, and developer attributes like expertise area, seniority, and performance score. The results are returned in a structured JSON format, including matching keywords for tickets. This command does not support undo operations.

--------
### StartTestingPhase

This command starts the global testing phase.


--------

### TicketParser

Converts a `TicketInput` object into a concrete `Ticket` instance. Depending on the ticket type—`FEATURE_REQUEST`, `BUG`, or `UI_FEEDBACK`—it uses the corresponding builder (`TicketFeatureRequest.BuilderFeature`, `TicketBug.BuilderBug`, or `TicketUIFeedback.BuilderUIFeedback`) to create a ticket with all relevant fields, including title, description, business priority, expertise area, and type-specific attributes. The parser also sets the ticket’s creation date. 



