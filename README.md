# mbb-payment
To run the application
> mvnw spring-boot:run

# API Endpoints
## _Balance API_
- /api/v1/balance/{accountNo}
- /api/v1/balance/{accountNo}/convert/{currencyCode}

## _Transaction API_
- /api/v1/transaction/credit
- /api/v1/transaction/debit
- /api/v1/transaction/history/{accountNo}?page={pageNo}

Postman collection can be import in the `./postman` directory

All request/response will be log in `./logs` directory

3rd party API being used **https://free.currconv.com**
