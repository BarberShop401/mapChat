# Mapchat
![Mapchat Logo](/assets/mapchatlogo.png)

An Android-based app for people to talk and interact with each other using Google Maps. 

![Mapchat screenshot](/assets/mapchat-screenshot.png)

## How to use:
  - On Android:
    - Install the ![APK](/mapchat.apk) and run it on your device. 
    
  - On an emulator:
    - Install Android Studio on your machine,
    - Clone this repo onto your machine via 'git clone https://github.com/BarberShop401/mapChat.git' in your terminal,
    - In Android Studio, click File > Import Project > find and select the build.gradle file in the root of the cloned repo folder,
    - Build the app,
    - Ensure you have an emulator set up in your AVD manager,
    - Click the green play button.
    - Or type './gradlew run' in your terminal at the root level of the repository. 

## Team Members
- Vik Akam
- Shingo Nakajima
- Lucas Wilber

## Project Management: Trello
https://trello.com/b/0Q5UCPBG/401-final-project-management

## User Stories

* US1: As a user, I want the ability to add comments so other people can see/reply to them.
  - FT1: Store comments in Firebase using Firebase console
  - FT2: Initiate Firebase SDK
  - FT3: Weigh the pros and cons of Firebase vs. Firestore, implement one, and structure JSON in an optimal way
  - FT4: Implement functionality to create/read from the database in the app, for comments and replies
  - Acceptance test: Ensure the comments are being saved in Firebase and are rendered when queried

* US2: As a user, I want to see my own comments and other users' comments appear on the map so that I can interact with comments. 
  - FT1: Display user-created comments as soon as they're created
  - FT2: Display all comments in an area around the user on the map
  - FT3: Add a timestamp to comments
  - Acceptance test: Ensure all comments around the user are visible
  - Acceptance test: Ensure new comments are visible after being created

* US3: As a user, I want to the map to show me only comments that are relevant to me, so that functionality is focused on me. *
  - FT1: Center the map on the user's location, zoomed in to show their general proximity
  - FT2: Display all comments in the user's general location
  - Acceptance test: Ensure that the map zooms in on the user when opened and displays comments around them
  
* US4: As a user, I want to be able to reply to comments so that I can interact with other users or myself on the app. *
  - FT1: Add a replies table to the database
  - FT2: Add a form for users to write and submit replies to comments
  - FT3: Attach replies to comments when they are rendered
  - Acceptance test: Ensure that replies can be created and displayed within comments
  - Acceptance test: Ensure that replies are correctly associated with its comment
  
* US5: As a user, I want a clean looking app so that the app is approachable and appealing. *
  - FT1: Implement UI-friendly colors
  - FT2: Implement fun custom fonts
  - FT3: Create a unique logo


## Wireframes:
![Map View](./assets/mapchat-wireframes.jpg)

## Domain Model:
![Domain Model](./assets/mapchat-domain-model.jpg)

## DBER: 
![DBER](./assets/mapchat-dber.jpg)
