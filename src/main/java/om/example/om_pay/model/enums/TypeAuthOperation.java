package om.example.om_pay.model.enums;

/**
 * Enumération des types d'opérations d'authentification.
 * Utilisé par AuthOperationFactory pour sélectionner la bonne opération.
 */
public enum TypeAuthOperation {
    REGISTER,
    LOGIN,
    VERIFY_CODE_SECRET,
    RESEND_CODE_SECRET,
    CHANGE_PASSWORD,
    REFRESH_TOKEN
}
