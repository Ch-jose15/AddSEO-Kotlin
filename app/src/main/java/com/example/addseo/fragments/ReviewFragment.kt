import android.os.Bundle
import android.view.View
import android.widget.Button
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.example.addseo.R

class ReviewFragment : Fragment(R.layout.fragment_review) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnReview).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://g.co/kgs/5Ubq2zk"))
            startActivity(intent)
        }
    }
}
