# Бот для отслеживания новых объявлений на авто ру (auto.ru) ниже рыночной цены

### Запуск
```
./gradlew bootRun
```
После создания http сессии и бота на ее основе, объявления будут выводиться в лог

### Создание http сессии для бота
```
curl -X 'PUT' \
  'http://localhost:8080/proxy/create' \
  -H 'accept: application/json'
```

### Создание бота для http сессии
```
curl -X 'PUT' \
  'http://localhost:8080/bot/create' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "proxyId": 0,
  "geoIds": [
    "MOSCOW_AND_MOSCOW_REGION"
  ]
}'
```

### Выполнение ajax запроса из http сессии (для исследований)
```
curl -X 'POST' \
  'http://localhost:8080/proxy/post/0/-/ajax/desktop/getPhonesWithProofOfWork' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{"category":"cars","offerIdHash":"1120177620-4044236c","pow":{"hash":"0000018a131726858d5940d36f01e4ed","time":439,"timestamp":1692537398516,"payload":"1120177620-4044236c","client_timestamp":1692537398917}}'
```