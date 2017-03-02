package teezha.bcit.addingtomapserver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class GetAttributesDialog extends AppCompatActivity {


    EditText etvUserName;
    EditText etvComment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_attributes_dialog);

        setFinishOnTouchOutside(false);

        Button btnSave = (Button) findViewById(R.id.btnSave);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);

        etvUserName =
                (EditText) findViewById(R.id.etvUserName);

        etvComment =
                (EditText) findViewById(R.id.etvComment);

    }

    public void onClickSave(View v) {
        Intent data = new Intent();
        data.putExtra("user_name",etvUserName.getText().toString());
        data.putExtra("comment", etvComment.getText().toString());
        setResult(RESULT_OK,data);
        finish();
    }

    public void onClickCancel(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }

}
