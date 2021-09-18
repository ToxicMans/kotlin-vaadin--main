package com.example.application

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

private val personDB by lazy {
    Database.connect(
        "jdbc:postgresql://localhost:5432/postgres",
        driver = "org.postgresql.Driver", user = "postgres", password = "QAZ123wsx123"
    ).also {
        transaction(it) {
            SchemaUtils.createMissingTablesAndColumns(PersonTable)
        }
    }
}

fun <T> personDbTx(action: () -> T): T = transaction(personDB) { action() }