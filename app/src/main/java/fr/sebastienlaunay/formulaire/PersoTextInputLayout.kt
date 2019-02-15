package fr.sebastienlaunay.formulaire

import android.app.DatePickerDialog
import android.content.Context
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.text.InputType
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.text.InputFilter
import android.util.Log
import android.view.View
import fr.sebastienlaunay.data.Constants
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.format.DateTimeFormat
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class PersoTextInputLayout : TextInputLayout, UField {

    enum class Type(val inputType: Int = -1, val hint: Int = -1) {
        FirstName(0, R.string.common_field_first_name_hint),
        LastName(1, R.string.common_field_last_name_hint),
        Email(2, R.string.common_field_email_hint),
        EmailConfirmation(3, R.string.common_field_email_confirmation_hint),
        Address(4, R.string.common_field_address_address_hint),
        PostalCode(5, R.string.common_field_postal_code_hint),
        City(6, R.string.common_field_city_hint),
        Phone(7, R.string.common_field_phone_hint),
        MobilePhone(8, R.string.common_field_mobile_phone),
        LandlinePhone(9, R.string.common_field_land_line_phone),
        BirthDay(10, R.string.common_field_birthday_hint),
        Password(11, R.string.common_field_password_hint),
        NewPassword(12, R.string.common_field_new_password_hint),
        OldPassword(13, R.string.common_field_old_password_hint),
        ConfirmPassword(14, R.string.common_field_password_confirmation_hint),
        BirthDayOlderThan18(15, R.string.common_field_birthday_hint),
        Text;

        companion object {
            fun from(inputType: Int): Type = Type.values().first { inputType == it.inputType }
        }
    }

    private lateinit var mEditText: TextInputEditText
    private lateinit var mType: Type
    private var mHideValidIndicator: Boolean = false
    private var mExternalValidator: Validator? = null
    private var mDisablePatternValidation = false
    private val mCalendar = Calendar.getInstance(TimeZone.getDefault()) // Utilisé par le date picker

    // Permet de vérifier que le contenu (la valeur) d'un autre PersoTextInputLayout est identique à celui-ci
    private var mReferenceToConfirm: PersoTextInputLayout? = null

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {

        //mEditText = AppCompatEditText(context)
        mEditText = TextInputEditText(context)
        mEditText.maxLines = 1
        mEditText.imeOptions = EditorInfo.IME_ACTION_NEXT

        val a = context.obtainStyledAttributes(attrs, R.styleable.PersoTextInputLayout, defStyleAttr, 0)

        // Récupération de la valeur de app:edit_text_hint défini dans le fichier xml
        val customHint = a.getString(R.styleable.PersoTextInputLayout_edit_text_hint)

        customHint?.let { Log.d("LOGFORM", "customerHint = $it") } ?: run { Log.d("LOGFORM", "customHint non défini") }

        // Récupération du type de champs provenant de app:input_type défini dans le fichier xml
        mType = Type.from(a.getInt(R.styleable.PersoTextInputLayout_input_type, -1))

        if (mType == Type.Text) {
            Log.d("LOGFORM", "app:input_type n'a pas été défini dans le fichier xml - mType.")
        } else {
            Log.d(
                "LOGFORM",
                "app:input_type a été défini dans le fichier xml avec mType.inputType : ${mType.inputType} - mType.hint correspondant est  : ${context.getString(
                    mType.hint
                )} - "
            )
        }

        // Récupération du Style défini au niveau de app:color_style du fichier xml.
        // Si aucun style défini au niveau du xml, utilisation du style PersoEditTextGrey_EditText
        val style = a.getResourceId(R.styleable.PersoTextInputLayout_color_style, R.style.PersoTextInputLayout_EditText)

        mEditText.setTextAppearance(context, style)

        // Récupération de app:hide_valid_indicator défini dans le fichier xml
        // Si celui-ci n'existe pas, prend la valeur false par défaut
        // Cela va permettre d'afficher ou de cacher l'indicateur de validation du champ dans la méthode valide()
        mHideValidIndicator = a.getBoolean(R.styleable.PersoTextInputLayout_hide_valid_indicator, false)

        a.recycle()


        // S'il y a une valeur dans app:edit_text_hint ou s'il y a une valeur dans app:input_type
        // (Dans le cas contraire, la valeur sera celle indiquée dans android:hint défini dans le fichier xml.
        if (customHint != null || mType != Type.Text) {

            // hint = customHint , si il y a une valeur dans customerHint
            // Sinon, hint = la valeur dans mType.hint
            hint = customHint ?: context.getString(mType.hint)

            // Pas sur de l'utilité de mettre un contentDescription a la vue, sachant que ce n'est pas une image
            contentDescription = customHint ?: context.getString(mType.hint)
        }


        mEditText.apply {

            // En fonction du type du champ, on applique l'inputType correspondant.
            // Cela permet, entre autre, d'afficher le clavier correspondant.
            // https://developer.android.com/reference/android/text/InputType
            inputType = when (mType) {
                Type.Email -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                Type.Phone, Type.MobilePhone, Type.LandlinePhone -> InputType.TYPE_CLASS_PHONE
                Type.Password, Type.ConfirmPassword, Type.OldPassword, Type.NewPassword -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                Type.PostalCode -> InputType.TYPE_CLASS_NUMBER
                Type.BirthDay, Type.BirthDayOlderThan18 -> InputType.TYPE_NULL

                else -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            }

            // Permet d'appliquer des filtres en fonction du type du champ.
            when (mType) {
                // Par exemple, l'edittext pour un type Phone, sera limiter à PHONE_NUMBER_LENGTH caractères
                Type.Phone -> filters = arrayOf(InputFilter.LengthFilter(Constants.InputType.PHONE_NUMBER_LENGTH))
                Type.FirstName, Type.LastName -> filters = arrayOf(InputFilter.LengthFilter(Constants.InputType.MAX_LENGTH))
            }
        }

        // Ajout de l'editText à la vue
        addView(mEditText)


        // Lors que l'on a déjà le focus, c'est le click listener qui est appelé.
        mEditText.setOnClickListener {

            when (mType) {
                Type.BirthDay, Type.BirthDayOlderThan18 -> openBirthdayDatePicker()
            }
        }

        mEditText.setOnFocusChangeListener { _, focus ->

            Log.d("LOGFORM", "FocusChanged - View : $contentDescription - Focus : $focus ")

            validate(!focus)

            if (focus && mType == Type.BirthDay) openBirthdayDatePicker()
            if (focus && mType == Type.BirthDayOlderThan18) openBirthdayDatePicker()
        }
    }

    fun isEmpty(): Boolean = value.isNullOrEmpty()

    var value: String?
        get() = mEditText.text.toString()
        set(value) {
            mEditText.apply {
                setText(value ?: "")
                setSelection(mEditText.text?.length ?: 0)
            }
            validate()
        }

    var dateValue: Date?
        get() = mCalendar.time
        set(date) {
            date?.let {
                mCalendar.time = it
                value = SimpleDateFormat(Constants.InputType.BIRTHDAY_FORMAT, Locale.getDefault()).format(it)
            }
        }

    override fun isValid(): Boolean {

        // Si la vérification est faire en externe
        mExternalValidator?.let {
            // On retourne le résultat de cette vérification externe
            return it.isValid()
        }
        // Sinon, on retourne la vérification interne suivante :
            ?: run {

                return (visibility != View.VISIBLE || !isEnabled)
                        || (!isEmpty()
                        && (mDisablePatternValidation || isValidPattern())
                        && (mReferenceToConfirm == null || isReferenceValidConfirmation())
                        )
            }

        // La vérification interne correspond la logique suivante :
        //
        //        Le Champ PersoTextInputLayout est Valide
        //
        //        SI (
        //            Ce champ n'est pas visible (View.VISIBLE)
        //            OU
        //            Si ce champ n'est pas activé (enabled)
        //        )
        //        OU SI (
        //                Ce champ n'est pas vide (!isEmpty())
        //                ET (
        //                     Si l'on a demandé de ne pas faire de Validation de Pattern (mDisablePatternValidation)
        //                     OU
        //                     Si le Pattern est validé (isValidPattern()
        //                )
        //                ET (
        //                     S'il n'y a pas d'autre référence à confimer (mReferenceToConfirm)
        //                     OU
        //                     Si la Référence est bien confirmée (isReferenceValidConfirmation())
        //                )
        //         )
    }


    private fun openBirthdayDatePicker() {
        Log.d("LOGFORM", "Affichage du date picker")
        DatePickerDialog(
            context,
            R.style.PersoTextInputLayout_DatePickerDialog,

            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                mCalendar.set(Calendar.YEAR, year)
                mCalendar.set(Calendar.MONTH, monthOfYear)
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                dateValue = mCalendar.time
            },
            mCalendar.get(Calendar.YEAR),
            mCalendar.get(Calendar.MONTH),
            mCalendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = Date().time
            show()
        }
    }

    // Permet d'effectuer la validation du PersoTextInputLayout
    fun validate(showError: Boolean = true) {

        // Si on ne doit pas afficher l'erreur, on enlève l'icone X ou V
        if (!showError) {
            mEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }

        // Si on ne doit pas afficher l'erreur, ou si le champ est vide, on indique alors aucune erreur
        if (!showError || isEmpty()) {
            error = null
            isErrorEnabled = false  // Permet de ne pas avoir d'espace vide après l'affichage d'une erreur
            // Besoin d'être défini après un error = null
        } else { // Sinon : On vérifie que le champ est valide
            when {

                // Si le champ n'est pas valide
                !isValid() -> {
                    // On affiche le message d'erreur correspondant au Type du champ
                    error = when (mType) {
                        Type.LandlinePhone -> context.getString(R.string.form_error_landline_phone_format)
                        Type.MobilePhone -> context.getString(R.string.form_error_mobile_phone_format)
                        Type.Email -> context.getString(R.string.form_error_email_format)
                        Type.Phone -> context.getString(R.string.form_error_phone_format)
                        Type.Password, Type.NewPassword, Type.OldPassword -> context.getString(R.string.form_error_password_format)
                        Type.ConfirmPassword -> context.getString(R.string.form_error_confirm_password_format)
                        Type.FirstName, Type.LastName -> context.getString(R.string.form_error_name_format)
                        Type.EmailConfirmation -> context.getString(R.string.form_error_confirm_email_format)
                        Type.PostalCode -> context.getString(R.string.form_error_postal_code)
                        Type.BirthDayOlderThan18 -> context.getString(R.string.form_error_birthday_not_older_than_18)
                        else -> null
                    }

                    if (error == null) isErrorEnabled = false  // Voir ci-dessus la raison de cette ligne

                    // On affiche l'icone X
                    mEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ico_formular_field_error, 0)
                }
                // Si le champ est valide
                else -> {
                    // On affiche aucun message d'erreur
                    error = null
                    isErrorEnabled = false

                    // Sous condition que app:hide_valid_indicator n'est pas indiqué à true dans le fichier xml
                    val drawable = when (mHideValidIndicator) {
                        true -> 0
                        false -> R.drawable.ico_formular_field_ok
                    }
                    // on affiche l'icone de validation V
                    mEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0)
                }
            }
        }
    }

    // Permet de ne faire aucune validation du pattern en interne.
    fun disablePatternValidation(): PersoTextInputLayout {
        mDisablePatternValidation = true
        return this
    }

    // Permet de faire la validation du champ en externe
    fun withExternalValidator(validator: Validator): PersoTextInputLayout {
        mExternalValidator = validator
        return this
    }

    // Permet de vérifier que la valeur d'une référence (d'un PersoTextInputLayout) est équivalente à une autre référence
    fun isIdenticalTo(reference: PersoTextInputLayout): PersoTextInputLayout {
        mReferenceToConfirm = reference
        return this
    }

    private fun isReferenceValidConfirmation(): Boolean = mReferenceToConfirm?.value == this.value

    private fun isValidPattern(): Boolean {

        value?.let { value ->
            when (mType) {
                Type.LandlinePhone -> return Regex(Constants.InputType.LANDLINE_PHONE_PATTERN).matches(value)
                Type.MobilePhone -> return Regex(Constants.InputType.MOBILE_PHONE_PATTERN).matches(value)
                Type.Phone -> return (Regex(Constants.InputType.LANDLINE_PHONE_PATTERN).matches(value)
                    .xor(Regex(Constants.InputType.MOBILE_PHONE_PATTERN).matches(value)))
                Type.Password, Type.NewPassword, Type.OldPassword -> return Regex(Constants.InputType.PASSWORD_PATTERN).matches(
                    value
                )
                Type.FirstName, Type.LastName -> return Regex(Constants.InputType.NAME_PATTERN).matches(value)
                Type.Email -> return Regex(Constants.InputType.EMAIL_PATTERN).matches(value)
                Type.PostalCode -> return Regex(Constants.InputType.CODE_POSTAL_PATTERN).matches(value)
                Type.BirthDayOlderThan18 -> return isUser18Older(value)
                else -> return true
            }
        }
    }

    interface Validator {
        fun isValid(): Boolean
    }

    fun isUser18Older(value: String): Boolean {
        val dtf = DateTimeFormat.forPattern(Constants.InputType.BIRTHDAY_FORMAT)
        val date = dtf.parseDateTime(value)
        val minAge = DateTime()
        val days = Days.daysBetween(date, minAge.minusYears(18))
        return days.getDays() >= 0
    }
}