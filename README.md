# API Cursos

Prueba técnica: hecha con **Java 21 + Spring Boot 3** para gestionar **Cursos** y **Alumnos**.  
Objetivo: una API REST con relación **1:N**, validaciones, **H2** en memoria, **JWT** para seguridad y estructura **MVC** clara.

## Stack y dependencias

- **Java 21**, **Spring Boot 3.3.x**
- **Spring Web** para los endpoints REST
- **Spring Data JPA + H2** para persistencia (sin instalar nada)
- **Spring Security + JWT (jjwt)** para autenticar y autorizar
- **Validation** en los DTOs (no metemos entidades crudas al mundo exterior)
- **Swagger UI** para testear
- **Lombok** para getters/setters/constructores, no se ha usado @Data porque a veces me ha generado problemas

## ¿Cómo lo planteé?


- **MVC puro y sencillo**: `controller` → `service` → `repository` → `model`  
  Los **controllers** hablan en **DTOs**, la lógica de negocio va en **services** y JPA vive en **repositories**.
- **Relación 1:N**: un **Curso** tiene varios **Alumnos**; cada **Alumno** pertenece a **un solo Curso**.
- **DTOs + mapeadores**: evitamos exponer entidades JPA directamente y controlamos el JSON.
- **Fechas a la española**: `"dd/MM/yyyy"` en los DTOs para que no haya sorpresas.
- **Sin paginación** en las respuestas de lista (para que el JSON salga limpio y sea fácil de leer en la prueba).
- **Seguridad “lo justo”**: login con usuario/contraseña → JWT → roles `ADMIN` y `USER`.  
  - `ADMIN`: puede **crear/editar/borrar**
  - `USER`: puede **consultar**
- 

## Estructura del proyecto (modo mapa)


```
es.jemwsg.cursos
├── config/                 # Swagger, seguridad, manejo de errores
│   └── seguridad/          # JWT, filtro, roles, UserDetailsService
├── controller/             # REST controllers (Cursos, Alumnos, Auth, Inicio)
├── dto/                    # DTOs (records) y mapeadores
│   └── mapper/
├── model/                  # Entidades JPA (Curso, Alumno, UsuarioAplicacion)
├── repository/             # Repositorios JPA
├── service/                # Reglas de negocio
└── resources/
    ├── static/login.html   # Login sencillo, para probar que redirige a Swagger
    └── application.properties
```

---

## Requisitos previos

- **JDK 21**
- **Maven 3.9+**
- (Opcional) VS Code con extensiones de Spring

---

## Configuración clave

`src/main/resources/application.properties` (ya incluido):

```properties
# --- Servidor ---
server.port=8080

# --- H2 Console ---
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# --- Datasource H2 (en memoria) ---
spring.datasource.url=jdbc:h2:mem:cursosdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=sa
spring.datasource.driver-class-name=org.h2.Driver

# --- JPA ---
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
spring.jpa.properties.hibernate.format_sql=true

# --- Carga de data.sql (desactivado) ---
spring.sql.init.mode=never
spring.jpa.defer-datasource-initialization=true

# --- JWT ---
app.jwt.secret=9f3c4a1d8e2b7c6f9452ab10cd34ef78a1b2c3d4e5f60718293a4b5c6d7e8f90
app.jwt.expiration-minutes=60
```
> **Nota**: El `secret` es **solo** para el ejercicio. Para producción, usaría variables de entorno o una keystore.

---


## Primeros pasos (login → Swagger)

1. Abre **http://localhost:8080/** → verás el **login** (`/login.html`).
2. Credenciales por defecto:
   - **admin / admin123** (ROLE_ADMIN)
   - **user / user123** (ROLE_USER)
3. Al loguearte:
   - Se hace `POST /api/auth/login`.
   - El servidor setea una **cookie `JWT` (HttpOnly, SameSite=Lax)** con el token.
   - Te redirige a **Swagger UI**.
4. En Swagger (**/swagger-ui/index.html**):
     Swagger usará **la cookie** automáticamente en la misma origin.


---

## Consola H2

- URL: **http://localhost:8080/h2-console**
- JDBC URL: `jdbc:h2:mem:cursosdb`
- User: `sa` · Password: `sa`

La BD es **en memoria** y se reinicia al apagar la app.

---

## Entidades y relación

- **Curso** 1 ── **N** Alumno  
  Un alumno solo puede pertenecer a **un** curso a la vez.

> Para simplificar el ejercicio y evitar LazyInitialization en respuestas, `Curso.alumnos` usa `fetch = EAGER`. En proyectos reales, es preferible **LAZY + DTO/consultas específicas**.

---

## Fechas (formato español)
Los DTO han cambiado el formato de fecha `@JsonFormat(pattern = "dd/MM/yyyy")`.  
Ejemplo: `"fechaInicio": "01/11/2025"`

---

## Endpoints y roles

### Auth
- `POST /api/auth/login` → devuelve `{ "token": "..." }` (público)

### Cursos
- `GET /api/cursos` → **ADMIN, USER**
- `GET /api/cursos/{id}` → **ADMIN, USER**
- `POST /api/cursos` → **ADMIN**
- `PUT /api/cursos/{id}` → **ADMIN**
- `DELETE /api/cursos/{id}` → **ADMIN**

### Alumnos
- `GET /api/alumnos` → **ADMIN, USER**
- `GET /api/alumnos/{id}` → **ADMIN, USER**
- `POST /api/alumnos` → **ADMIN**
- `PUT /api/alumnos/{id}` → **ADMIN**
- `DELETE /api/alumnos/{id}` → **ADMIN**

> Las anotaciones `@PreAuthorize` aplican los **roles** en cada operación.

---

## Validaciones y errores

- Validaciones con `jakarta.validation` en los **DTO** (ej. `@NotBlank`, `@Email`, `@Past`, etc.).
- Errores unificados con `ErrorApi`:
```json
{
  "timestamp": "2025-10-02T11:05:32.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "campo: error...",
  "path": "/api/..."
}
```

---

## Detalles de seguridad

- **JWT HS256** con **jjwt**.
- Filtro `FiltroJwtAutenticacion` que:
  - Extrae el token de `Authorization: Bearer ...` o cookie `JWT`.
  - Valida y autentica al usuario.
- **Roles**: `ROLE_ADMIN`, `ROLE_USER`.
- Passwords con **DelegatingPasswordEncoder** (bcrypt por defecto).

---

## “Uso de Streams” (requisito)

- Conversión entidad→DTO con `stream().map(...).toList()`.
- Reglas con `countByCursoId` y colecciones básicas.

---

## Notas / Limitaciones

- **BD en memoria**: reinicia al parar la app.
- `fetch = EAGER` en `Curso.alumnos` por sencillez del ejercicio.
- Sin **refresh tokens** ni rotación de claves (no requerido).
- El **secret** es de ejemplo.

---

## Troubleshooting

- **403 en Swagger**: pulsa **Authorize** y pega `Bearer <token>`.
- **H2 no abre**: verifica URL `jdbc:h2:mem=cursosdb`, user `sa`, pass `sa`.
- **Fechas**: usar formato `dd/MM/yyyy` tal y como muestran los ejemplos.

---

## Licencia

Uso libre para fines educativos / prueba técnica.