```
Example requests:

Author
curl -X GET localhost:8080/api/v1/author/{id}
curl -X GET localhost:8080/api/v1/author
curl -X POST localhost:8080/api/v1/author -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","birthDate":"1990-01-01"}'
curl -X PUT localhost:8080/api/v1/author/{id} -H "Content-Type: application/json" -d '{"firstName":"Jane","lastName":"Doe","birthDate":"1992-02-02"}'
curl -X DELETE localhost:8080/api/v1/author/{id}

Book
curl -X GET localhost:8080/api/v1/book/{id}
curl -X GET localhost:8080/api/v1/book
curl -X POST localhost:8080/api/v1/book -H "Content-Type: application/json" -d '{"title":"Clean Code","subTitle":"A Handbook of Agile Software Craftsmanship","description":"Classic software engineering book","pages":464,"isbn":"9780132350884","publisherId":"00000000-0000-0000-0000-000000000000"}'
curl -X PUT localhost:8080/api/v1/book/{id} -H "Content-Type: application/json" -d '{"title":"Clean Architecture","subTitle":"A Craftsman Guide","description":"Software architecture principles","pages":432,"isbn":"9780134494166","publisherId":"00000000-0000-0000-0000-000000000000"}'
curl -X DELETE localhost:8080/api/v1/book/{id}

Publisher
curl -X GET localhost:8080/api/v1/publisher/{id}
curl -X GET localhost:8080/api/v1/publisher
curl -X POST localhost:8080/api/v1/publisher -H "Content-Type: application/json" -d '{"name":"Penguin","country":"UK"}'
curl -X PUT localhost:8080/api/v1/publisher/{id} -H "Content-Type: application/json" -d '{"name":"HarperCollins","country":"US"}'
curl -X DELETE localhost:8080/api/v1/publisher/{id}
```