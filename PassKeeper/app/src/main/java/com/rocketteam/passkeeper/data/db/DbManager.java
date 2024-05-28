package com.rocketteam.passkeeper.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rocketteam.passkeeper.data.model.request.PasswordCredentials;
import com.rocketteam.passkeeper.data.model.request.UserCredentials;
import com.rocketteam.passkeeper.data.model.response.PasswordResponse;
import com.rocketteam.passkeeper.data.model.response.UserResponse;
import com.rocketteam.passkeeper.util.HashUtility;
import com.rocketteam.passkeeper.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Clase que gestiona las operaciones de base de datos relacionadas con usuarios y contraseñas.
 */
public class DbManager {
    // Definición de las columnas de la tabla de contraseñas
    public static final String TB_PASSWORD = "password";
    public static final String PASSWORD_USERNAME = "username";
    public static final String PASSWORD_URL = "url";
    public static final String PASSWORD_KEYWORD = "keyword";
    public static final String PASSWORD_DESCRIPTION = "description";
    public static final String PASSWORD_USER = "user_id";
    public static final String PASSWORD_NAME = "name";
    //definicion de la tabla contraseña
    public static final String CREATE_PASSWORD_TABLE = "CREATE TABLE IF NOT EXISTS password ( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username TEXT, " +
            "url TEXT, " +
            "keyword TEXT NOT NULL, " +
            "description TEXT, " +
            "name TEXT, " +
            "user_id INTEGER, " +
            "created_at TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now', 'localtime')), " +
            "updated_at TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now', 'localtime')), " +
            "FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE)";
    // Definición de las columnas de la tabla de usuarios
    public static final String TB_USER = "user";
    //public static final String ID_USER = "id";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String PREMIUM = "premium";
    public static final String SALT = "salt"; // Columna para almacenar el salt
    public static final String BIO = "biometric"; // Columna para almacenar la opcion biometrica
    //definicion de la tabla usuario
    public static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS user ( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "email TEXT UNIQUE, " +
            "password TEXT, " +
            "premium INTEGER DEFAULT 0, " +
            "salt TEXT, " +
            "biometric INTEGER DEFAULT 0," +
            "created_at TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now', 'localtime')), " +
            "updated_at TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now', 'localtime')) " +
            ")";
    private static final String PASSWORD_ID = "id";
    //Definicion de variables y constantes globales
    private final DbConnection connection;
    private final SharedPreferences sharedPreferences;
    private SQLiteDatabase db;

    /**
     * Constructor de la clase DbManager.
     *
     * @param context Contexto de la aplicación.
     */
    public DbManager(Context context) {
        this.connection = new DbConnection(context);
        this.sharedPreferences = context.getSharedPreferences("Storage", Context.MODE_PRIVATE);
    }

    /**
     * Abre la conexión a la base de datos para realizar operaciones.
     *
     * @throws SQLException Si ocurre un error al abrir la base de datos.
     */
    public void open() throws SQLException {
        db = connection.getWritableDatabase();
    }

    /**
     * Cierra la conexión a la base de datos.
     * <p>
     * Es importante llamar a este método para liberar recursos cuando ya no se necesite la conexión.
     */
    public void close() {
        connection.close();
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     *
     * @param user Objeto UserCredentials que contiene la información del usuario.
     * @param bio  Valor Entero que representa la selección de utilizar Huella del usuario
     * @return true si el registro es exitoso, false si hay un error.
     * @throws HashUtility.HashingException Si ocurre un error durante el hashing de la contraseña.
     * @throws HashUtility.SaltException    Si ocurre un error durante la generación del salt.
     */
    public boolean userRegister(UserCredentials user, int bio) throws HashUtility.HashingException, HashUtility.SaltException {
        try {
            Log.i("TAG", "LLEGA PARAMETRO DE BIO: " + bio);
            // Generar un salt aleatorio
            String salt = HashUtility.generateSalt();
            // Hashear la contraseña con el salt generado
            String hashedPassword = HashUtility.hashPassword(user.getPassword(), salt);

            ContentValues content = new ContentValues();
            content.put(EMAIL, user.getEmail());
            content.put(PASSWORD, hashedPassword); // Guardar el hash en la base de datos
            content.put(SALT, salt); // Guardar el salt en la base de datos
            content.put(BIO, bio); //guardar la seleccion biometrica

            // Evitar el conflicto de email duplicado y no realizar el registro, pero devolver -1
            long newRowId = db.insertWithOnConflict(TB_USER, null, content, SQLiteDatabase.CONFLICT_IGNORE);

            //Si se registro el usuario
            if (newRowId != -1) {
                saveStorage(-1, bio, -1);
            }

            // Si newRowId es -1, indica que hubo un conflicto y no se pudo insertar el nuevo usuario
            return newRowId != -1;
        } catch (HashUtility.SaltException e) {
            // Manejar la excepción de generación de salt
            Log.e("DbManager", "Salt generation error: " + e.getMessage());
            throw e; // Re-lanzar la excepción para que sea manejada en un nivel superior si es necesario
        } catch (HashUtility.HashingException e) {
            // Manejar la excepción de hashing de contraseña
            Log.e("DbManager", "Hashing password error: " + e.getMessage());
            throw e; // Re-lanzar la excepción para que sea manejada en un nivel superior si es necesario
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Obtiene el salt de un usuario por su ID.
     *
     * @param userId ID del usuario.
     * @return Salt del usuario o null si el usuario no existe en la base de datos.
     */
    public String getSaltById(int userId) {
        String salt = null;
        Cursor cursor = null;

        try {
            String query = "SELECT salt FROM user WHERE id = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            int saltIndex = cursor.getColumnIndex("salt");
            if (saltIndex != -1 && cursor.moveToFirst()) {
                salt = cursor.getString(saltIndex);
            } else {
                // La columna "salt" no existe en el conjunto de resultados o el cursor está vacío
                Log.e("DbManager", "No se pudo encontrar la columna 'salt'");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return salt;
    }

    /**
     * Registra una nueva contraseña en la base de datos.
     *
     * @param psw La información de la contraseña a registrar.
     * @return true si la contraseña se registró correctamente, false si ocurrió un error.
     */
    public boolean passwordRegister(PasswordCredentials psw) throws Exception {

        if (psw == null) {
            throw new IllegalArgumentException("PasswordCredentials no puede ser null");
        }
        Log.i("TAG", "Llega al passwordRegister: " + psw.getUsername());
        try {
            //Obtengo el Salt para hashear la contraseña
            String salt = getSaltById(psw.getUserId());
            if (salt != null) {

                ContentValues content = new ContentValues();
                content.put(PASSWORD_USERNAME, psw.getUsername());
                content.put(PASSWORD_URL, psw.getUrl());
                String hashedPassword = HashUtility.encrypt(psw.getKeyword(), salt);
                content.put(PASSWORD_KEYWORD, hashedPassword); // Guardar la contraseña hasheada
                content.put(PASSWORD_DESCRIPTION, psw.getDescription());
                content.put(PASSWORD_NAME, psw.getName());
                content.put(PASSWORD_USER, psw.getUserId());

                long newRowId = db.insertWithOnConflict(TB_PASSWORD, null, content, SQLiteDatabase.CONFLICT_IGNORE);
                Log.i("TAG", "Se registra PWD: " + newRowId + " de nombre: " + content.getAsString(PASSWORD_NAME));
                return newRowId != -1;
            } else {
                // El usuario con el ID especificado no existe
                Log.e("DbManager", "No existe el usuario");
                return false;
            }
        } catch (SQLException e) {
            Log.e("TAG", "Error de SQL al registrar la contraseña: " + e.getMessage());
            throw e;
        } catch (HashUtility.HashingException e) {
            Log.e("TAG", "Error al hashear la contraseña: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            Log.e("TAG", "Password registration error: " + e.getMessage());
            throw e;
        } finally {
            db.close();
        }
    }

    /**
     * Valida las credenciales del usuario.
     *
     * @param pwd   Contraseña ingresada por el usuario.
     * @param email Correo electrónico ingresado por el usuario.
     * @return true si las credenciales son válidas, false en caso contrario.
     */
    public boolean validateUser(String pwd, String email) throws HashUtility.HashingException {
        // Obtener el usuario por correo electrónico
        UserResponse user = this.getUserByEmail(email);

        if (user != null && HashUtility.checkPassword(pwd, user.getPassword(), user.getSalt())) {
            try {
                saveStorage(user.getId(), user.getBiometric(), user.getPremium());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Log.i("TAG", "userID logeado por credenciales: " + user.getEmail());
            return true; // Las credenciales son válidas
        }
        return false; // Las credenciales son inválidas
    }

    /**
     * Obtiene un usuario por su dirección de correo electrónico desde la base de datos.
     *
     * @param email Correo electrónico del usuario a buscar.
     * @return Objeto UserResponse si se encuentra el usuario, o null si no se encuentra.
     */
    private UserResponse getUserByEmail(String email) {
        UserResponse user = null;
        Log.i("TAG", "llega el email: " + email);

        try {
            // Define la consulta SQL para seleccionar el usuario por email
            String query = "SELECT * FROM user WHERE email = ?";
            Cursor cursor = db.rawQuery(query, new String[]{email});
            int idIndex = cursor.getColumnIndex("id");
            int emailIndex = cursor.getColumnIndex("email");
            int pwdIndex = cursor.getColumnIndex("password");
            int premiumIndex = cursor.getColumnIndex("premium");
            int salIndex = cursor.getColumnIndex("salt");
            int bioIndex = cursor.getColumnIndex("biometric");

            // Verificar si se encontró el usuario en la base de datos
            if (emailIndex != -1 && cursor.moveToFirst()) {
                // Obtener los datos del usuario desde el cursor
                int id = cursor.getInt(idIndex);
                String userEmail = cursor.getString(emailIndex);
                String password = cursor.getString(pwdIndex);
                int premium = cursor.getInt(premiumIndex);
                String sal = cursor.getString(salIndex);
                int biometric = cursor.getInt(bioIndex);

                // Crear un nuevo objeto UserResponse con los datos obtenidos
                user = new UserResponse(id, userEmail, password, premium, sal, biometric);
            }
            // Cerrar el cursor y la base de datos
            cursor.close();
            db.close();

        } catch (Exception e) {
            Log.e("DbManager", "Error al obtener el usuario por email", e.getCause());
        }

        // Devolver el usuario encontrado (o null si no se encontró)
        return user;
    }

    /**
     * Obtiene las contraseñas para un usuario específico.
     *
     * @param userId ID del usuario.
     * @return Cursor con las contraseñas asociadas al usuario.
     * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
     */
    public Cursor getPasswordsForUser(int userId) {
        try {
            String query = "SELECT * FROM password WHERE user_id = ? ORDER BY name";
            return db.rawQuery(query, new String[]{String.valueOf(userId)});
        } catch (SQLException e) {
            Log.e("DbManager", "Error de SQL: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Verifica si hay usuarios con biometría habilitada en la base de datos.
     *
     * @return true si hay usuarios con biometría habilitada, false en caso contrario. También
     * completa el SharedPreference STORAGE con los datos del usuario
     * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
     */
    public boolean userWhitBiometrics() {
        try {
            this.open();
            String query = "SELECT * FROM user WHERE biometric = 1";
            Cursor cursor = db.rawQuery(query, null);
            int emailIndex = cursor.getColumnIndex("email");
            int idIndex = cursor.getColumnIndex("id");
            int premiumIndex = cursor.getColumnIndex("premium");

            if (emailIndex != -1 && cursor.moveToFirst()) {
                // Obtengo el string del email del usuario
                String userEmail = cursor.getString(emailIndex);
                int pre = cursor.getInt(premiumIndex);
                Log.i("TAG", "userWhitBiometrics: Usuario logeado con biometria: " + userEmail);
                // Envio a guardar en el Storage los datos del usuario
                saveStorage(cursor.getInt(idIndex), 1, pre);
                //cierro el cursor y retorno true
                cursor.close();
                return true;// Las credenciales son válidas
            }

        } catch (SQLException e) {
            Log.e("DbManager", "Error de SQL: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
        return false;// Las credenciales son inválidas
    }

    /**
     * Obtiene una lista de objetos `PasswordResponse` para un usuario específico.
     *
     * @param userId El ID del usuario.
     * @return Una lista de objetos `PasswordResponse` que contienen la información de las contraseñas
     * del usuario, o `null` si ocurre un error.
     */
    public List<PasswordResponse> getPasswordsListForUserId(int userId) {
        List<PasswordResponse> passwords = null;
        Cursor cursor = null;
        this.open();

        try {
            passwords = new ArrayList<>();
            cursor = getPasswordsForUser(userId);
            int idIndex = cursor.getColumnIndex(PASSWORD_ID);
            int usernameIndex = cursor.getColumnIndex(PASSWORD_USERNAME);
            int urlIndex = cursor.getColumnIndex(PASSWORD_URL);
            int keywordIndex = cursor.getColumnIndex(PASSWORD_KEYWORD);
            int descriptionIndex = cursor.getColumnIndex(PASSWORD_DESCRIPTION);
            int nameIndex = cursor.getColumnIndex(PASSWORD_NAME);


            if (idIndex != -1) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(idIndex);
                    String username = cursor.getString(usernameIndex);
                    String url = cursor.getString(urlIndex);
                    String keyword = cursor.getString(keywordIndex);
                    String description = cursor.getString(descriptionIndex);
                    String name = cursor.getString(nameIndex);

                    PasswordResponse passwordResponse = new PasswordResponse(id, username, url, keyword, description, name);
                    passwords.add(passwordResponse);
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "Error al crear lista de contraseñas", e.getCause());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            this.close();
        }

        return passwords;
    }

    /**
     * Elimina una contraseña de la base de datos.
     *
     * @param passwordId El ID de la contraseña a eliminar.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    public void deletePassword(int passwordId) {
        try {
            // Abre la base de datos
            this.open();
            // Elimina la contraseña con el ID especificado
            db.delete(TB_PASSWORD, PASSWORD_ID + " = ?", new String[]{String.valueOf(passwordId)});
        } catch (SQLException e) {
            Log.e("DbManager", "Error al eliminar la contraseña: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cierra la base de datos
            this.close();
        }
    }

    /**
     * Recupera los detalles de una contraseña a partir de su ID.
     *
     * @param passwordId El ID de la contraseña que se desea recuperar.
     * @return Un objeto PasswordCredentials que contiene los detalles de la contraseña, o null si no se encuentra.
     */
    public PasswordResponse getPasswordDetails(int passwordId, int userId) {
        this.open();
        PasswordResponse passwordResponse = null;
        Cursor cursor = null;

        Log.i("TAG", "llega el id de password: " + passwordId);

        try {
            // Consulta SQL para seleccionar detalles de contraseña por ID
            String query = "SELECT * FROM password WHERE id = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(passwordId)});

            // Obtener los índices de las columnas en el resultado del cursor
            int nameIndex = cursor.getColumnIndex(PASSWORD_NAME);
            int usernameIndex = cursor.getColumnIndex(PASSWORD_USERNAME);
            int keywordIndex = cursor.getColumnIndex(PASSWORD_KEYWORD);
            int urlIndex = cursor.getColumnIndex(PASSWORD_URL);
            int descriptionIndex = cursor.getColumnIndex(PASSWORD_DESCRIPTION);
            String salt = this.getSaltById(userId);

            // Verificar si el cursor tiene datos
            if (cursor.moveToFirst()) {
                String name = cursor.getString(nameIndex);
                String username = cursor.getString(usernameIndex);
                String keyword = HashUtility.decrypt(cursor.getString(keywordIndex), salt);
                String url = cursor.getString(urlIndex);
                String description = cursor.getString(descriptionIndex);
                Log.d("DbManager", "Name: " + name);
                Log.d("DbManager", "Username: " + username);
                Log.d("DbManager", "Keyword: " + keyword);
                Log.d("DbManager", "URL: " + url);
                Log.d("DbManager", "Description: " + description);

                passwordResponse = new PasswordResponse(passwordId, username, url, keyword, description, name);
            }
        } catch (SQLException e) {
            // Capturar excepción en caso de error
            Log.e("DbManager", "Error al obtener detalles de la contraseña: " + e.getMessage());
        } catch (Exception e) {
            Log.e("DbManager", "ERROR: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (cursor != null) {
                // Cerrar el cursor para liberar recursos
                cursor.close();
            }
            this.close();
        }

        return passwordResponse;
    }

    /**
     * Actualiza una contraseña en la base de datos.
     *
     * @param passwordId      El ID de la contraseña que se va a actualizar.
     * @param updatedPassword Un objeto PasswordCredentials que contiene los nuevos detalles de la contraseña.
     * @return true si la actualización se realizó con éxito, o false en caso de error.
     */
    public boolean updatePassword(int passwordId, PasswordCredentials updatedPassword, int userId) {
        this.open();
        try {
            // Crear objeto ContentValues para almacenar los nuevos valores
            ContentValues content = new ContentValues();
            content.put(PASSWORD_NAME, updatedPassword.getName());
            content.put(PASSWORD_USERNAME, updatedPassword.getUsername());
            String salt = this.getSaltById(userId);
            String pass = HashUtility.encrypt(updatedPassword.getKeyword(), salt);
            content.put(PASSWORD_KEYWORD, pass);
            content.put(PASSWORD_URL, updatedPassword.getUrl());
            content.put(PASSWORD_DESCRIPTION, updatedPassword.getDescription());

            // Definir la cláusula WHERE para la actualización
            String whereClause = "id = ?";
            String[] whereArgs = {String.valueOf(passwordId)};

            // Realizar la actualización en la base de datos
            int rowsAffected = db.update(TB_PASSWORD, content, whereClause, whereArgs);

            // Verificar si se actualizaron filas y retornar true si se actualizó al menos una fila
            return rowsAffected > 0;
        } catch (SQLException e) {
            // Capturar excepción en caso de error
            Log.e("DbManager", "Error al actualizar la contraseña: " + e.getMessage());
            return false;
        } catch (Exception e) {
            Log.e("DbManager", "Error: " + e.getMessage());
            return false;
        } finally {
            this.close();
        }
    }

    /**
     * Guarda los datos de usuario en SharedPreferences.
     *
     * @param userId         ID del usuario.
     * @param biometricValue Valor de la opción biométrica.
     */
    public void saveStorage(int userId, int biometricValue, int premiumValue) {
        Log.i("TAG", "LLega a saveStorage: " + userId + " " + biometricValue + " " + premiumValue);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Verifica si el userId es diferente de -1 antes de guardarlo
        if (userId != -1) {
            Log.i("TAG", "saveStorage: El usuario no es nulo completo userID");
            editor.putInt("userId", userId);
        }
        // si el valor de premium es 0 verifica llamando a la API
        if (premiumValue == 0) {
            Log.i("TAG", "saveStorage: El valor de premium es 0");
            String userEmail = getEmailById(userId); // llamar al metodo privado para obtener el email del usuario actual
            Log.i("TAG", "saveStorage: obtengo el email por id: " + userEmail);
            if (userEmail != null) {
                NetworkUtils.getPremiumStatusFromAPI(userEmail, new NetworkUtils.PremiumStatusCallback() {
                    @Override
                    public void onResult(boolean isPremium) {
                        if (isPremium) {
                            if(setPremiumTrue(userId)){
                                editor.putInt("premium", 1);
                                editor.apply();    
                            }
                        } else {
                            editor.putInt("premium", 0);
                            editor.apply();
                        }
                        editor.putInt("biometric", biometricValue);
                        editor.apply();
                    }
                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                        Log.e("DbManager", "saveStorage: Error al consultar el estado premium: " + e.getMessage());
                    }
                });
            } else {
                // Manejar el caso donde userEmail es nulo
                Log.e("DbManager", "El email del usuario es nulo.");
                editor.putInt("biometric", biometricValue);
                editor.apply();
            }
        } else if (premiumValue != -1) {
            editor.putInt("premium", premiumValue);
            editor.putInt("biometric", biometricValue);
            editor.apply();
        } else {
            editor.putInt("biometric", biometricValue);
            editor.apply();
        }
        Log.i("TAG", "SharedPreference completado: " + sharedPreferences.getAll());
    }

    /**
     * Retorna el correo electrónico asociado al ID de usuario especificado.
     *
     * @param userId El ID del usuario cuyo correo electrónico se desea obtener.
     * @return El correo electrónico del usuario o null si no se encuentra en la base de datos.
     */
    private String getEmailById(int userId) {
        // Registra en el log la llegada del ID de usuario
        Log.i("TAG", "getEmailById llega el ID: " + userId);

        Cursor cursor = null; // Inicializa el cursor como null
        String email = null; // Inicializa el correo electrónico como null

        this.open(); // Abre la conexión a la base de datos

        try {
            // Define la consulta SQL para seleccionar el correo electrónico del usuario por su ID
            String query = "SELECT email FROM user WHERE id = ?";

            // Ejecuta la consulta SQL y obtiene el cursor con los resultados
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            // Obtiene el índice de la columna "email" en el cursor
            int indexEmail = cursor.getColumnIndex("email");

            // Verifica si se encontró el usuario en la base de datos y mueve el cursor al primer resultado
            if (indexEmail != -1 && cursor.moveToFirst()) {
                // Obtiene el correo electrónico del usuario desde el cursor
                email = cursor.getString(indexEmail);
            }
        } catch (Exception e) {
            // Registra un error en el log si ocurre un problema al obtener el correo electrónico
            Log.e("DbManager", "Error al obtener el email por ID", e.getCause());
            e.printStackTrace();
        } finally {
            // Asegura cerrar el cursor y la conexión a la base de datos
            if (cursor != null) {
                cursor.close();
            }
            this.close(); // Cierra la conexión a la base de datos
        }

        // Retorna el correo electrónico del usuario o null si no se encuentra en la base de datos
        return email;
    }

    /**
     * Actualiza el estado premium del usuario en la base de datos local a `true`.
     *
     * @param userId El ID del usuario.
     * @return `true` si se actualizó el estado premium, `false` en caso contrario.
     */
    private boolean setPremiumTrue(int userId) {
        this.open();
        try {
            // Crear objeto ContentValues para almacenar los nuevos valores
            ContentValues values = new ContentValues();
            values.put(PREMIUM, 1);

            // Definir la cláusula WHERE para la actualización
            String whereClause = "id = ?";
            String[] whereArgs = {String.valueOf(userId)};

            // Realizar la actualización en la base de datos
            int rowsUpdated = db.update(TB_USER, values, whereClause, whereArgs);

            // Verificar si se actualizaron filas y retornar true si se actualizó al menos una fila
            return rowsUpdated > 0;
        } catch (SQLException e) {
            // Capturar excepción en caso de error
            Log.e("DbManager", "Error al actualizar estado premium: " + e.getMessage());
            return false;
        } finally {
            this.close();
        }
    }

    //Fin DbManager
}
