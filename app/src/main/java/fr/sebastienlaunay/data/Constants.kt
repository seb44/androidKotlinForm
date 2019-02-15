package fr.sebastienlaunay.data

interface Constants {

    interface InputType {
        companion object {

            // (0|(\+33)|(0033)) : Contenant 0 ou +33 ou 0033

            // ^(0|(\+33)|(0033)) : Commençant par 0 ou +33 ou 0033
            // suivi de
            // [1-58_9] : un chiffre en 1 et 5 ou entre 8 et 9
            // suivi de et finnissant ($) par :
            // [0-9]{8}\$ : 8 chiffres entre 0 et 9 (équivalent à \\d{8}
            //const val LANDLINE_PHONE_PATTERN = "^(0|(\\+33)|(0033))[1-58-9][0-9]{8}$"
            //const val LANDLINE_PHONE_PATTERN = "^(0|(\\+33)|(0033))[1-58-9]\\d{8}$"

            // ^(0|(\+33)|(0033)) : Commençant par 0 ou +33 ou 0033
            // suivi de
            // \\s? : 0 ou 1 espace
            // suivi de
            // [1-58_9] : un chiffre en 1 et 5 ou entre 8 et 9
            // suivi de
            // \\.? : 0 ou 1 point
            // suivi de
            // \\d{2} : 2 chiffres
            // suivi de
            // \\.? : 0 ou 1 point
            // suivi de
            // \\d{2} : 2 chiffres
            // suivi de
            // \\.? : 0 ou 1 point
            // suivi de
            // \\d{2} : 2 chiffres
            // suivi de
            // \\.? : 0 ou 1 point
            // suivi de et finnissant par :
            // \\d{2}$ : 2 chiffres
            const val LANDLINE_PHONE_PATTERN = "^(0|(\\+33)|(0033))\\s?[1-58-9]\\.?\\d{2}\\.?\\d{2}\\.?\\d{2}\\.?\\d{2}$"
            const val MOBILE_PHONE_PATTERN = "^(0|(\\+33)|(0033))\\s?[6-7]\\.?\\d{2}\\.?\\d{2}\\.?\\d{2}\\.?\\d{2}$"

            // 8 caractères min, avec au moins 1 chiffre et 1 majuscule
            const val PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*\\d).{8,}$"

            // Caractères spéciaux non autorisés
            const val NAME_PATTERN =
                "[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz\\À\\Á\\Ã\\Ä\\Å\\Æ\\Ç\\È\\É\\Ê\\Ë\\Ì\\Í\\Î\\Ï\\Ð\\Ñ\\Ò\\Ó\\Ô\\Õ\\Ö\\Ø\\Ù\\Ú\\Û\\Ü\\Ý\\Þ\\ß\\à\\á\\â\\ã\\ä\\å\\æ\\ç\\è\\é\\ê\\ë\\ì\\í\\î\\ï\\ð\\ñ\\ò\\ó\\ô\\õ\\ö\\ø\\ù\\ú\\û\\ü\\ý\\Þ\\ß\\-\\'\\ƒ\\ ]*"


            const val MAX_LENGTH = 255

            const val EMAIL_PATTERN = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"

            const val CODE_POSTAL_PATTERN = "^(([0-8][0-9])|(9[0-5])|(2[ab]))[0-9]{3}$"

            const val BIRTHDAY_FORMAT = "dd/MM/yyyy"


            const val PHONE_NUMBER_LENGTH = 18
            const val CREDIT_CARD_CVV_LENGTH = 3
            const val CREDIT_CARD_CVV_PATTERN = "^\\d{3}$"
            const val CREDIT_CARD_NUMBER_LENGTH = 16
            const val CREDIT_CARD_VISA_PATTERN = "^4[0-9]{0,16}$"
            const val CREDIT_CARD_MASTERCARD_PATTERN = "(5[1-5][0-9]{0,14}|2[2-7][0-9]{0,14})$"
            const val LOYALTY_CARD_LENGTH = 14
            const val LOYALTY_CARD_BEGIN_PATTERN = "8939"

            const val PROMOTION_PATTERN = "-?(\\d+)(([.]|[,])(\\d+))?%?( ?€)?"
        }
    }
}