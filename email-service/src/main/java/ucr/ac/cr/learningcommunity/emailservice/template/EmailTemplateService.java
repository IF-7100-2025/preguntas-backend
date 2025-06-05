package ucr.ac.cr.learningcommunity.emailservice.template;

import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {

    public String createWelcomeTemplate(String name) {
        return String.format("""
        <html>
            <head>
                <style>
                    body {font-family: 'Arial', sans-serif; color: #333; line-height: 1.6; margin: 0; padding: 0;}
                    .email-container {max-width: 600px; margin: auto; padding: 20px; background: #f9f9f9; border-radius: 8px; border: 1px solid #ddd;}
                    .header {background-color: #006699; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0;}
                    .footer {background-color: #f1f1f1; color: #777; text-align: center; padding: 20px; margin-top: 20px; border-radius: 0 0 8px 8px; font-size: 14px;}
                    .content {font-size: 16px; margin-top: 20px; padding: 0 15px;}
                    .cta {background-color: #006699; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; margin: 25px 0; display: inline-block; font-weight: bold;}
                    .feature {background: #fff; padding: 20px; margin: 20px 0; border-radius: 8px; border-left: 4px solid #006699; display: flex; align-items: flex-start;}
                    .feature-icon {font-size: 24px; margin-right: 15px; color: #006699;}
                    .feature-content {flex: 1;}
                    h1 {margin-bottom: 0;}
                    h2 {color: #006699; font-size: 20px; margin-top: 0;}
                    @media only screen and (max-width: 600px) {
                        .email-container {width: 100%%; border-radius: 0;}
                        .header {border-radius: 0;}
                    }
                </style>
            </head>
            <body>
                <div class="email-container">
                    <div class="header">
                        <h1>¡Bienvenido a Conti Learning Community!</h1>
                    </div>
                    <div class="content">
                        <p>Hola <strong>%s</strong>,</p>
                        <p>¡Qué gusto tenerte con nosotros! En Conti Learning Community hemos creado un espacio donde el conocimiento se comparte y crece colaborativamente.</p>
                        <div class="feature">
                            <div class="feature-icon">💡</div>
                            <div class="feature-content">
                                <h2>Crea y comparte preguntas</h2>
                                <p>¿Tienes dudas sobre algún tema? Compártelas con nuestra comunidad. ¿Sabes la respuesta a alguna pregunta? ¡Ayuda a otros compartiendo tu conocimiento! Juntos aprendemos mejor.</p>
                            </div>
                        </div>
                        <div class="feature">
                            <div class="feature-icon">📝</div>
                            <div class="feature-content">
                                <h2>Diseña quizzes personalizados</h2>
                                <p>Explora nuestras más de 400 categorías para crear tests adaptados a tus intereses. Perfecto para preparar exámenes, repasar conceptos o simplemente desafiar a tus amigos.</p>
                            </div>
                        </div>
                        <p style="text-align: center; margin: 30px 0;">
                            <a href="https://conti.learningcommunity.cr" class="cta">Comenzar ahora</a>
                        </p>
                        <p>¿Necesitas ayuda para empezar? Estamos aquí para apoyarte en cada paso.</p>
                    </div>
                    <div class="footer">
                        <p>Este correo fue enviado automáticamente. Por favor, no respondas directamente a este mensaje.</p>
                        <p>¿Preguntas? <a href="mailto:soporte@learningcommunity.cr" style="color: #006699;">Contáctanos</a></p>
                        <p>© 2025 Conti Learning Community. Todos los derechos reservados.</p>
                        <p>
                            <a href="https://conti.learningcommunity.cr/privacidad" style="color: #006699; text-decoration: none; margin: 0 10px;">Política de Privacidad</a>
                            <a href="https://conti.learningcommunity.cr/terminos" style="color: #006699; text-decoration: none; margin: 0 10px;">Términos de Servicio</a>
                        </p>
                    </div>
                </div>
            </body>
        </html>
        """, name);
    }

    public String createVerificationTemplate(String name, String code) {
        return String.format("""
        <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2>¡Hola %s!</h2>
                <p>Gracias por registrarte en Conti Learning Community.</p>
                <p>Tu código de verificación es:</p>
                <p style="font-size: 24px; color: #006699;"><strong>%s</strong></p>
                <p>Ingresalo en la aplicación para activar tu cuenta.</p>
                <br>
                <p style="font-size: 12px; color: gray;">Este correo fue generado automáticamente. No respondas a este mensaje.</p>
            </body>
        </html>
    """, name, code);
    }

}
