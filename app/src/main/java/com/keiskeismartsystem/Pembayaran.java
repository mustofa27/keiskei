package com.keiskeismartsystem;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.keiskeismartsystem.dbsql.CityTransact;
import com.keiskeismartsystem.dbsql.WhereHelper;
import com.keiskeismartsystem.helper.ConnectionDetector;
import com.keiskeismartsystem.helper.UserSession;
import com.keiskeismartsystem.library.HttpClient;
import com.keiskeismartsystem.model.City;
import com.keiskeismartsystem.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Pembayaran extends AppCompatActivity {
    ProgressDialog _progress;
    private List<Payment> payments;
    private List<Kurir> kurirs;
    private List<Servis> servises, selectedServis;
    private List<Provinsi> provinsis;
    private int indikator = 0,harga;
    double berat;
    private int id_kota,id_payment,id_kurir;
    private String _alamat,_kodepos,_service,_ongkos;
    ArrayAdapter adapter,adapter1,adapter2,adapter3,adapter4;
    UserSession userSession;
    private Integer total_harga;
    private static ConnectionDetector _conn;
    public static final String _base_url = "https://keiskei.co.id/";
    EditText alamat,kodepos;
    Spinner kota,_provinsi,courier,servis,pembayaran;
    TextView hargaBarang,_ongkir,total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);
        alamat = (EditText) findViewById(R.id.alamat);
        kodepos = (EditText) findViewById(R.id.kodepos);
        kota = (Spinner) findViewById(R.id.kota);
        _provinsi = (Spinner) findViewById(R.id.provinsi);
        courier = (Spinner) findViewById(R.id.kurir);
        servis = (Spinner) findViewById(R.id.servis);
        pembayaran = (Spinner) findViewById(R.id.payment);
        hargaBarang = (TextView) findViewById(R.id.harga_barang);
        _ongkir = (TextView) findViewById(R.id.ongkir);
        total = (TextView) findViewById(R.id.total);
        _conn = new ConnectionDetector(this);
        userSession  = new UserSession(this);
        alamat.setText(userSession.getUserSessionData().getAddress());
        payments = new ArrayList<Payment>();
        kurirs = new ArrayList<Kurir>();
        servises = new ArrayList<Servis>();
        selectedServis = new ArrayList<Servis>();
        provinsis = new ArrayList<Provinsi>();
        if(!_conn.isConnectedToInternet()){
            Toast toast = Toast.makeText(this,"Tidak ada koneksi internet.", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            _progress = new ProgressDialog(this);
            _progress.setCancelable(true);
            _progress.setMessage("Getting data..");
            _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            _progress.setProgress(0);
            _progress.setMax(100);
            _progress.show();
            new LoadCheckout().execute();
        }
    }
    private class LoadCheckout extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(String... args) {
            String resp = "";
            try {
                String url = _base_url + "m/checkout/" + userSession.getUserSessionData().getID();
                HttpClient request = HttpClient.get(url);
                if (request.ok())
                {
                    resp = request.body();
                }else{
                    resp = "{RESP : 'ERROR' }";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resp;
        }

        protected void onPostExecute(String output) {
            Log.v("keiskeidebug", output);
            JSONObject json = new JSONObject();
            try {
                json = new JSONObject(output);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String resp = null;

            _progress.dismiss();
            try {
                resp = json.getString("RESP");
                if(resp.equals("SCSCKT")){
                    JSONObject konten = new JSONObject(json.getString("DATA"));
                    JSONArray payment = konten.getJSONArray("payment");
                    JSONArray kurir = konten.getJSONArray("courier");
                    JSONArray ongkir = konten.getJSONArray("harga");
                    berat = konten.getDouble("berat");
                    harga = konten.getInt("harga_barang");
                    hargaBarang.setText("RP "+String.valueOf(harga)+",00");
                    JSONArray provinsi = konten.getJSONArray("province");
                    for (int i = 0; i < payment.length(); i++){
                        JSONObject tmp = payment.getJSONObject(i);
                        payments.add(new Payment(tmp.getInt("id"),tmp.getString("name"),tmp.getString("account_name"),tmp.getString("account_number")));
                    }
                    adapter = new ArrayAdapter(Pembayaran.this,android.R.layout.simple_spinner_dropdown_item,payments);
                    pembayaran.setAdapter(adapter);
                    for (int i = 0; i < kurir.length(); i++){
                        JSONObject tmp = kurir.getJSONObject(i);
                        kurirs.add(new Kurir(tmp.getInt("id"),tmp.getString("code"),tmp.getString("name")));
                    }
                    for (int i = 0; i < ongkir.length(); i++){
                        JSONObject tmp = ongkir.getJSONObject(i);
                        servises.add(new Servis(tmp.getString("kurir"),tmp.getString("service"),tmp.getInt("ongkos"),tmp.getString("durasi")));
                    }
                    for (int i = 0; i < provinsi.length(); i++){
                        JSONObject tmp = provinsi.getJSONObject(i);
                        provinsis.add(new Provinsi(tmp.getInt("id"),tmp.getString("name")));
                    }
                    adapter1 = new ArrayAdapter(Pembayaran.this,android.R.layout.simple_spinner_dropdown_item,kurirs);
                    courier.setAdapter(adapter1);
                    courier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            int temp = selectedServis.size();
                            for (int i = 0; i < temp; i++){
                                selectedServis.remove(0);
                            }
                            for(int i = 0; i < servises.size(); i++){
                                if(servises.get(i).getNama_kurir().equals(((Kurir) courier.getSelectedItem()).getName()))
                                    selectedServis.add(servises.get(i));
                            }
                            adapter2 = new ArrayAdapter(Pembayaran.this,android.R.layout.simple_spinner_dropdown_item,selectedServis);
                            servis.setAdapter(adapter2);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    servis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            _ongkir.setText("RP "+String.valueOf(((Servis)servis.getSelectedItem()).getHarga())+",00");
                            total.setText("RP "+String.valueOf(((Servis)servis.getSelectedItem()).getHarga() + harga)+",00");
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    adapter3 = new ArrayAdapter(Pembayaran.this,android.R.layout.simple_spinner_dropdown_item,provinsis);
                    _provinsi.setAdapter(adapter3);
                    _provinsi.setSelection(konten.getInt("provinsi")-1);
                    _provinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            ArrayList<WhereHelper> whereHelpers = new ArrayList<WhereHelper>();
                            whereHelpers.add(new WhereHelper("ms_province_id",String.valueOf(((Provinsi) _provinsi.getSelectedItem()).getId())));
                            List<City> cities = (new CityTransact(Pembayaran.this)).get(whereHelpers);
                            adapter4 = new ArrayAdapter(Pembayaran.this,android.R.layout.simple_spinner_dropdown_item,cities);
                            kota.setAdapter(adapter4);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    ArrayList<WhereHelper> whereHelpers = new ArrayList<WhereHelper>();
                    whereHelpers.add(new WhereHelper("ms_province_id",konten.getString("provinsi")));
                    List<City> cities = (new CityTransact(Pembayaran.this)).get(whereHelpers);
                    adapter4 = new ArrayAdapter(Pembayaran.this,android.R.layout.simple_spinner_dropdown_item,cities);
                    kota.setAdapter(adapter4);
                    kota.setSelection(konten.getInt("kota")-1);
                    kota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(indikator == 0){
                                indikator++;
                            }
                            else{
                                _progress = new ProgressDialog(Pembayaran.this);
                                _progress.setCancelable(true);
                                _progress.setMessage("Getting data..");
                                _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                _progress.setProgress(0);
                                _progress.setMax(100);
                                _progress.show();
                                new CheckShipping(((City) kota.getSelectedItem()).getSid(),berat).execute();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
                else{
                    /*new AlertDialog.Builder(Pembayaran.this).setTitle("Status Keranjang")
                            .setMessage(json.getString("MESSAGE")).setPositiveButton("OKE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    }).show();*/
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    private class CheckShipping extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        int id;
        double berat;
        public CheckShipping(int id,double berat){
            this.id = id;
            this.berat = berat;
        }
        protected String doInBackground(String... args) {
            String resp = "";
            try {
                String url = _base_url + "m/shipping/" + id + "/" + berat;
                HttpClient request = HttpClient.get(url);
                if (request.ok())
                {
                    resp = request.body();
                }else{
                    resp = "{RESP : 'ERROR' }";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resp;
        }

        protected void onPostExecute(String output) {
            Log.v("keiskeidebug", output);
            JSONObject json = new JSONObject();
            try {
                json = new JSONObject(output);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String resp = null;
            _progress.dismiss();
            try {
                resp = json.getString("RESP");
                if(resp.equals("SCSSHP")){
                    JSONArray ongkir = json.getJSONArray("DATA");
                    int temp = servises.size();
                    for (int i = 0; i < temp; i++){
                        servises.remove(0);
                    }
                    for (int i = 0; i < ongkir.length(); i++){
                        JSONObject tmp = ongkir.getJSONObject(i);
                        servises.add(new Servis(tmp.getString("kurir"),tmp.getString("service"),tmp.getInt("ongkos"),tmp.getString("durasi")));
                    }
                    temp = selectedServis.size();
                    for (int i = 0; i < temp; i++){
                        selectedServis.remove(0);
                    }
                    for(int i = 0; i < servises.size(); i++){
                        if(servises.get(i).getNama_kurir().equals(((Kurir) courier.getSelectedItem()).getName()))
                            selectedServis.add(servises.get(i));
                    }
                    adapter2 = new ArrayAdapter(Pembayaran.this,android.R.layout.simple_spinner_dropdown_item,selectedServis);
                    servis.setAdapter(adapter2);
                }
                else{
                    /*new AlertDialog.Builder(Pembayaran.this).setTitle("Status Keranjang")
                            .setMessage(json.getString("MESSAGE")).setPositiveButton("OKE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    }).show();*/
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public void bayar(View view){
        id_kota = ((City)kota.getSelectedItem()).getSid();
        id_kurir = ((Kurir)courier.getSelectedItem()).getId();
        id_payment = ((Payment)pembayaran.getSelectedItem()).getId();
        _alamat = alamat.getText().toString();
        _kodepos = kodepos.getText().toString();
        _service = ((Servis) servis.getSelectedItem()).getNama_servis();
        _ongkos = String.valueOf(((Servis) servis.getSelectedItem()).getHarga());
        _progress = new ProgressDialog(Pembayaran.this);
        _progress.setCancelable(true);
        _progress.setMessage("Submitting data..");
        _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        _progress.setProgress(0);
        _progress.setMax(100);
        _progress.show();
        new Bayar().execute();
    }
    private class Bayar extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(String... args) {
            String resp = "";
            try {
                String url = _base_url + "m/bayar";
                HttpClient request = HttpClient.post(url);
                request.part("id", userSession.getUserSessionData().getID());
                request.part("id_kota", id_kota);
                request.part("alamat", _alamat);
                request.part("kodepos", _kodepos);
                request.part("id_kurir", id_kurir);
                request.part("service", _service);
                request.part("ongkir", _ongkos);
                request.part("payment", id_payment);
                if (request.ok())
                {
                    resp = request.body();
                }else{
                    resp = "{RESP : 'ERROR' }";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resp;
        }

        protected void onPostExecute(String output) {
            Log.v("keiskeidebug", output);
            JSONObject json = new JSONObject();
            try {
                json = new JSONObject(output);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String resp = null;
            _progress.dismiss();
            try {
                resp = json.getString("RESP");
                if(resp.equals("SCSOUT")){
                    new AlertDialog.Builder(Pembayaran.this).setTitle("Status")
                            .setMessage(json.getString("MESSAGE")).setPositiveButton("OKE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Pembayaran.this,MainActivity.class));
                            Pembayaran.this.finish();
                        }
                    }).show();
                }
                else{
                    /*new AlertDialog.Builder(Pembayaran.this).setTitle("Status Keranjang")
                            .setMessage(json.getString("MESSAGE")).setPositiveButton("OKE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    }).show();*/
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    private class Payment{
        int id;
        String nama,account_name,account_number;

        public Payment(int id, String nama, String account_name, String account_number) {
            this.id = id;
            this.nama = nama;
            this.account_name = account_name;
            this.account_number = account_number;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNama() {
            return nama;
        }

        public void setNama(String nama) {
            this.nama = nama;
        }

        public String getAccount_name() {
            return account_name;
        }

        public void setAccount_name(String account_name) {
            this.account_name = account_name;
        }

        public String getAccount_number() {
            return account_number;
        }

        public void setAccount_number(String account_number) {
            this.account_number = account_number;
        }

        @Override
        public String toString() {
            return getNama()+ " " +getAccount_number() + " - " + getAccount_name();
        }
    }
    private class Kurir{
        int id;
        String code,name;

        public Kurir(int id, String code, String name) {
            this.id = id;
            this.code = code;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return getName();
        }
    }
    private class Servis{
        String nama_kurir,nama_servis;
        int harga;
        String lama;

        public Servis(String nama_kurir, String nama_servis, int harga, String lama) {
            this.nama_kurir = nama_kurir;
            this.nama_servis = nama_servis;
            this.harga = harga;
            this.lama = lama;
        }

        public String getNama_kurir() {
            return nama_kurir;
        }

        public void setNama_kurir(String nama_kurir) {
            this.nama_kurir = nama_kurir;
        }

        public String getNama_servis() {
            return nama_servis;
        }

        public void setNama_servis(String nama_servis) {
            this.nama_servis = nama_servis;
        }

        public int getHarga() {
            return harga;
        }

        public void setHarga(int harga) {
            this.harga = harga;
        }

        public String getLama() {
            return lama;
        }

        public void setLama(String lama) {
            this.lama = lama;
        }

        @Override
        public String toString() {
            return getNama_servis();
        }
    }
    private class Provinsi{
        int id;
        String code,name;

        public Provinsi(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return getName();
        }
    }
}
