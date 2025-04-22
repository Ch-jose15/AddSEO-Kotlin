import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.addseo.R

class SupportFragment : Fragment(R.layout.fragment_support) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnCall).setOnClickListener {
            // Crear un intent para abrir la aplicación de teléfono con el número marcado
            val phoneNumber = "tel:+34 680318581"
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber))
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.btnMessage).setOnClickListener {
            // Acción para el botón de mensaje (esto se puede llenar según necesidad)
        }

        view.findViewById<Button>(R.id.btnAgenda).setOnClickListener {
            // Acción para el botón de agenda (esto se puede llenar según necesidad)
        }
    }
}
