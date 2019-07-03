# Initial Spring Boot Application

This is the initial demo application that will be used for showing
all security patterns when deploying and running this in Kubernetes.

This application provides two REST APIs:

* Greetings API
  * GET [http://localhost:8080](http://localhost:8080): Shows greeting with configured default values
  * GET [http://localhost:8080?message=test](http://localhost:8080?message=test): Shows greeting with custom message
  * GET [http://localhost:8080/admin](http://localhost:8080/admin): Shows the administrative section (only accessible by admin user)
* Actuator API
  * Exposes all available [actuator endpoints](http://localhost:8080/actuator) of Spring Boot (including sensitive ones)
  
All APIs are secured by requiring either basic authentication or form based login.

Login credentials are: 

* Standard user: _user_ / _secret_
* Admin user: _admin_ / _secret_  
  
## Run the application

Just start it by running _com.example.initial.app.InitialSpringBootApplication_.

## Sample command client requests

### Httpie

This should give a 401:

```bash
http localhost:8080
```

This should return the default greeting:

```bash
http --auth user:secret localhost:8080
```

This should return a custom greeting:

```bash
http --auth user:secret localhost:8080 "message==Test"
```

### Curl

This should return the default greeting:

```bash
curl --user user:secret http://localhost:8080
```

This should return a custom greeting:

```bash
curl --user user:secret http://localhost:8080\?message\=Test
```

## Sample browser requests

By using the browser we can try to put in 
some cross-site scripting (XSS) snippets into the message parameter.

Try to display a popup via javascript:

```http request
http://localhost:8080/?message=<script>alert('XSS')</script>
```

Try to read the JSESSION cookie:

```http request
http://localhost:8080/?message=<script>alert(document.cookie)</script> 
```

Try to redirect to Google search via XSS:

```http request
http://localhost:8080/?message=<script>document.location="http://www.google.com/"</script>
``` 

With the default code XSS is not working as there are multiple 
defense mechanisms in place:

* Input validation only permits maximum length of _30_ for url message parameter
* Reflected greeting is output escaped (Html AND javascript)
* Content-Type is set to _application/json_ 
* Browser XSS filter is enabled via response header
* Session cookie is not reachable via javascript (_http_only_)

If you __disable__ all these precautions then you should see XSS working.

### Please do not use this in productive code !!! 