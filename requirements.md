# Software Requirements

## Vision of MapChat
MapChat allows a user to leave comments on Google Maps. Upon entering Mapchat, the user may write comments (reviews) of a certain place
(e.g. restaurants, parks, schools etc). The user may also view other people's comments that were written.

## Scope
Our app will:
- Allow a user to write comments for places of their choice. 
- Allow a user will be able to add, update and delete their comments
- Allow a user to have a profile page where they can edit their username

Our app will NOT: 
- Allow users to store credit card information,
- Allow users to download comments that they've written,
- Allow/Force users to purchase content or functionality. 

### MVP
In order for a minimum viable product, the app should: 
- Upon opening the app, take the user to a main screen,
- Allow the user to add a comment at a location,
- Allow the user to reply to other comments,
- Allow the user to see all comments and replies made thus far.

### Stretch goals
Stretch goals for this app include: 
- Allowing a user to upload images of that location,
- Allowing a user to set up an account to have associated replies and comments,
- Allowing a user to upload audio clips of that location,
- Animated features in the layout for a more pleasant and enjoyable experience,
- Integrating elaborate JUnit/Integration/Espresso testing.

### Data Flow
MVP: 
1. User opens the app. 
2. The app will display Google maps on the Main Activity. 
3. Add Comment button will start a Comment modal where they can write the body for the comment and submit it using the submit button.
3. User's comments will be left at that latitude and longitude and be saved in Firebase.
4. User's comments will be overlaid on that place.
 
## Non-Functional Requirements
Usability:
- Users must be able to navigate between pages of the app without errors.
- Users must be able to see pop-up animations for comments and replies for an enjoyable experience.

Testability: 
- Test Firebase getting/receiving data
- Test Google Maps display with layout
- Test adding, deleting comments
- Test adding, deleting replies