package com.example.application.views

import com.example.application.data.service.Service
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteAlias
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired


@Route("Weather", layout = MainLayout::class)
@RouteAlias(value = "", layout = MainLayout::class)
class WeatherView: VerticalLayout() {
    @Autowired
    private var weatherServise: Service = Service()

    private var title: Label = Label("Прогноз погоды")
    private var cityTextField: TextField = TextField().apply {
        width = "80%"
    }

    private var searchButton:Button = Button("Поиск").apply {
        addClickListener {
            var city : String = cityTextField.value
            weatherServise.setCityName(city)
            var mainObject: JSONObject = weatherServise.weatherMain()
            var temp:Int = mainObject.getInt("temp")
            Notification.show("В $city $temp C")

        }

    }
    init {
        add(
            HorizontalLayout(title),
            HorizontalLayout(cityTextField),
            HorizontalLayout(searchButton),

        )
    }
}
