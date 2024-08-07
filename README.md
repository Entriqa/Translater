# Translater
Project for java courses 
>Если возникает проблема с ключом в переводчике - его можно изменить в application.properties. Так как я использую Яндекс переводчик - ключ сгенерирован сервисным аккаунтом. Ключ, выложенный в общий доступ, может быть заблокирован.


Чтобы запустить проект, сначала склонируйте его:
```
git clone https://github.com/Entriqa/Translater/
```
Далее перейдите в ветку master
```
git checkout master
```
Запустите файл /src/main/java/ru.kors.Translator/TranslatorApplication.java
Желательно это сделать из Intellij Idea
Далее перейдите на <a href="http://localhost:8080/" target="_blank">Simple Translator</a>

Прежде чем запускать приложение убедитесь, что у вас установлена <a href="https://www.postgresql.org/download/windows/" target="_blank">PostgreSQL</a>
<li>Установка PostgreSQL на linux</li>

```
sudo apt update
sudo apt install postgresql postgresql-contrib
```
<li>Запуск</li>

```
sudo systemctl start postgresql
sudo systemctl enable postgresql
```
<li>Подключение к базе данных</li>

```
sudo -u postgres psql
```
Далее вам нужно создать пользователя с user=yourusername password=yourpassword

```
CREATE USER yourusername WITH PASSWORD 'yourpassword';
```
Выдать права

```
GRANT CONNECT ON DATABASE yourdatabase TO yourusername;
```

