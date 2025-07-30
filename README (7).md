# ☁️ CloudService Fullstack

Облачное хранилище с авторизацией, загрузкой и отображением файлов. Включает в себя backend на Java (Spring Boot), frontend на Vue.js и базу данных PostgreSQL. Приложение разворачивается через Docker.

---

## 🔧 Требования

Перед началом убедитесь, что у вас установлены:

- [Docker](https://www.docker.com/products/docker-desktop)
- [Git](https://git-scm.com/)
- Браузер (например, Google Chrome)

---

## 📥 Установка

Откройте терминал и выполните по шагам:

### 1. Клонируйте проект:

```bash
git clone https://github.com/ваш-логин/cloudservice_fullstack.git
cd cloudservice_fullstack
```

### 2. Запустите все сервисы:

```bash
docker-compose up -d --build
```

Это запустит:
- Backend (localhost:8081)
- Frontend (localhost:8080)
- PostgreSQL (localhost:5432)

Откройте браузер и перейдите на [http://localhost:8080](http://localhost:8080)

---

## 🧪 Тестовый аккаунт

Вы можете сразу войти с тестовым аккаунтом:

- Email: `test@example.com`
- Пароль: `password`

Или создать нового пользователя через форму регистрации.

---

## 🛠 Работа с базой данных

Создание БД не требуется — при первом запуске Docker сам создаст базу `cloud`, создаст таблицы и применит миграции с помощью Flyway.

Если необходимо подключиться напрямую:

- Host: `localhost`
- Port: `5432`
- User: `user`
- Password: `password`
- DB Name: `cloud`

---

## 🧼 Остановка и очистка

Остановить все сервисы:

```bash
docker-compose down
```

Полная очистка (включая БД):

```bash
docker-compose down -v
```

---

## 🔐 Безопасность

- Все API защищены JWT-токеном
- Авторизация через `Authorization: Bearer <token>`
- Фронтенд автоматически подставляет токен после входа

---

## 📁 Файлы

- Загрузка файлов доступна после входа
- Файлы хранятся в папке `backend/uploads/`
- Путь можно изменить в `application.properties` с помощью свойства:

```properties
file.upload-dir=./uploads
```

---

## 📌 Примечание

Если вы запускаете проект на Windows и видите ошибку при подключении к БД — убедитесь, что порт 5432 не занят.