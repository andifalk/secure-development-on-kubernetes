# Initial Spring Boot Application

This is the initial demo application that will be used for showing
all security patterns when deploying and running this in Kubernetes.

This application provides two REST APIs:

* Greetings API
  * GET localhost:8080: Shows greeting with configured default values
  * GET localhost:8080?message=test: Shows greeting with custom message
  * POST localhost:8080 {"greeting": "...", "message": "..."}: Shows greeting with custom message (using POST request)
* Actuator API
  * Exposes all available actuator endpoints of Spring Boot (including sensitive ones)
  
All APIs are secured by requiring either basic authentication or form based login.

Login credentials are: _user_ / _secret_  
  
## Run the application

Just start it by running _com.example.initial.app.InitialSpringBootApplication_.

## Sample client requests


