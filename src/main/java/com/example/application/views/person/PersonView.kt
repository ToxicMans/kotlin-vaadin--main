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

//        grid.addColumn("firstName").isAutoWidth = true
//        grid.addColumn("lastName").isAutoWidth = true
//        grid.addColumn("email").isAutoWidth = true
//        grid.addColumn("phone").isAutoWidth = true
//        grid.addColumn("dateOfBirth").isAutoWidth = true
//        grid.addColumn("occupation").isAutoWidth = true
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
            addColumn(PersonRecord::firstName).setHeader("Имя")
            addColumn(PersonRecord::lastName).setHeader("Фамилия")
            addColumn(PersonRecord::email).setHeader("Email").isSortable = true
            addColumn(PersonRecord::phone).setHeader("Телефон").isSortable = true
            addColumn(PersonRecord::birth).setHeader("День рождения")
            addColumn(PersonRecord::occupation).setHeader("Профессия").isSortable = true
        }
    }
}

/**
 * TODO
 * каждую задачку снизу коммитить и пушить
 *
 * колонки с русскими именами
 * сортировки
 * фильтрация
 * еще одна колонка с ФИО (инициалы)
 * */