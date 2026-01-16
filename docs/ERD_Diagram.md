# Advena ERD Diagram
```mermaid
---
title: Advena ERD
---
erDiagram
    Users ||--o{ User-Followers : User_uid1
    Users ||--o{ User-Followers : Follows_uid2
    Users ||--o{ Events : Hosts
    Users {
        Varchar(50) uid PK
        Varchar(100) name
        Varchar(200) bio
        Varchar(100) email "unique"
    }

    User-Followers {
        Varchar(50) followerid PK,FK "references Users.uid"
        Varchar(50) followeeid PK,FK "references Users.uid"
    }
    Users ||--o{ Event-Attendees : Attends
    Events ||--o{ Event-Attendees : Has
    Event-Attendees {
        Varchar(50) eid PK,FK "references Events.eid"
        Varchar(50) uid PK,FK "references Users.uid"
    }
    Events {
        Varchar(50) eid PK
        Varchar(100) name
        Varchar(MAX) description
        Varchar(50) host_id FK "references Users.uid"
        Varchar(100) address
        Double latitude
        Double longitude
        Date date
        Time start_time
        Time end_time
        Float estimated_cost
        Int max_attendees 
        Varchar(MAX) tags
        Varchar(50) type
    }

    
