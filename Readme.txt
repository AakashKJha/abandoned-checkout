Tech Stack:
    Spring Boot
    Java 17
    Postgresql
    Thymeleaf

Connection:
    in application.properties file
        spring.datasource.url=jdbc:postgresql://localhost/checkout
        spring.datasource.username=your username
        spring.datasource.password=your password

--------------------------------------------------------------------------------------
PostMan used to hit endpoint:
        Post  |  http://localhost:8080/checkout/?first=1&second=2&third=3

                    param:

                      (Integer)  first -> it will be parsed as min   ------------> optional , default = 30 min
                      (Integer)  second -> it will be parsed as day  ------------> optional , default = 1 day
                      (Integer)  third -> it will be parsed as day   ------------> optional , default = 3 day

                                        **to change the parsing way one can go to src/main/java/com/example/demo/Service/CheckoutService.java
                                                and change DAY_OF_MONTH -> to MINUTE

                    RequestBody:  -> required
                    either order json or abandoned json

-------------------------------------------------------------------------------------------
After a success-full run we can see the data in browser
    url: http://localhost:8080/checkout/details

    view:

    Order Confirmed
    Cart Id	Phone
    450789464	+16136120707
    450789467	+13125551212

    Abandoned Cart
    Cart Id	First Message	Second Message	Third Message
    450789467	This is first Remainder to your cart 450789467
    450789466	This is first Remainder to your cart 450789466	This is Second Remainder to your cart 450789466	This is Third Remainder to your cart 450789466

