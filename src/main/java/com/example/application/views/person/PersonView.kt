package com.example.application.views.person

import com.example.application.PersonRecord
import com.example.application.PersonTable
import com.example.application.PersonTable.toRecord
import com.example.application.personDbTx
import com.example.application.views.MainLayout
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.jetbrains.exposed.sql.selectAll

@Route("/personList", layout = MainLayout::class)
class PersonView : VerticalLayout() {

    private val grid: Grid<PersonRecord> = Grid<PersonRecord>()
    private val updateDataButton = Button(VaadinIcon.REFRESH.create()).apply {
        addClickListener { update() }
    }


    // Configure Grid
    init {
        isMargin = true
        grid.setup()

        add(updateDataButton, grid)
        update()
    }

    private fun update() {
        val personList: List<PersonRecord> = loadPersonList()
        grid.setItems(personList)
    }

    private fun loadPersonList(): List<PersonRecord> =
        personDbTx { PersonTable.selectAll().map { it.toRecord() } }

    companion object {
        private fun Grid<PersonRecord>.setup() {
            removeAllColumns()
            addColumn(PersonRecord::firstName).setup("Имя")
            addColumn(PersonRecord::lastName).setup("Фамилия")
            addColumn(PersonRecord::email).setup("Email")
            addColumn(PersonRecord::phone).setup("Телефон")
            addColumn(PersonRecord::birth).setup("День рождения")
            addColumn(PersonRecord::occupation).setup("Профессия")
            addColumn(PersonRecord:: middleName).setup("Отчество")
            addColumn(PersonRecord::abode).setup("Живет ли человек в России")
        }

        private fun <T> Grid.Column<T>.setup(name: String, isSortable: Boolean = true) {
            setHeader(name)
            this.isSortable = isSortable
        }
    }
}

/**
 * TODO
 * каждую задачку снизу коммитить и пушить
 *
 * добавить поле "отчество" и boolean в db
 * создать кнопку edit
 * создать кнопку удаления чтобы выскавивало окно с подтверждением
 *
 * еще одна колонка с ФИО (инициалы)
 * */