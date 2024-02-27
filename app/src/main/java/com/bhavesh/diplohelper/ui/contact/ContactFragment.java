package com.bhavesh.diplohelper.ui.contact;

import androidx.lifecycle.ViewModelProvider;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bhavesh.diplohelper.R;
import com.bhavesh.diplohelper.databinding.FragmentContactBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ContactFragment extends Fragment {

    private FragmentContactBinding binding;

    private EditText inputEditText;
    private TextView outputTextView;
    private ImageButton sendButton;
    private ProgressBar progressBar;

    public static ContactFragment newInstance() {
        return new ContactFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentContactBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        inputEditText = root.findViewById(R.id.inputEditText);
        outputTextView = root.findViewById(R.id.outputTextView);
        sendButton = root.findViewById(R.id.sendButton);
        progressBar = root.findViewById(R.id.progressBar);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = inputEditText.getText().toString();
                new OpenAIAPIRequest().execute(userInput);
            }
        });

        return root;
    }

    class OpenAIAPIRequest extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // Show loading indicator and set loading text
            progressBar.setVisibility(View.VISIBLE);
            outputTextView.setText("Loading...");
        }

        @Override
        protected String doInBackground(String... params) {
            String apiKey = "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";
            String endpoint = "https://api.openai.com/v1/chat/completions";

            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            String jsonInput = "{\"model\":\"gpt-3.5-turbo\",\"messages\":[{\"role\":\"system\",\"content\":\"You are a helpful assistant.\"},{\"role\":\"user\",\"content\":\"" + params[0] + "\"}]}";

            RequestBody body = RequestBody.create(mediaType, jsonInput);
            Request request = new Request.Builder()
                    .url(endpoint)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .header("model","gpt-3.5-turbo")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // Hide loading indicator
            progressBar.setVisibility(View.GONE);

            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONArray choicesArray = jsonResponse.getJSONArray("choices");

                    if (choicesArray.length() > 0) {
                        JSONObject firstChoice = choicesArray.getJSONObject(0);
                        JSONObject message = firstChoice.getJSONObject("message");
                        String assistantResponse = message.getString("content");

                        outputTextView.setText(assistantResponse);
                    } else {
                        outputTextView.setText("No assistant response found in the choices array");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    outputTextView.setText("Error parsing JSON response");
                }
            } else {
                outputTextView.setText("Error in API request");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
