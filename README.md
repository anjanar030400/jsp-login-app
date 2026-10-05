# Project 5 - JSP Login Web Application

A login/registration web app built with JSP + Servlets (Jakarta EE 10),
deployed on Apache Tomcat 10.1, backed by MySQL 8 via JDBC.

## Stack

- Java 21
- Apache Tomcat 10.1 (Jakarta `jakarta.servlet.*` namespace)
- MySQL 8.0
- MySQL Connector/J 8.4.0 (JDBC driver)
- Plain JSP + Servlets, no framework -- easy to read for an internship assignment

## Project layout

```
project5-login-app/
├── sql/
│   └── schema.sql              # creates DB, app user, and the users table
├── src/com/project5/
│   ├── util/
│   │   ├── PasswordUtil.java   # PBKDF2 salted password/answer hashing
│   │   ├── HtmlUtil.java       # escapes text before echoing into HTML
│   │   └── DBUtil.java         # loads db.properties, hands out JDBC connections
│   ├── dao/
│   │   ├── User.java           # plain data holder for a users row
│   │   └── UserDAO.java        # all SQL, always via PreparedStatement
│   ├── servlet/
│   │   ├── LoginServlet.java
│   │   ├── RegisterServlet.java
│   │   ├── ForgotPasswordServlet.java
│   │   ├── ResetPasswordServlet.java
│   │   ├── LogoutServlet.java
│   │   └── PhotoServlet.java   # streams the logged-in user's own photo
│   └── filter/
│       └── AuthFilter.java     # redirects to login.jsp if landing.jsp is hit without a session
└── webapp/                     # deployed as the Tomcat docBase
    ├── css/style.css           # one shared stylesheet for every page
    ├── images/default-avatar.svg
    ├── index.jsp                -> redirects to login.jsp
    ├── login.jsp
    ├── register.jsp
    ├── register-success.jsp     # confirmation page after registering
    ├── forgot-password.jsp      # step 1: verify user ID + security answer
    ├── reset-password.jsp       # step 2: set a new password
    ├── landing.jsp              # protected; shows date, name, photo, logout
    └── WEB-INF/
        ├── web.xml
        ├── classes/             # compiled .class files + db.properties
        ├── lib/mysql-connector-j-8.4.0.jar
        └── uploads/             # uploaded profile photos (not web-accessible directly)
```

## What each file does

- **PasswordUtil.java** -- derives a PBKDF2-HMAC-SHA256 hash (65,536 iterations) from a
  password/answer + a random 16-byte salt. Both are stored Base64-encoded; the plain
  text is never written to the database. `verify()` recomputes the hash and does a
  constant-time comparison.
- **HtmlUtil.java** -- escapes `& < > " '` before any user-supplied text (name, re-typed
  form values) is written back into HTML, preventing XSS.
- **DBUtil.java** -- reads `WEB-INF/classes/db.properties` once and opens a new JDBC
  connection per request via `DriverManager`. Explicitly loads the MySQL driver class
  so it registers correctly inside Tomcat's classloader (a known quirk when the driver
  jar lives in `WEB-INF/lib`).
- **UserDAO.java** -- every query is a `PreparedStatement` with bound parameters (no
  string concatenation), so user input can never alter the SQL.
- **LoginServlet** -- looks up the user, verifies the password hash, and on success
  stores `userId`, `firstName`, `photoPath` in the session, then redirects to
  `landing.jsp`. On failure it redirects with a fixed error *code* (e.g. `?error=invalid`)
  which `login.jsp` maps to static text -- the raw query string is never echoed, so
  the error message itself can't be used for reflected XSS.
- **RegisterServlet** (`@MultipartConfig`) -- validates all fields (User ID pattern,
  email format, password length/match, photo type/size), checks for duplicate
  user IDs/emails, saves the uploaded photo under a random UUID filename in
  `WEB-INF/uploads`, hashes the password and security answer, and inserts the row.
  On error it forwards back to `register.jsp` with the previously typed values
  (escaped) so nothing is lost.
- **ForgotPasswordServlet** -- verifies User ID + security answer. Uses the *same*
  generic error message whether the ID doesn't exist or the answer is wrong, so an
  attacker can't use it to enumerate valid accounts. On success it stores the
  verified user ID in the session (`pwResetUserId`) and forwards to `reset-password.jsp`.
- **ResetPasswordServlet** -- reads the user ID to update **only from the session**
  set by `ForgotPasswordServlet`, never from a posted form field. This means you
  cannot bypass the security question by POSTing straight to this servlet.
- **LogoutServlet** -- invalidates the session and redirects to `login.jsp?success=logout`.
- **PhotoServlet** -- streams only the *current session user's* own photo (it never
  takes a filename/user from the request), so one user can't view another's photo
  by changing a parameter.
- **AuthFilter** (`@WebFilter("/landing.jsp")`) -- runs before `landing.jsp` renders;
  if there's no session, it redirects to `login.jsp?error=session` instead.
- **style.css** -- single shared theme (card layout, color palette, form styling)
  used by every page for a consistent look.

## Security notes

- Passwords and security-question answers are salted + hashed with PBKDF2 (never
  stored in plain text).
- All SQL uses `PreparedStatement`.
- `landing.jsp` is unreachable without a valid session (enforced server-side by a filter).
- The password-reset flow can't be bypassed by posting directly to the "set new
  password" endpoint -- it only trusts a session flag set after the security
  question was answered correctly.
- User-supplied text is HTML-escaped before being echoed back into any page.
- The app connects to MySQL as a low-privilege `project5_app` user (SELECT/INSERT/
  UPDATE/DELETE on just the `project5_login` database), not `root`.

## One-time setup

1. **Install dependencies (macOS, Homebrew):**
   ```
   brew install openjdk@21 tomcat@10 mysql@8.0
   ```

2. **Start MySQL and create the database:**
   ```
   brew services start mysql@8.0
   mysql -u root < sql/schema.sql
   ```
   This creates the `project5_login` database, a `project5_app` MySQL user
   (password placeholder `CHANGE_ME` in `schema.sql` -- edit it to a real
   password before running, or `ALTER USER` afterwards), and the `users` table.

3. **Configure DB credentials:**
   ```
   cp webapp/WEB-INF/classes/db.properties.example webapp/WEB-INF/classes/db.properties
   ```
   Edit `db.properties` if you changed the password in `schema.sql`.
   This file is git-ignored so real credentials never get committed.

4. **Get the MySQL JDBC driver** (already included at
   `webapp/WEB-INF/lib/mysql-connector-j-8.4.0.jar`; if missing, download from
   Maven Central: `com.mysql:mysql-connector-j:8.4.0`).

## Build

```
cd project5-login-app
TOMCAT_LIB=/opt/homebrew/opt/tomcat@10/libexec/lib
javac -d webapp/WEB-INF/classes \
  -cp "$TOMCAT_LIB/servlet-api.jar:$TOMCAT_LIB/jsp-api.jar:webapp/WEB-INF/lib/mysql-connector-j-8.4.0.jar" \
  $(find src -name "*.java")
```

## Deploy

Symlink (or copy) the `webapp/` folder into Tomcat's `webapps/` directory as the
context `project5`:

```
ln -s "$(pwd)/webapp" /opt/homebrew/opt/tomcat@10/libexec/webapps/project5
```

## Run

Tomcat needs Java 21 on `JAVA_HOME` (not whatever newer JDK Homebrew may have
pulled in as a dependency):

```
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
export CATALINA_HOME=/opt/homebrew/opt/tomcat@10/libexec
"$CATALINA_HOME/bin/catalina.sh" run
```

Then open: **http://localhost:8080/project5/**

To stop: `Ctrl+C` (foreground) or find/kill the `org.apache.catalina.startup.Bootstrap` process.

## Testing it

1. Go to http://localhost:8080/project5/ -> redirected to the login page.
2. Click **Create an account**, fill in the form (including a photo), submit.
3. You'll land on a confirmation page, then can log in with your new User ID/password.
4. The landing page shows today's date, "Welcome, &lt;First Name&gt;", your photo,
   and a Logout button.
5. Try opening `http://localhost:8080/project5/landing.jsp` in a private/incognito
   window (no session) -- you'll be redirected straight to the login page.
6. Use **Forgot password?** with your User ID and security answer to set a new password.
