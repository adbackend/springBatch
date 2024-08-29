CREATE DATABASE databaseName;<br>
CREATE USER 'toto'@'localhost' IDENTIFIED BY '1234';<br>
GRANT ALL PRIVILEGES ON databaseName.* TO 'toto'@'localhost';<br>
FLUSH PRIVILEGES;<br>
