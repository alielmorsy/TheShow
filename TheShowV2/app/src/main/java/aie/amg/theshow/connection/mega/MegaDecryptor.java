package aie.amg.theshow.connection.mega;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MegaDecryptor {
    private Cipher cipher;
    private IvParameterSpec ivSpec;
    private SecretKeySpec skeySpec;
    private CipherOutputStream cipherOut;


    public MegaDecryptor(String link) throws Exception {

        if (link != null) {
            link = link.replaceAll(" ", "");
            String[] list = checkURL(link.trim());

            init(list[1]);

        } else {
            // throw new IllegalArgumentException("Bad Mega Lint " + link);
        }
    }

    public static String createLink(String url) throws Exception {


        String[] s = checkURL(url.replaceAll(" ", "").replaceAll("\n", ""));

        Log.d("crypt", Arrays.toString(s));
        Log.d("crypt", s[0]);
        Log.d("crypt", s[1] + " aa");

        String file_id = s[0];
        byte[] file_key = MegaCrypt.base64_url_decode_byte(s[1].trim());

        int[] intKey = MegaCrypt.aByte_to_aInt(file_key);
        JSONObject json = new JSONObject();
        try {
            json.put("a", "g");
            json.put("g", "1");
            json.put("p", file_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject file_data = new JSONObject(api_request(json.toString()));

        int[] keyNOnce = new int[]{intKey[0] ^ intKey[4], intKey[1] ^ intKey[5], intKey[2] ^ intKey[6], intKey[3] ^ intKey[7], intKey[4], intKey[5]};
        byte[] key = MegaCrypt.aInt_to_aByte(keyNOnce[0], keyNOnce[1], keyNOnce[2], keyNOnce[3]);

        String attribs = (file_data.getString("at"));

        attribs = new String(MegaCrypt.aes_cbc_decrypt(MegaCrypt.base64_url_decode_byte(attribs), key));


        try {
            return file_data.getString("g");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String api_request(String data) throws IOException {


        String urlString = "https://g.api.mega.co.nz/cs?id=" + Integer.MAX_VALUE;

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //  connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36");
        connection.setRequestMethod("POST"); //use post method
        connection.setDoOutput(true); //we will send stuff
        connection.setDoInput(true); //we want feedback
        connection.setUseCaches(false); //no caches
        connection.setAllowUserInteraction(false);
        connection.setRequestProperty("Content-Type", "text/xml");

        try (OutputStream out = connection.getOutputStream()) {
            OutputStreamWriter wr = new OutputStreamWriter(out);
            wr.write("[" + data + "]"); //data is JSON object containing the api commands
            wr.flush();
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        } //in this case, we are ensured to close the output stream

        InputStream in = connection.getInputStream();
        StringBuilder response = new StringBuilder();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close(); //close the reader
        } catch (IOException e) {
            e.printStackTrace();
        } finally {  //in this case, we are ensured to close the input stream
            if (in != null)
                in.close();
        }

        return response.toString().substring(1, response.toString().length() - 1);


    }

    private static String[] checkURL(String link) throws Exception {
        ArrayList<String> list = new ArrayList<>();
        assert link != null : new NullPointerException("Fuck In Hell Null Link");
        Log.d("url", link);

        String regex = "";
        if (link.contains("file")) {
            regex = "(https://mega.nz/file)/(\\p{all}+)#(\\p{all}+)";
            Matcher matcher = Pattern.compile(regex).matcher(link);
            matcher.matches();
            list.add(matcher.group(2));
            list.add(matcher.group(3));
            Log.d("url", "new");
        } else if (link.contains("!")) {

            regex = "(https://mega.nz/#(\\p{all}*))!(\\p{all}+)!(\\p{all}+)";
            Matcher matcher = Pattern.compile(regex).matcher(link);
            matcher.matches();
            list.add(matcher.group(3));
            list.add(matcher.group(4));
        } else
            throw new Exception("Bad Link");


        return list.toArray(new String[0]);
    }

    public void decryptBytes(OutputStream outputStream, byte[] bytes, int read) throws IOException {
        if (cipherOut == null)
            cipherOut = new CipherOutputStream(outputStream, cipher);
        cipherOut.write(bytes, 0, read);
    }

    private void init(String keyString) {
        byte[] file_key = MegaCrypt.base64_url_decode_byte(keyString);
        int[] intKey = MegaCrypt.aByte_to_aInt(file_key);

        int[] keyNOnce = new int[]{intKey[0] ^ intKey[4], intKey[1] ^ intKey[5], intKey[2] ^ intKey[6], intKey[3] ^ intKey[7], intKey[4], intKey[5]};
        byte[] key = MegaCrypt.aInt_to_aByte(keyNOnce[0], keyNOnce[1], keyNOnce[2], keyNOnce[3]);

        int[] iiv = new int[]{keyNOnce[4], keyNOnce[5], 0, 0};
        byte[] iv = MegaCrypt.aInt_to_aByte(iiv);

        ivSpec = new IvParameterSpec(iv);
        skeySpec = new SecretKeySpec(key, "AES");


    }

    public void encrypt(FileInputStream inputStream, FileOutputStream outputStream) throws IOException {
        CipherInputStream cipherInput = new CipherInputStream(inputStream, cipher);
        byte[] b = new byte[1024];
        int c;
        while ((c = cipherInput.read(b)) > 0) {
            outputStream.write(b, 0, c);
        }
        outputStream.flush();
        cipherInput.close();
    }

    public void setDecrypt() {
        try {
            cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException e) {
            e.printStackTrace();
        }


    }

    public void setEncrypt() {
        try {
            cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public InputStream createCipherStream(InputStream inputStream) {
        return new CipherInputStream(inputStream, cipher);
    }

}
