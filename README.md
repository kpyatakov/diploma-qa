# **Запуск автотестов**

### шаги воспроизведения
1. Скачать проект с удаленного репозитория на свой локальный, с помощью команды git clone
2. Открыть проект на установленной заранее IntelliJ Idea
3. Подключите DataBase(используя mySql, postgresql)
4. Запускаем Docker контейнеры СУБД MySQL и PostgreSQL
5. Запустить контейнеры в терминале
```
docker-compose up
```

6. Запускаем SUT
```
java -jar artifacts/aqa-shop.jar
```

#### Приложение должно запуститься на:
```
http://localhost:8080. 
```
Если по каким-то причинам порт 8080 на вашей машине используется другим приложением, используйте:
java -jar app-order.jar -port=9090

7. Запускаем автотесты

>для MySQL
```
./gradlew clean test -Durl=jdbc:mysql://localhost:3306/app
```
>для PostgreSQL
```
./gradlew clean test -Durl=jdbc:postgreSQL://localhost:5433/app
```
8. Генерация отчетов
```
./gradlew clean test allureReport
./gradlew allureServe
```

9. Завершение работы AllureServe
```
Ctrl+C => Y
```
10. Остановить и удалить все контейнеры
```
docker-compose down
```
