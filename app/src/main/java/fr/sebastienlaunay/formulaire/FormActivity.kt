package fr.sebastienlaunay.formulaire

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_form.*

class FormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        /********************************************************************/
        /* TEST du comportement de disablePatternValidation                 */
        /********************************************************************/
        // On met une valeur incorrecte dans le champ du téléphone
        phone.value="44"   // A ce stade, le message d'erreur ainsi que la croix est affichée dans la view

        Log.d("LOGFORM","Validation du phone pattern = ${phone.isValid()}") // La valeur est "false"

        // On demande de ne plus faire de vérification du champ
        phone.disablePatternValidation()
        // On refait une demande de validation
        phone.validate()

        // La valeur de phone.isValid() est maintenant à true.
        Log.d("LOGFORM","Validation du phone pattern = ${phone.isValid()}")

        /********************************/
        /* FIN DU TEST                  */
        /********************************/



        /**********************************************************************************************************/
        /*Permet de faire la vérification/validation en externe au lieu de le faire dans PersoTextInputLayout     */
        /**********************************************************************************************************/
        /*
         etEmail.withExternalValidator(object : PersoTextInputLayout.Validator {
            override fun isValid(): Boolean {
                // Faire la vérification
                return true // or False !
            }
        })
        */

        /********************************/
        /* FIN DU TEST                  */
        /********************************/


        /*****************************************************************************************/
        /* Permet de vérifer que le mot de passe de confirmation est identique au mot de passe   */
        /* La vérification se fera lors de la perte du focus sur le mot de passe de confirmation */
        /*****************************************************************************************/
        passwordConfirmation.isIdenticalTo(password)
        eMailConfirmation.isIdenticalTo(eMail)



        btnValidateForm.setOnClickListener {

            resultat.setText("La validation du formulaire est vu dans un autre article")

        }
    }
}
