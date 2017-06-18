# Crespo’s Worms Tournament

Crespo’s Worms Tournament (CWT) is a yearly hosted tournament of the strategy game “Worms Armageddon”. It’s considered the most prestigious of its kind and has money prizes involved.

[Website](www.cwtsite.com) | [Wiki](http://worms2d.info/Crespo%27s_Worms_Tournament) | [Facebook](www.facebook.com/CresposWormsTournament) | [Twitter](www.twitter.com/cwt_wa)
 
## PostgreSQL Docker Container

Run under name `postgres` with version `9.4.12` and expose through port `5432`.

```sh
docker run --name postgres -p 5432:5432 postgres:9.4.12
```

Run `psql` instance connecting against the previously run PostgreSQL database.  

```sh
docker run -it --rm --link postgres:postgres postgres:9.4.12 psql -h postgres -U postgres
```

A JDBC connection would look like this:

```
jdbc:postgresql://127.0.0.1:5432/postgres
```

The username is `postgres` and there is no password set.
