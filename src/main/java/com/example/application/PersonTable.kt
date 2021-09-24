package com.example.application

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
    val middleName = varchar("middleName",20).nullable()
    val email = varchar("email", 50).nullable()
    val phone = varchar("phone", 20).nullable()
    val birth = date("birth").index()
    val occupation = varchar("occupation", 16).nullable()
    val abode = bool("abode").index()


    fun ResultRow.toRecord() =
        PersonRecord(
            firstName = this[firstName],
            lastName = this[lastName],
            middleName = this[middleName],
            email = this[email],
            phone = this[phone],
            abode = this[abode],
            birth = this[birth].let { birth -> LocalDate.of(birth.year, birth.monthOfYear, birth.dayOfMonth) },
            occupation = this[occupation]


        )
}

data class PersonRecord(
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val email: String?,
    val phone: String?,
    val birth: LocalDate,
    val occupation: String?,
    val abode: Boolean
)


object PersonRepository {
    fun insertPerson(person: PersonRecord) {
        personDbTx {
            PersonTable.insert {
                it[firstName] = person.firstName
                it[lastName] = person.lastName
                it[middleName] = person.middleName
                it[email] = person.email
                it[phone] = person.phone
                it[birth] = person.birth.let { birth ->
                    DateTime(birth.year, birth.monthValue, birth.dayOfMonth, 0, 0)
                }
                it[occupation] = person.occupation
                it[abode] = person.abode
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

