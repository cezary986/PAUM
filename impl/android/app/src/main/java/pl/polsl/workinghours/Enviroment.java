package pl.polsl.workinghours;

/**
 * Interfejs na wszystkie stałe  tym adresy endpointów w API
 */
public interface Enviroment {

    String API_URL = "http://192.168.0.199:90/paum/api/";
    //String API_URL = "http://157.158.203.41:90/paum/api/";
    int REQUEST_CACHE_TIME = 5; // min
    String START_WORK_DESC = "ZESKANUJ QR ZEBY ZACZAC PRACE";
    String WORK_DESC = "ZESKANUJ KOD ZEBY ZAKONCZYC PRACE";
    String END_WORK_DESC = "DZIS PRACOWALES";

    interface Groups {
        String EMPLOYEE  = "Employee";
        String EMPLOYER = "Employer";
    }

    enum Endpoints {
        /**
         * Zwykłe logowanie loginem i hasłem
         */
        LOGIN  (Enviroment.API_URL + "auth/login/"),
        /**
         * Logowanie przy pomocy refresh token'a
         */
        LOGIN_REFRESH  (Enviroment.API_URL + "auth/refresh/"),
        /**
         * Pobranie danych o samym sobie
         */
        PROFILE (Enviroment.API_URL + "user/profile/"),
        /**
         * Pobranie danych o grupach do jakich nalezy uzytkownik
         */
        GROUPS (Enviroment.API_URL + "user/groups/"),
        /**
         * Pobranie danych o samym sobie
         */
        EMPLOYEES_LIST (Enviroment.API_URL + "employee/"),
        /**
         * Pobranie danych o godzinach pracy pracownika
         */
        EMPLOYEES_WORK_HOURS (Enviroment.API_URL, "/employee/"),

        EMPLOYEES_WORK_HOURS_MINE(Enviroment.API_URL + "employee/work_hours/"),

        /**
         * Pobranie lub wysłanie kodu
         */
        CODE (Enviroment.API_URL + "code/");

        public final String[] urlParts;

        public String getUrl(String... param) {
            if (param.length == 0) {
                return this.urlParts[0];
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < this.urlParts.length; i++) {
                    stringBuilder.append(this.urlParts[i]);
                    stringBuilder.append(param[i]);
                }
                return stringBuilder.toString();
            }
        }

        Endpoints(String... url) {
            this.urlParts = url;
        }
    }
}
