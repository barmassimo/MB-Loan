MB Loan
=======

A Java / Spring Framework / Hibernate micro-lending app, available via 
* WEB gui (login authoentication)
* REST interface (http basic authentication)


Copyright (C) 2014, [Massimo Barbieri](http://www.massimobarbieri.it) 

## Business rules

* Client can apply for loan after registation.
* Loan is rejected if:
    1. the attempt to take loan is made after from 00:00 to 6:00 AM with max possible amount.
    1. reached 3 applications per day from a single IP.
* Client can extend a loan for one week, interest gets increased by a factor of 1.5.

## Environment

Spring Boot
Tomcat 
PostgreSQL or HSQLDB
Maven

## API examples

### Register as a new User

request:
```
curl -X POST -H "Content-Type:application/json" -H "Accept:application/json" http://localhost:8080/api/users -d "{ \"name\": \"massimo\", \"surname\":\"barbieri\", \"email\":\"massimo@massimobarbieri.it\", \"password\":\"secret\" }"
```

response:
```
{
    "status": "success",
    "objectId": 5,
    "errorMessage": "",
    "links": [
        {
            "rel": "loans",
            "href": "http://localhost:8080/api/loans"
        }
    ]
}
```

### Apply for a Loan

request:
```
curl -X POST -H "Content-Type:application/json" -H "Accept:application/json" http://localhost:8080/api/loans -d "{ \"amount\": 100, \"duration\":42 }" --user massimo@massimobarbieri.it:secret
```

response:
```
{"status":"success","objectId":1,"errorMessage":"","links":[{"rel":"self","href":"http://localhost:8080/api/loans/1"}]}
```

### Get info on a specific Loan

request:
```
curl -H "Content-Type:application/json" -H "Accept:application/json" http://localhost:8080/api/loans/1 --user massimo@massimobarbieri.it:secret
```

response:
```
{"loan":{"id":1,"creationDate":1400271432200,"amount":100.00,"interest":0.03,"duration":42,"ipAddress":"127.0.0.1","loanExtensions":[]},"links":[{"rel":"self","href":"http://localhost:8080/api/loans/1"},{"rel": "extend","href":"http://localhost:8080/api/loans/1/loan_extension"}]}
```

### Get info on all Loans

request:
```
curl -H "Content-Type:application/json" -H "Accept:application/json" http://localhost:8080/api/loans/ --user massimo@massimobarbieri.it:secret
```

response:
```
[{"loan":{"id":1,"creationDate":1400269742160,"amount":100.00,"interest":0.18,"duration":46,"ipAddress":"127.0.0.1","loanExtensions":[{"id":1,"creationDate":1400269858314},{"id":2,"creationDate":1400269860371},{"id":3,"creationDate":1400269861472},{"id":4,"creationDate":1400269897636}]},"links":[{"rel":"self","href":"http://localhost:8080/api/loans/1"},{"rel":"extend","href":"http://localhost:8080/api/loans/1/loan_extension"}]}]
```

### Extend a Loan

request:
```
curl -X POST -H "Content-Type:application/json" -H "Accept:application/json" http://localhost:8080/api/loans/1/loan_extension --user massimo@massimobarbieri.it:secret
```

response:
```
{"status":"success","objectId":1,"errorMessage":"","links":[{"rel":"loan","href":"http://localhost:8080/api/loans/1"}]}
```

## License

GNU GENERAL PUBLIC LICENSE V 3

