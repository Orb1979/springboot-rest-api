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
curl -X POST localhost:8080/api/v1/book -H "Content-Type: application/json" -d '{"title":"title1","subTitle":"subTitle1","description":"description1","pages":123,"isbn":"1234560000001"' | jq
curl -X POST localhost:8080/api/v1/book -H "Content-Type: application/json" -d '{"title":"title1","subTitle":"subTitle2","description":"description2","pages":456,"isbn":"1234560000002","publisherId":"{id}","authorIds": [{id}]}' | jq
curl -X PUT localhost:8080/api/v1/book/{id} -H "Content-Type: application/json" -d '{"title":"titleX","subTitle":"subTitleX","description":"descriptionX","pages":123,"isbn":"1234560000003","publisherId":"{id}","authorIds": [{id}]}'
curl -X PATCH localhost:8080/api/v1/book/{id} -H "Content-Type: application/json" -d '{"title":"titleY","subTitle":"subTitleY","description":"descriptionY"}'
curl -X DELETE localhost:8080/api/v1/book/{id}

Publisher
curl -X GET localhost:8080/api/v1/publisher/{id}
curl -X GET localhost:8080/api/v1/publisher
curl -X POST localhost:8080/api/v1/publisher -H "Content-Type: application/json" -d '{"name":"Penguin","country":"UK"}'
curl -X PUT localhost:8080/api/v1/publisher/{id} -H "Content-Type: application/json" -d '{"name":"HarperCollins","country":"US"}'
curl -X DELETE localhost:8080/api/v1/publisher/{id}

BookV2
# Get a single book
curl -X GET localhost:8080/api/v2/book/{bookId}

# Get all books
curl -X GET localhost:8080/api/v2/book

# Create a book without relationships
curl -X POST localhost:8080/api/v2/book -H "Content-Type: application/json" -d '{"title":"title1","subTitle":"subTitle1","description":"description1","pages":123,"isbn":"1234560000001"}' | jq

# Create a book with a publisher and authors
curl -X POST localhost:8080/api/v2/book -H "Content-Type: application/json" -d '{"title":"title2","subTitle":"subTitle2","description":"description2","pages":456,"isbn":"1234560000002","publisherId":"{publisherId}","authorIds": ["{authorId}"]}' | jq

# Fully update a book (Bulk Overwrite)
curl -X PUT localhost:8080/api/v2/book/{bookId} -H "Content-Type: application/json" -d '{"title":"titleX","subTitle":"subTitleX","description":"descriptionX","pages":123,"isbn":"1234560000003","publisherId":"{publisherId}","authorIds": ["{authorId}"]}'

# Partially update field attributes (Patch)
curl -X PATCH localhost:8080/api/v2/book/{bookId} -H "Content-Type: application/json" -d '{"title":"titleY","subTitle":"subTitleY","description":"descriptionY"}'

# Delete a book record entirely
curl -X DELETE localhost:8080/api/v2/book/{bookId}

#*** New Granular Sub-Resource Endpoints ***

# Add an author to a book
curl -X POST localhost:8080/api/v2/book/{bookId}/authors/{authorId} | jq

# Remove a single author from a book
curl -X DELETE localhost:8080/api/v2/book/{bookId}/authors/{authorId} | jq

# Replace all authors for a book with a fresh list
curl -X PUT localhost:8080/api/v2/book/{bookId}/authors -H "Content-Type: application/json" -d '["{authorId1}", "{authorId2}"]' | jq

# Remove the publisher link from a book
curl -X DELETE localhost:8080/api/v2/book/{bookId}/publisher | jq



```