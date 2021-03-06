# Lagalt
Lagalt is a website used to facilitate connecting individuals in creative fields with projects that need their specific skill sets.
Requirements as per [instructions](docs/Lagalt-Case.pdf).

# Team Members
Johnny Hoang, Jesper Englund, Eric Enoksson, Emil Oja

## Running instructions and requirements to run
The client and the server needs to be run separately.

To run the client, the user must have node is installed https://nodejs.org/en/.

- Open up a terminal in the client/ folder.
- type "npm install" and wait for node to install necessary packages.
- type "npm start" to start the dev server
- If the browser does not automatically open a tab, visit http://localhost:3000

To run the server, the user must have java sdk 14+ installed https://www.oracle.com/java/technologies/javase-downloads.html, and an IDE/editor supporting java, maven, and lombok. The server also needs a firebase service account key file which should be named serviceAccount.json and be put in the root folder. A fresh can be retrieved from the firebase web console (you need access to the Lagalt firebase project) at Project settings > Service account > Generate new private key.

- Run main method in src/main/java/se/experis/com/case2020/lagalt

## API endpoints
All end points uses the base path: /api/v1/...
For requests that requires authorization, the jwt token should be sent in the Authorization header. Body data must be sent in json.

`headers: { Host: <host>, Content-Type: application/json, Authorization: <jwtToken> }`

### Common Responses
In case there is a server side error, a 500 status code will be returned for all endpoints. In case of bad request format, a 400 is returned. A 405 is returned if the method is unsupported.


### Project endpoints

#### Get summarized projects
- **method:** GET
- **path:** /api/v1/projects
- **optional headers:** Authorization
- **optional query string parameters:** 
  - search: string
  - industry: string (enum)
- **expected changes:** none
- **possible responses:**
  - 200: If a valid Authorization header is passed with, the returned payload bases on user history. If a value is passed through the search parameter, the payload consists of matching projects, sorted by timestamp. If there is no Authorization header or search parameter, the latest projects will be listed.
- **possible error cases:** none

#### Get specific project
- **method:** GET
- **path:** /api/v1/projects/:projectOwner/:projectName
- **optional headers:** Authorization
- **body:** none
- **expected changes:** none
- **possible responses:**
  - 200: Project details. If the user is a member of the project, private project data is returned
- **possible error cases:**
  - 404: Project does not exist

#### Create new project
- **method:** POST
- **path:** /api/v1/projects
- **required headers:** Authorization
- **body:**
{
  title: string
  industry: {
    key (enum): value
  },
  tags: [{
    key(enum): value
  }],
  description: string
}
- **expected changes:** new project document w collection. tags, new projectRecord document
- **possible responses:**
  - 200: A list of pending applications to the given project
  - 204: No applications pending to the given project
- **possible error cases:**
  - 401: User is not an admin or owner of the project
  - 404: Project does not exist

#### Edit project
- **method:** PUT
- **path:** /api/v1/projects
- **required headers:** Authorization
- **body:** 
{
  description: string,
  industry: {
    key (enum): value
  },
  tags: [{
    key(enum): value
  }],
  admins: string[] (userId’s),
  members: string[] (userId’s),
  status: string (enum),
  links: [{
    name: string,
    url: string
  }]
}
- **expected changes:** project document w. collections admins, tags,
- **possible responses:**
  - 200: Successful edit
- **possible error cases:**
  - 401: Not authorized to edit project (must be admin or owner)
  - 404: Project not found


### Project application endpoints

#### Get project applications
- **method:** GET
- **path:** /api/v1/projects/:projectOwner/:projectName/applications
- **required headers:** Authorization
- **body:** none
- **expected changes:** none
- **possible responses:**
  - 200: A list of pending applications to the given project
  - 204: No applications pending to the given project
- **possible error cases:**
  - 401: User is not an admin or owner of the project, or not authenticated
  - 404: Project does not exist
 
#### Apply to a project
- **method:** POST
- **path:** /api/v1/projects/:projectOwner/:projectName/applications
- **required headers:** Authorization
- **body:**
{
  motivation: string
}
- **expected changes:** document added to applications and project activeApplications. project document w. projectId and document in its users collection added to pendingApplications
- **possible responses:**
  - 200: Application created successfully
- **possible error cases:**
  - 401: User is not authenticated
  - 404: Project not found
  - 409: User already has a pending application to the given project OR is already a member

#### Reply to application
- **method:** POST
- **path:** /api/v1/projects/:projectOwner/:projectName/applications/:applicationId
- **required headers:** Authorization
- **body:**
{
  status: string (enum),
  message: string
}
- **expected changes:** project activeApplications document removed, project members document added (if confirmed), pendingApplications document removed, applications document changed 
- **possible responses:**
  - 200: Application replied to
- **possible error cases:**
  - 401: User is not authenticated
  - 404: Project not found
  - 406: Application already answered
  - 409: User already has a pending application to the given project OR is already a member


### Authentication endpoints

#### Sign in
- **method:** GET
- **path:** /api/v1/signin
- **required headers:** Authorization
- **body:** none
- **expected changes:** firebase auth token. no changes in the database
- **possible responses:**
  - 200: Signed in successfully
- **possible error cases:**
  - 401: Invalid token

#### Sign out
- **method:** GET
- **path:** /api/v1/logout
- **required headers:** Authorization
- **body:** none
- **expected changes:** firebase auth token. no changes in the database
- **possible responses:**
  - 200: Signed out successfully
- **possible error cases:**
  - 401: If not authenticated

#### Get username of logged in user
- **method:** GET
- **path:** /api/v1/loggedInUser
- **required headers:** Authorization
- **body:** none
- **expected changes:** none
- **possible responses:**
  - 200: Username or null
- **possible error cases:** none

#### Check if username is available
- **method:** GET
- **path:** /api/v1/isUsernameAvailable/:username
- **headers:** Authorization
- **body:** none
- **expected changes:** none
- **possible responses:**
  - 200: Returns a boolean whether the username is available or not
- **possible error cases:** none

#### Sign up
- **method:** POST
- **path:** /api/v1/signup
- **headers:** Authorization
- **body:**
{
  username: string
}
- **expected changes:** document added to users, document added to userRecords
- **possible responses:**
  - 201: Sign up done
- **possible error cases:**
  - 401: Invalid JWT token
  - 403: An account is already tied to that email
  - 406: Username does not meet criteria
  - 409: Username taken


### User endpoints

#### Profile data for logged in user
- **method:** GET
- **path:** /api/v1/profile
- **required headers:** Authorization
- **body:** none
- **expected changes:** none
- **possible responses:**
  - 200: Returns user data
- **possible error cases:**
  - 401: User not authenticated

#### Public user info
- **method:** GET
- **path:** /api/v1/users/:username
- **headers:** none
- **body:** none
- **expected changes:** none
- **possible responses:**
  - 200: Returns user data
- **possible error cases:**
  - 403: User has chosen to be hidden and so its data is not available
  - 404: User not found

#### Edit user profile
- **method:** PUT
- **path:** /api/v1/profile
- **required headers:** Authorization
- **body:**
{
  name: string,
  hidden: boolean,
  description: string,
  portfolio: string,
  tags: {
    key (enum): value
  }
}
- **expected changes:** changes to profile document
- **possible responses:**
  - 200: Successful profile edit
- **possible error cases:**
  - 401: User not authenticated


### Enum endpoints
All enum endpoints returns an object with key-value pairs

#### Get available industries
- **method:** GET
- **path:** /api/v1/available/industries
- **headers:** none
- **body:** none
- **expected changes:** none
- **possible responses:**
  - 200: A list of possible project industries supported by the site
- **possible error cases:** none

#### Get available project/user tags
- **method:** GET
- **path:** /api/v1/available/tags
- **headers:** none
- **body:** none
- **expected changes:** none
- **possible responses:**
  - 200: A list of possible skill tags supported by the site
- **possible error cases:** none

#### Get tags available industries for a certain industry
- **method:** GET
- **path:** /api/v1/available/tags/:industry
- **headers:** none
- **body:** none
- **expected changes:** none
- **possible responses:**
  - 200: A list of possible tags project industries supported by the site
- **possible error cases:**
  - 404: Industry does not exist

#### Get available project statuses
- **method:** GET
- **path:** /api/v1/available/projectstatuses
- **headers:** none
- **body:** none
- **expected changes:** none
- **possible responses:**
  - 200: A list of possible project statuses a project can have
- **possible error cases:** none

#### Get available project statuses
- **method:** GET
- **path:** /api/v1/available/applicationstatuses
- **headers:** none
- **body:** none
- **expected changes:** none
- **possible responses:**
  - 200: A list of possible application statuses an application can have
- **possible error cases:** none


### Message board endpoints

#### Get all project message boards
- **method:** GET
- **path:** /api/v1/projects/:projectOwner/:projectName/messageboard
- **headers:** Authorization
- **body:** none
- **expected changes:** none
- **possible responses:**
  - 200: A list of possible application statuses an application can have
  - 204: Project does not have any message boards
- **possible error cases:** none
  - 401: User is not a member of the project or not authenticated

#### Get message board posts
- **method:** GET
- **path:** /api/v1/projects/:projectOwner/:projectName/messageboard/:messageBoardId
- **headers:** Authorization
- **body:** none
- **expected changes:** none
- **possible responses:**
  - 200: A list of possible application statuses an application can have
  **possible error cases:** none
  - 401: User is not a member of the project or not authenticated
  - 404: Project not found

#### Create new message board
- **method:** POST
- **path:** /api/v1/projects/:projectOwner/:projectName/messageboard
- **headers:** Authorization
- **body:**
{
  title: string,
  text: string
}
- **expected changes:** added collection and document to project message board
- **possible responses:**
  - 200: Message board created
- **possible error cases:**
  - 401: User is not authenticated
  - 404: Project not found
  - 409: There is already a thread with the supplied name

#### Create new message board post
- **method:** POST
- **path:** /api/v1/projects/:projectOwner/:projectName/messageboard/:messageBoardId
- **headers:** Authorization
- **body:**
{
  text: string
}
- **expected changes:** added document to project message board
- **possible responses:**
  - 200: Message board created
- **possible error cases:**
  - 401: User is not authenticated
  - 404: Project not found or there is no thread with the supplied id

#### Delete message board post
- **method:** DELETE
- **path:** /api/v1/projects/:projectOwner/:projectName/messageboard/:messageBoardId/:messageId
- **headers:** Authorization
- **body:** none
- **expected changes:** message board document belonging to project tagged as deleted (boolean field)
- **possible responses:**
  - 200: Message board created
- **possible error cases:**
  - 401: User is not the owner of the message or not authenticated
  - 404: Project not found
/projects/:projectOwner/:projectName/messageboard/:messageBoardId/:messageId


### Chat endpoints

#### Get project chat DB Path
- **method:** GET
- **path:** /api/v1/projects/{projectOwner}/{projectName}/chat
- **headers:** Authorization
- **body:** none
- **expected changes:** none
- **possible responses:**
  - 200: Get successful
  - 401: User is not a project member or not authenticated
  - 404: Project not found
- **possible error cases:** none

#### Send chat message
- **method:** POST
- **path:** /api/v1/projects/{projectOwner}/{projectName}/chat
- **headers:** Authorization
- **body:**
{
  text: string
}
- **expected changes:** added document to project chat default channel
- **possible responses:**
  - 200: Msg sent successfully
  - 401: User is not a project member or not authenticated
  - 404: Project not found
- **possible error cases:** none
