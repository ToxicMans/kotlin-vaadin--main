package com.example.application

import com.example.application.PersonTable.lastName
import com.example.application.PersonTable.toRecord
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.joda.time.DateTime
import java.time.LocalDate

object PersonTable : Table("person") {
    val id = long("id").autoIncrement()

    val firstName = varchar("firstName", 16).index()
    val lastName = varchar("lastName", 20).index()
    val email = varchar("email", 50).nullable()
    val phone = varchar("phone", 20).nullable()
    val birth = date("birth").index()
    val occupation = varchar("occupation", 16).nullable()


    fun ResultRow.toRecord() =
        PersonRecord(
            firstName = this[firstName],
            lastName = this[lastName],
            email = this[email],
            phone = this[phone],
            birth = this[birth].let { birth -> LocalDate.of(birth.year, birth.monthOfYear, birth.dayOfMonth) },
            occupation = this[occupation]
        )
}

data class PersonRecord(
    val firstName: String,
    val lastName: String,
    val email: String?,
    val phone: String?,
    val birth: LocalDate,
    val occupation: String?
)


object PersonRepository {
    fun insertPerson(person: PersonRecord) {
        personDbTx {
            PersonTable.insert {
                it[firstName] = person.firstName
                it[lastName] = person.lastName
                it[email] = person.email
                it[phone] = person.phone
                it[birth] = person.birth.let { birth ->
                    DateTime(birth.year, birth.monthValue, birth.dayOfMonth, 0, 0)
                }
                it[occupation] = person.occupation
            }
        }
    }

    fun findByFirstName(firstName: String): List<PersonRecord> =
        personDbTx {
            PersonTable
                .select { PersonTable.firstName eq firstName  }
                .map { resultRow: ResultRow -> resultRow.toRecord()}
        }



}

