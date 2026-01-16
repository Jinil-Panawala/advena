
# Advena UML Class Diagrams
```mermaid
---
config:
theme: dark
title: Advena UML Diagram
---
classDiagram
    namespace Domain {

        class Model {
            +List~User~ allUsers
            +List~Event~ allEvents
            +String loggedInUserId

            +getUser(uid)
            +getUserByemail(email)
            +getUserFollowers(uid)
            +getUserFollowing(uid)
            +getEventsHostedByUser(uid)
            +getEventsAttendedByUser(uid)
            +createUser(id,name,email,bio)
            +updateUser(user)
            +deleteUser(user)
            +followUser(follwerId, followeeId)
            +unfollowUser(follwerId, followeeId)
            +getFilteredEvents(uid, filters)
            +getEvent(eid)
            +getEventAttendees(eid)
            +getEventsByTag(tag)
            +createEvent()
            +updateEvent(event)
            +deleteEvent(event)
            +attendEvent(uid,eid)
            +leaveEvent(uid,eid)
            +getAttendeesCount(eid)
            +isUserAttending(uid,eid)        
        }
        class EventType {
            <<enumeration>>
            PUBLIC
            FOLLOWER
            FRIEND
        }

        class Event {
            +String id
            +String name
            +String description
            +String address
            +Double longitude
            +Double latitude
            +String date
            +String startTime
            +String endTime
            +Double estimatedCost
            +Int maxAttendees
            +String tags
            +EventType type
        }

        class User {
            +String id
            +String name
            +String? email
            +String? bio
        }
    }
    Model ..> Event: uses
    Model ..> User: uses
    Event ..> EventType : uses
    namespace Presentation {

        class BaseViewModel {
            +String loggedInUserId
            +getEventAttendeeCountFlow(eid)
            +getIsUserAttendingFlow(eid)
            +isEventOwnedByLoggedInUser(event)
            +isEventVisibleToLoggedInUser(event)
            #handleRSVPWithCallback(event,onStart,onComplete,onError)
            #deleteEventWithCallback(event,onStart,onComplete,onError)
            +getFollowers(userId)
            +getFollowing(userId)
            #applySearchFilter(events, searchQuery)
            #applyUserSearchFilter(users, searchQuery)
        }
        
        class AuthViewModel {
            +LoginUiState loginState
            +SignUpUiState signUpState
            +onLogin(email, password)
            +onSignUp(username, fullname, email, password)
            +onLogOut()
            +clearLoginError()
            +clearSignUpError()
            -createUserProfile(userId,fullName, email)        
        }
        
        class LoginUiState {
            +Boolean isLoading
            +Boolean isSuccess
            +String? errorMessage
        }
        
        class SignUpUiState {
            +Boolean isLoading
            +Boolean isSuccess
            +Boolean errorMessage
        }
        
        
        class EditProfileViewModel {
            +String name
            +String bio
            +String profielInitial
            +Boolean isSaving
            -loadUserData()
            +updateName(newName)
            +updateBio(newBio)
            +saveProfile()
        }
        
        
        class EventCreationViewModel {
            +String name
            +String location
            +String description
            +String occupancyLimit
            +String expectedCost
            +String date
            +String startTime
            +String endTime
            +String tags
            +EventType type
            +Event? selectedEvent
            +Boolean showDatePicker
            +Boolean showStartTimePicker
            +Boolean showEndTimePicker
            +Boolean showIncompleteFieldsDialog
            +Boolean isSaving
            +loadEvent(event)
            +updateName(new)
            +updateLocation(new)
            +updateDescription(new)
            +updateOccupancyLimit(new)
            +updateExpectedCost(new)
            +updateTags(new)
            +updateType(new)
            +showDatePicker()
            +hideDatePicker()
            +showEndTimePicker()
            +hideEndTimePicker()
            +hideIncompleteFieldsDialog()
            +setDateFromMillis(millis)
            +setStartTime(hour,minute)
            +setEndTime(hour,minute)
            +saveEvent()
            
        }

        
        class EventsViewModel {
            +EventsUiState uiState
            -List~Event~ allEvents
            +loadEvents()
            +onSearchChange(newValue)
            +openFilter()
            +closeFilter()
            +onEditEvent(event)
            +updateFilters()
            -buildFilters()
            -applySearchFilter(events)
            +handleRSVP(event)
            +deleteEvent(event)
        }
        
        class EventsUiState {
            +List~Event~ events
            +String search
            +Event? selectedEvent
            +Boolean isFilterOpen
            +Boolean isLoading
            +String? filterStartDate
            +String? filterEndDate
            +Int? filterGroupSize
            +Int? filterCost
        }
        
        
        class FindFriendsViewModel{
            +String searchQuery
            +String currentId
            -List~User~ _followingList
            +List~User~ followingList
            -refreshFollowing()
            -baseUsers()
            +filteredUsers()
            +followUser(uid)
            +unfollowUser(uid)
        }
        

        class HomeViewModel{
            -HomeUiState _uiState
            +HomeUiState uiState
            +onLocationPermissionGranted()
            +updateUserLocation(location)
            +onSearchChange(newValue, currentMapBounds)
            +updateFilters()
            +loadEventsInBounds(bounds)
            -buildFilters(bounds)
            -calculateRadiusFromBounds(bounds)
            +setError(message)
            +handleRSVP(event)
            +deleteEvent(event)

        }
        
        class HomeUiState {
            +LatLng userLocation
            +List~Event~ visibleEvents
            +Boolean isLoading
            +String? errorMessage
            +Boolean locationPermissionGranted
            +String? filterStartDate
            +String? filterEndDate
            +Int? filterGroupSize
            +Int? filterCost
            +String search
        }
        
        
        class ProfileViewModel {
            +ProfileUiState uiState
            -loadUserData()
            +loadEvents()
            +refreshUserBioName()
            +handleRSVP(event)
            +deleteEvent(event)
            +toggleFollow()
        }
        
        class ProfileUiState {
            +User? user
            +Boolean isOwnProfile
            +Boolean isFollowing
            +Boolean isLoading
            +Boolean updatingEvents
            +List~Event~ hostedEvents
            +List~Event~ attendedEvents
        }
        
    }
    EditProfileViewModel --|> BaseViewModel : inherits from
    EventCreationViewModel --|> BaseViewModel : inherits from
    EventsViewModel --|> BaseViewModel : inherits from
    FindFriendsViewModel --|> BaseViewModel : inherits from
    HomeViewModel --|> BaseViewModel : inherits from
    ProfileViewModel --|> BaseViewModel : inherits from
    AuthViewModel ..> Model : uses
    AuthViewModel *-- LoginUiState
    AuthViewModel *-- SignUpUiState
    EditProfileViewModel ..> Model : uses
    EventCreationViewModel ..> EventType : uses
    EventCreationViewModel ..> Model: uses
    EventsViewModel ..> Model: uses
    EventsViewModel *-- EventsUiState
    FindFriendsViewModel ..> Model: uses
    HomeViewModel ..> Model: uses
    HomeViewModel *-- HomeUiState
    ProfileViewModel ..> Model: uses
    ProfileViewModel *-- ProfileUiState

note for FindFriendsViewModel "Used for FindFriendsScreen"
note for EventsViewModel "Used for EventsScreen"
note for EventCreationViewModel "Used for EventCreationScreen"
note for EditProfileViewModel "Used for EditProfileScreen"
note for AuthViewModel "Used for SignUpScreen and LoginScreen"
note for HomeViewModel "Used for HomeScreen"
note for ProfileViewModel "Used for ProfileScreen"
