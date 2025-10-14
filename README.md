openssl genrsa > app.key
openssl rsa -in app.key -pubout -out app.pub