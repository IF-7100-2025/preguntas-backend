_# 📚 Plataforma de Aprendizaje Colaborativo - v1.0
**Proyecto  | Ingeniería del Software UCR - 2025**  
**Grupo Conti**

---

## 🖥️ **Visión General**
Desarrollar una plataforma para que las personas puedan:  
✅ Crear y organizar preguntas por materias  
✅ Generar pruebas de estudio automáticamente  
✅ Evaluar la calidad del contenido mediante votaciones

---

## 🛠️ **Stack Tecnológico (Propuesta Inicial)**
| Componente       | Tecnología       |
|-----------------|------------------|
| Frontend        | React.js + Vite  |
| Estilos         | TailwindCSS      |
| Backend         | Spring Boot 3.4  |
| Autenticación   | JWT              |

---

## 📋 **Funcionalidades Principales**
1. **Sistema de Preguntas**
    - Creación de preguntas tipo test
    - Categorización por asignaturas
    - Material de apoyo adjuntable

2. **Generador de Pruebas**
    - Creación automática de exámenes
    - Historial de resultados  (a futuro)

3. **Sistema de Reputación**
    - Votación de preguntas/respuestas
    - Reporte de contenido

---
## 🔄 **Release 1.0.0**

##### **Funcionalidades completadas:**

1. ##### **Servicio de Autenticación**
- Registro de usuarios
- Inicio de sesión de usuarios

2. ##### **Servicio de Inteligencia Artificial**
- Implementación del servicio de IA con openAI
- Creación y gestión de categorías para preguntas

3. ##### **Servicio de Preguntas**
- Creación de preguntas asociadas a categorías específicas
- Generación de quizzes basados en la categoría seleccionada

4. ##### **Kafka**
- Implementación del Kafka para la comunicación entre los microservicios

##### Además se agregaron otras funcionalidades extra dentro del servicio de preguntas como:

- #### Visualización del perfíl de usuario
- #### Actualización del perfíl de usuario

