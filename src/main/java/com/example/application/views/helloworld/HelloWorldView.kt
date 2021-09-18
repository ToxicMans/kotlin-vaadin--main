package com.example.application.views.helloworld

import com.example.application.PersonRecord
import com.example.application.PersonRepository
import com.example.application.views.MainLayout
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route


@Route("/AllInfoSearch", layout = MainLayout::class)
class AllInfoSearch : VerticalLayout() {
    private val firstNameField: TextField = TextField("First name")

    private val findButton = Button("find").apply {
        addClickListener {
            val personList: List<PersonRecord> = PersonRepository.findByFirstName(firstNameField.value)

            val message: String = when {
                personList.isNotEmpty() -> personList.joinToString(separator = " | ") { "${it.lastName} ${it.firstName}" }
                else -> "Не найдено"
            }

            Notification.show(message)
        }
    }

    init {
        add(
            HorizontalLayout(firstNameField),
            HorizontalLayout(findButton)
        )
    }
}