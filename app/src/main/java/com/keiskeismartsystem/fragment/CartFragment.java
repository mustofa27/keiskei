package com.keiskeismartsystem.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.keiskeismartsystem.LandingActivity;
import com.keiskeismartsystem.MainActivity;
import com.keiskeismartsystem.R;
import com.keiskeismartsystem.SplashScreen;
import com.keiskeismartsystem.adapter.CartListAdapter;
import com.keiskeismartsystem.adapter.ProductListAdapter;
import com.keiskeismartsystem.helper.ConnectionDetector;
import com.keiskeismartsystem.helper.UserSession;
import com.keiskeismartsystem.library.HttpClient;
import com.keiskeismartsystem.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private List<Product> productList;
    ListView listView;
    UserSession userSession;
    ProgressDialog _progress;
    private static ConnectionDetector _conn;
    CartListAdapter adapter;
    public static final String _base_url = "http://www.smartv2.lapantiga.com/";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userSession = new UserSession(getActivity());
        _conn = new ConnectionDetector(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        listView = (ListView) root.findViewById(R.id.list_cart);
        productList = new ArrayList<Product>();
        if(!_conn.isConnectedToInternet()){
            Toast toast = Toast.makeText(getActivity(),"Tidak ada koneksi internet.", Toast.LENGTH_SHORT);
            toast.show();
        }
        _progress = new ProgressDialog(root.getContext());
        _progress.setCancelable(true);
        _progress.setMessage("Getting data..");
        _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        _progress.setProgress(0);
        _progress.setMax(100);
        _progress.show();
        new LoadCart().execute();
        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private class LoadCart extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(String... args) {
            String resp = "";
            try {
                String url = _base_url + "m/cart/get/" + userSession.getUserSessionData().getID();
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
                Bundle bundle = new Bundle();
                //_productTransact.truncate();
                final List<Product> products = new ArrayList<Product>();
                if(resp.equals("SCSGCRT")){
                    JSONArray city_t = json.getJSONArray("DATA");
                    for (int i = 0; i < city_t.length(); i++){
                        JSONObject tmp = city_t.getJSONObject(i);
                        Product product = new Product();
                        try {
                            product.setSid(Integer.parseInt(tmp.getString("id")));
                            product.setCode(tmp.getString("code"));
                            product.setTitle(tmp.getString("title"));
                            product.setDescription(tmp.getString("description"));
                            product.setPhotoExt(tmp.getString("image"));
                            product.setHarga(tmp.getString("harga"));
                            product.setKategori(tmp.getString("kategori"));
                            product.setJumlah(1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            continue;
                        }
                        productList.add(product);
                    }
                    if(productList.size()!=0){
                        populateList();
                    }
                }
                else{
                    Toast toast = Toast.makeText(getActivity(), "Terjadi kesalahan.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void populateList()
    {
        adapter = new CartListAdapter(getActivity(),productList);
        listView.setAdapter(adapter);
    }
}
