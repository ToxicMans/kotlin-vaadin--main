package com.example.application.views.list

import com.example.application.data.entity.SamplePerson
import com.example.application.data.service.SamplePersonService
import com.example.application.views.MainLayout
import com.vaadin.flow.component.*
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.dependency.Uses
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.splitlayout.SplitLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.data.binder.ValidationException
import com.vaadin.flow.data.renderer.TemplateRenderer
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import org.springframework.beans.factory.annotation.Autowired
import org.vaadin.artur.helpers.CrudServiceDataProvider


@PageTitle("List")
@Route(value = "list/:samplePersonID?/:action?(edit)", layout = MainLayout::class)
@Uses(Icon::class)
abstract class ListView : VerticalLayout() {


    private val SAMPLEPERSON_ID = "samplePersonID"
    private val SAMPLEPERSON_EDIT_ROUTE_TEMPLATE = "list/%d/edit"

    private val grid = Grid(
        SamplePerson::class.java, false
    )

    private var firstName: TextField? = null
    private var lastName: TextField? = null
    private var email: TextField? = null
    private var phone: TextField? = null
    private var dateOfBirth: DatePicker? = null
    private var occupation: TextField? = null
    private var important: Checkbox? = null

    private val cancel = Button("Cancel")
    private val save = Button("Save")

    private var binder: BeanValidationBinder<SamplePerson?>? = null

    private var samplePerson: SamplePerson? = null

    private var samplePersonService: SamplePersonService? = null

    open fun ListView(@Autowired samplePersonService: SamplePersonService) {
        addClassNames("list-view", "flex", "flex-col", "h-full")
        this.samplePersonService = samplePersonService
        // Create UI
        val splitLayout = SplitLayout()
        splitLayout.setSizeFull()
        createGridLayout(splitLayout)
        createEditorLayout(splitLayout)
        add(splitLayout)

        // Configure Grid
        grid.addColumn("firstName").isAutoWidth = true
        grid.addColumn("lastName").isAutoWidth = true
        grid.addColumn("email").isAutoWidth = true
        grid.addColumn("phone").isAutoWidth = true
        grid.addColumn("dateOfBirth").isAutoWidth = true
        grid.addColumn("occupation").isAutoWidth = true
        val importantRenderer = TemplateRenderer.of<SamplePerson?>(
            "<iron-icon hidden='[[!item.important]]' icon='vaadin:check' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: var(--lumo-primary-text-color);'></iron-icon><iron-icon hidden='[[item.important]]' icon='vaadin:minus' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: var(--lumo-disabled-text-color);'></iron-icon>"
        )
            .withProperty("important") { obj: SamplePerson? -> obj!!.isImportant }
        grid.addColumn(importantRenderer).setHeader("Important").isAutoWidth = true
        grid.setDataProvider(CrudServiceDataProvider<SamplePerson?, Any>(samplePersonService))
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER)
        grid.setHeightFull()

        // when a row is selected or deselected, populate form
        grid.asSingleSelect()
            .addValueChangeListener { event: ComponentValueChangeEvent<Grid<SamplePerson?>?, SamplePerson?> ->
                if (event.value != null) {
                    UI.getCurrent().navigate(
                        String.format(
                            SAMPLEPERSON_EDIT_ROUTE_TEMPLATE, event.value!!
                                .id
                        )
                    )
                } else {
                    clearForm()
                    UI.getCurrent().navigate(ListView::class.java)
                }
            }

        // Configure Form
        binder = BeanValidationBinder(SamplePerson::class.java)

        // Bind fields. This where you'd define e.g. validation rules
        binder!!.bindInstanceFields(this)
        cancel.addClickListener { e: ClickEvent<Button?>? ->
            clearForm()
            refreshGrid()
        }
        save.addClickListener { e: ClickEvent<Button?>? ->
            try {
                if (samplePerson == null) {
                    samplePerson = SamplePerson()
                }
                binder!!.writeBean(samplePerson)
                samplePersonService.update(samplePerson)
                clearForm()
                refreshGrid()
                Notification.show("SamplePerson details stored.")
                UI.getCurrent().navigate(ListView::class.java)
            } catch (validationException: ValidationException) {
                Notification.show("An exception happened while trying to store the samplePerson details.")
            }
        }
    }

    open fun beforeEnter(event: BeforeEnterEvent) {
        val samplePersonId = event.routeParameters.getInteger(SAMPLEPERSON_ID)
        if (samplePersonId.isPresent) {
            val samplePersonFromBackend = samplePersonService!![samplePersonId.get()]
            if (samplePersonFromBackend.isPresent) {
                populateForm(samplePersonFromBackend.get())
            } else {
                Notification.show(
                    String.format("The requested samplePerson was not found, ID = %d", samplePersonId.get()), 3000,
                    Notification.Position.BOTTOM_START
                )
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid()
                event.forwardTo(ListView::class.java)
            }
        }
    }

    private fun createEditorLayout(splitLayout: SplitLayout) {
        val editorLayoutDiv = Div()
        editorLayoutDiv.className = "flex flex-col"
        editorLayoutDiv.width = "400px"
        val editorDiv = Div()
        editorDiv.className = "p-l flex-grow"
        editorLayoutDiv.add(editorDiv)
        val formLayout = FormLayout()
        firstName = TextField("First Name")
        lastName = TextField("Last Name")
        email = TextField("Email")
        phone = TextField("Phone")
        dateOfBirth = DatePicker("Date Of Birth")
        occupation = TextField("Occupation")
        important = Checkbox("Important")
        important!!.style["padding-top"] = "var(--lumo-space-m)"
        val fields = arrayOf<Component>(
            firstName!!, lastName!!, email!!, phone!!, dateOfBirth!!, occupation!!, important!!
        )
        for (field in fields) {
            (field as HasStyle).addClassName("full-width")
        }
        formLayout.add(*fields)
        editorDiv.add(formLayout)
        createButtonLayout(editorLayoutDiv)
        splitLayout.addToSecondary(editorLayoutDiv)
    }

    private  fun createButtonLayout(editorLayoutDiv: Div) {
        val buttonLayout = HorizontalLayout()
        buttonLayout.className = "w-full flex-wrap bg-contrast-5 py-s px-l"
        buttonLayout.isSpacing = true
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY)
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        buttonLayout.add(save, cancel)
        editorLayoutDiv.add(buttonLayout)
    }

    private fun createGridLayout(splitLayout: SplitLayout) {
        val wrapper = Div()
        wrapper.setId("grid-wrapper")
        wrapper.setWidthFull()
        splitLayout.addToPrimary(wrapper)
        wrapper.add(grid)
    }

    private  fun refreshGrid() {
        grid.select(null)
        grid.dataProvider.refreshAll()
    }

    private  fun clearForm() {
        populateForm(null)
    }

    private fun populateForm(value: SamplePerson?) {
        samplePerson = value
        binder!!.readBean(samplePerson)
    }
}
