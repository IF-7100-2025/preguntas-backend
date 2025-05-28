Feature: Categorización automática de preguntas

  Scenario: Categorización exitosa cuando el embedding coincide con una categoría
    Given existe una categoría "Programación Java" con vector de embedding [1.0, 2.0, 3.0]
    And el modelo de embedding devuelve [1.0, 2.0, 3.0] para la pregunta
    When categorizo la pregunta "Cuál es un patrón de aruqitectura común en Java?"
    Then la respuesta debe contener exactamente una categoría
    And el nombre de la categoría debe ser "Programación Java"