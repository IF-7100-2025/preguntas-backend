Feature: Autenticación de usuario y generación de JWT

  Scenario: Usuario válido se autentica y recibe un token JWT válido
    Given existe un usuario con las siguientes credenciales:
      | username | email             | contraseña | rol   |
      | Aaron95  | aaronrs@gmail.com | inge8800!  | COLAB |
    When el usuario inicia sesión con:
      | username | contraseña |
      | Aaron95  | inge8800!  |
    Then el sistema genera un token JWT válido
    And el token contiene el email "aaronrs@gmail.com" como subject