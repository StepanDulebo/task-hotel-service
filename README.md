# Property View API

RESTful API для управления отелями (тестовое задание).

## Стек технологий

- **Java 21**
- **Spring Boot 4.0.3**
- **Spring Data JPA + Hibernate**
- **Liquibase** (миграции базы данных)
- **H2 Database** (in-memory)
- **Maven**
- **Swagger / OpenAPI**

## Как запустить проект

```bash
mvn clean spring-boot:run
```

Сервер запускается на порту:

```
http://localhost:8092
```

## Swagger UI (документация API)

После запуска откройте в браузере:

http://localhost:8092/swagger-ui.html

Через Swagger можно протестировать все API-методы.

##  Основные эндпоинты

GET  `/property-view/hotels`  Получить список всех отелей (краткая информация) 
GET  `/property-view/hotels/{id}`  Получить полную информацию об отеле 
GET  `/property-view/search`  Поиск отелей по фильтрам 
POST  `/property-view/hotels`  Создать новый отель 
POST  `/property-view/hotels/{id}/amenities`  Добавить удобства к отелю |
GET  `/property-view/histogram/{param}`  Получить гистограмму по параметру (`brand`, `city`, `country`, `amenities`) |


##  Примеры запросов

### Поиск отелей

```http
GET http://localhost:8092/property-view/search?city=Minsk&brand=Hilton&amenities=Free WiFi
```

### Гистограмма

```http
GET http://localhost:8092/property-view/histogram/amenities
GET http://localhost:8092/property-view/histogram/city
```


### Добавление удобств

```http
POST http://localhost:8092/property-view/hotels/1/amenities
```

Body:

```json
[
  "Free parking",
  "Free WiFi",
  "Non-smoking rooms",
  "Concierge",
  "On-site restaurant",
  "Fitness center",
  "Pet-friendly rooms",
  "Room service",
  "Business center",
  "Meeting rooms"
]
```

---

##  Переключение базы данных

По умолчанию используется **H2 in-memory database**.

Чтобы переключиться на **PostgreSQL** или **MySQL**, необходимо изменить параметр:

```
spring.datasource.url
```

в файле:

```
application.yml
```

После запуска **Liquibase автоматически применит все миграции** к новой базе данных.

## Архитектура проекта

Проект построен по классической структуре Spring Boot:

```
controller
service
repository
entity
dto
config
migration (Liquibase)
```


