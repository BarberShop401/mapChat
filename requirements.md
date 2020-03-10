# Software Requirements

## Vision of Mapchat
Mapchat allows a user to leave comments on Google Maps. Upon entering Mapchat, the user may write comments (reviews) of a certain place (e.g. restaurants, parks, schools etc). The user may also view other people's comments that were written.

## Scope
Our app will:
- Allow a user to write comments for places of their choice. 
- In Mapchat, the user will be able to add, update and delete their comments
- The user also has a profile page where he/she/they can edit username

Our app will NOT: 
- Allow users to store credit card information
- Allow users to download comments that they've written
- Allow/Force users to purchase content or functionality. 

### MVP
In order for a minimum viable product, the app should: 
- Upon opening the app, take the user to a main screen. 
- Allow the user to add a comment at a location 
- Allow the user to update and delete the comment

### Stretch
Stretch goals for this app include: 
- Allowing a user to upload images of that location
- Allowiing a user to upload audio clips of that location
- Allowing a user to reply to a comment left by another user

### Data Flow
MVP: 
1. User opens the app. 
2. The app will display Google maps on the Main Activity. 
3. Add Comment button will start a Comment Activity where they can write the body for the comment and submit it using the submit button
3. User's comments will be left at that latitude and logitude and be saved in Firebase
4. User's comments will be overlayed on that place
 
## Non-Functional Requirements
Usability: 
- Users must be able to navigate the app in a user-friendly way that does not interfere with production of an output harmony. 
- Users must be able to navigate between pages of the app without errors. 

Testability: 
- Test Firebase
- Test Google Maps
- Test adding, deleting, updating comments