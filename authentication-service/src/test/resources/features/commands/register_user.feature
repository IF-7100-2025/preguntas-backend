Feature: Registro de nuevo usuario

  Scenario: Registro exitoso de un usuario con credenciales válidas
    Given no existe un usuario registrado con el correo "ana78@outlook.com"
    When registro un nuevo usuario con:
          | email              | username | password     |
          | ana78@outlook.com  | Ana70*   | BooksLover8  |
    Then el usuario debe existir en la base de datos
    And el username debe ser "Ana70*"
    And el email debe ser "ana78@outlook.com"
    And la contraseña debe estar encriptada
    And el usuario debe tener asignado el rol "COLAB"