# user-app
Springboot Rest  Application (Embedded h2 DB,Hibernate,Unit tests)

**DTO user:**
name, login, password, roles:one or some from the list (ADMIN,MANAGER,WORKER)

**Requests:**

GET: path = "/users" - get all users;
     path = "/user/{login}" - get user by this login;
     
POST: value = "/user" - save new user; body is DTO-user;

DELETE: path = "/user/{login}" - delete user with his roles by login;

PUT: value = "/user" - edit existing user; body is DTO-user with or without roles.




