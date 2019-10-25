package pl.polsl.workinghours.data.auth;
import android.content.Context;

import androidx.annotation.Nullable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;

public class CredentialDataSource {

    /** Nazwa pliku z zaszyfrowanym refresh tokenem */
    private String FILE_NAME = "app_data.txt";

    private Encryptor encryptor;
    private String accessToken;
    private String refreshToken;

    public  CredentialDataSource() {
        this.encryptor = new Encryptor();
    }

    /**
     * Odczytuje za zaszyfrowanego pliku refresh token
     *
     * @param context
     * @return refresh token
     * @throws Exception błąd odczytu pliku
     */
    @Nullable
    public String getRefreshToken(Context context) throws Exception {
        return this.refreshToken == null ? this.readFromFile(context) : this.refreshToken;
    }

    /**
     * Zapisuje refresh token do pliku szyfrując go
     *
     * @param refreshToken
     * @param context
     */
    public void saveRefreshToken(String refreshToken, Context context) {
        this.saveToFile(refreshToken, context);
        this.refreshToken = refreshToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    private String readFromFile(Context context) throws Exception {
        FileInputStream inputStream;

        byte[] bytes = new byte[1000];
        int bytesRead = 0;
        try {
            inputStream = context.openFileInput(FILE_NAME);

            if(inputStream.getChannel().size() == 0)
                return  null;
            else
                bytesRead = inputStream.read(bytes);
                inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] newBytes = new byte[bytesRead];
        System.arraycopy(bytes, 0, newBytes, 0, bytesRead);
        String result = this.encryptor.decrypt(newBytes);
        return result;
    }

    private void saveToFile(String data, Context context) {
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            FileWriter fileWriter = new FileWriter(outputStream.getFD());
            fileWriter.write("");
            outputStream.write(this.encryptor.encrypt(data));
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void clear(Context context) {
        this.accessToken = null;
        this.refreshToken = null;
        this.saveToFile("", context);
    }
}
