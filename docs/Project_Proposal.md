## User Stories – Priya Singh (Local Connector)
### Problems Identified
- Organizing events through group chats is inefficient and messy.
- It’s difficult to manage RSVPs and know who’s actually attending.
- Collecting payments is stressful and inconvenient.
- Last-minute dropouts create extra work and money issues.
- Communication is scattered across multiple apps.
- Hosts want to focus on people, not logistics.


Advena is a mobile application designed to simplify hosting for locals who want to connect with others. Here are some user stories:
1. As a host, I want to create events with clear details so that people understand exactly what I’m offering.
2. As a host, I want to set a maximum group size when creating an event so that gatherings stay manageable.
3. As a host, I want to see confirmed RSVPs in real time so that I know who’s coming.
4. As a host, I want the ability to reschedule or cancel easily so that I can adapt when plans change.
5. As a host, I want to be able to invite people from my friends list to my event.


## Gantt Chart
```mermaid
---
config:
  theme: dark
---
gantt
    title Advena Project Plan
    dateFormat YYYY-MM-DD
    todayMarker off
    section Proposal
        Create and Present Project Proposal :2025-09-12, 14d
    section Sprint 1
        Setup architecture   :2025-09-27, 2d
        Create core classes: 5d
        Add unit testing libraries  :3d
        Setup MockStorage functions    :3d
    section Sprint 2
        Create figma screen mockups :2025-10-11, 5d
        Setup profile and main google maps search page: 9d 
        Start implementing event viewing functionality: 5d
        Create unit tests for implemented features: 2d
    section Sprint 3
        Create database E/R diagram :2025-11-01, 3d
        Setup and integrate database : 3d
        Ensure existing logic works with databse: 6d
        Unit tests for implemented features: 2d
    section Sprint 4
        Finish integreating core functionality for creating, searching, and RSVPing event: 7d
        Unit tests, review and documentation: 6d
    section Milestone
        Project Complete :milestone, 2025-11-28, 0d


