package com.groovycoder.spockdockerextension.wait

import com.groovycoder.spockdockerextension.Docker
import com.groovycoder.spockdockerextension.Env
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import spock.lang.Specification

import java.sql.ResultSet
import java.sql.Statement

class WaitStrategyIT extends Specification {

    @Docker(image = "postgres", ports = ["5432:5432"], env = [
            @Env(key = "POSTGRES_USER", value = "foo"),
            @Env(key = "POSTGRES_PASSWORD", value = "secret")
    ], waitStrategy = { new JdbcWaitStrategy(username: "foo", password: "secret", jdbcUrl: "jdbc:postgresql:foo", driverClassName: "org.postgresql.Driver", testQueryString: "SELECT 1") })
    def "waits until postgres accepts jdbc connections"() {

        given: "a jdbc connection"
        HikariConfig hikariConfig = new HikariConfig()
        hikariConfig.setJdbcUrl("jdbc:postgresql:foo")
        hikariConfig.setUsername("foo")
        hikariConfig.setPassword("secret")
        HikariDataSource ds = new HikariDataSource(hikariConfig)

        when: "querying the database"
        Statement statement = ds.getConnection().createStatement()
        statement.execute("SELECT 1")
        ResultSet resultSet = statement.getResultSet()
        resultSet.next()

        then: "result is returned"
        int resultSetInt = resultSet.getInt(1)
        resultSetInt == 1
    }

}
