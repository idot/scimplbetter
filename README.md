#scimplbetter

the (simple) scala implementation better is a betting system designed for small groups of
up to 100 people for betting of tournaments. Currently data is provided for the FIFA World Cup 2010.

Its a point system: For each game you can try to predict the final result
and you may get points for the correct tendency (win, loose, draw) or
even for the correct result. Awarded points are (tendency,result):
in group phase:                 1 and 3
last 16 and quarter finals:     2 and 4
semi finals:                    3 and 9
3rd place game:                 1 and 3
final:                          4 and 12
Points for the special bets are: World Champion (10 points), MVP (8
points), Top Scorer (8 points), Semi Finalists (5 points for each
correct guess).
The actual key is in files/levels.txt and determines at runtime the points/round.


## install

#for testing (in memory db, dummy users, dummy bets):
1. download by cloning git clone git://github.com/idot/scimplbetter.git
2. cd into git dir
3. mvn jetty:run
4. log in as username1/password1 (1..8) 8 == admin user

#for real (real db, your users):
1. download by cloning git clone git://github.com/idot/scimplbetter.git
2. cd into git dir
3. change src/main/resources/META-INF/persistence.xml 
   your db proberties + 
   <property name="hibernate.hbm2ddl.auto" value="create"/>
4. change files/dummyUsers.txt
   leave at least one admin in, only admins can register users
5. change files/dummyBets.txt 
   delete all content, but leave the file there.
6. mvn jetty:run (will create db and load your initial admin users)
7. change src/main/scala/bootstrap/liftweb/Boot.scala
   uncomment the call to importDummy()
8. change src/main/resources/META-INF/persistence.xml 
   remove: <property name="hibernate.hbm2ddl.auto" value="create"/>
9. mvn clean package => creates a war that you can deploy in jetty.


## hints 
Please set the correct timezone upon startup of you container.
I guess you will also have to change the time of the games in the file
files/fifa_2010.tab. The scimplebetter always compares the date as
it is in the database with local time as returned by new Date()


## TODO 
Missing functionality:

Deadline end of group phase:
1. entry for special bets results
2. calculate points for special bets

Deadline before finals:
recheck announce winners (basics are there but no tests)

Nice To have:
signed e-mails after game closes
more statistics
Atom feed of user activities
