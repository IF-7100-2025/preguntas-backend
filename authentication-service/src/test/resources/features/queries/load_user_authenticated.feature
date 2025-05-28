Feature: Cargar usuario por nombre de usuario

  Scenario: Cargar un usuario existente usando su nombre de usuario
    Given existe un usuario con las siguientes credenciales:
      | username | email             | contraseña | rol   |
      | Jose89   | joserv10@gmail.com | Techno2025 | COLAB |
    When se carga el usuario usando el nombre de usuario "Jose89"
    Then se retorna un usuario autenticado con el email "joserv10@gmail.com"
    And el usuario tiene el rol "COLAB"