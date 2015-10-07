Web part to view the new products with Flibusta.net

----------------
Понадобится на устанавливаемой машине:
* git               https://git-scm.com/downloads
* java (min ver 7)  https://www.java.com/download/
* Maven             http://maven.apache.org/download.cgi       После установки, проверяем наличие переменных окружения. Пример: M2_HOME=c:\develop\Java\maven     Path=%Path%;%M2_HOME%\bin
* Node.js & npm     https://nodejs.org/en/download/
* Tomcat            https://tomcat.apache.org/download-80.cgi
* Oracle client

Далее нужно установить два приложения
1. Node js приложение (flibustaNewsNjs) - по запросу (на http://localhost:8083/latest_news ) отдаёт все новинки с flibusta.net
   в виде JSON ответа
2. Java web приложения (LatestBooks). Он периодически (раз в час) забирает новинки из nodejs и сохраняет в Оракл БД.
   Просмотреть что было выкачано - http://localhost:8080/LatestBooks  
   
Установим их:
1. Разворачиваем nodeJs приложение
   1.1 https://github.com/SeregaLBN/flibustaNewsNjs
   1.2 В отдельно каталоге (например c:\proj)
       выполняем
         git clone https://github.com/SeregaLBN/flibustaNewsNjs.git
   1.3 Заходим в подкаталог flibustaNewsNjs
       и выполняем
         npm install
   1.4 Запускаем!
         npm start
   1.5 Проверяем в браузере, что с http://localhost:8083/latest_news
       отдаётся JSON ответ

2. Разворачиваем Java web приложение
   2.1 Для сборки проекта нужено будет наличие JDBC драйвера (v11.2.0) в локальном Maven репозитории.
       Сперва его нужно туда установить (сам драйвер возьмётся с каталога уже установленного клиента Оракла):
         mvn install:install-file -Dfile=%ORACLE_HOME%\jdbc\lib\ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0 -Dpackaging=jar
   2.2 https://github.com/SeregaLBN/flibustaNewsJ
   2.3 В отдельно каталоге (например c:\proj)
       выполняем
         git clone https://github.com/SeregaLBN/flibustaNewsJ.git
   2.4 Собираем проект - заходим в подкаталог flibustaNewsJ
       и выполняем
         mvn clean install
       ...ждёмс пока Maven соберёт проект...
   2.5 Для удобства дэплоя, переименовываем
         c:\proj\flibustaNewsJ\target\LatestBooks-1.0.0.BUILD-SNAPSHOT.war  
       в
         c:\proj\flibustaNewsJ\target\LatestBooks.war      
   2.6 Заходим в админку Tomcata
       по-умолчанию - это http://localhost:8080/manager/
   2.7 В разделе 'WAR file to deploy'
       выбираем файл LatestBooks.war
       и кликаем Deploy
       ...ждёмс
   2.8 Проверяем в браузере http://localhost:8080/LatestBooks
PS: Настройки подключения к БД
  %TOMCAT_INSTALL_DIR%\webapps\LatestBooks\WEB-INF\classes\META-INF\spring\database.properties
    Настройки логирования
  %TOMCAT_INSTALL_DIR%\webapps\LatestBooks\WEB-INF\classes\log4j.properties