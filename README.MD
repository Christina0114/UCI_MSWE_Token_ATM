# Token ATM
This is the repository for the Token ATM team.


# setup database
1. install mysql
2. use root login mysql ```mysql -u root```
3. Creates the new database ```mysql> create database token_atm;```
4. Create a user ```mysql> create user 'lin'@'%' identified by 'lin';```
5. Gives all privileges to the new user on the newly created database ```mysql> grant all on token_atm.* to 'lin'@'%';```

*Note: Don't need to create table, it will be created by code.
