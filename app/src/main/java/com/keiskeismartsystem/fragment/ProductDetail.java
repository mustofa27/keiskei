package com.keiskeismartsystem.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.keiskeismartsystem.R;
import com.keiskeismartsystem.dbsql.ProductTransact;
import com.keiskeismartsystem.dbsql.WhereHelper;
import com.keiskeismartsystem.helper.ConnectionDetector;
import com.keiskeismartsystem.helper.UserSession;
import com.keiskeismartsystem.model.Product;
import com.keiskeismartsystem.socket.AsyncResponse;
import com.keiskeismartsystem.socket.ClientSocket;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProductDetail.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProductDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductDetail extends Fragment implements AsyncResponse {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static ConnectionDetector _conn;
    Product product;
    ProductTransact productTransact;
    TextView nm_produk, nm_kategori, nom_harga, nm_kode, desc;
    String _base_url = "https://keiskei.co.id/";
    Button button;
    private static ProgressDialog _progress;
    private UserSession _user_session;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductDetail newInstance(String param1, String param2) {
        ProductDetail fragment = new ProductDetail();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ProductDetail() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey("id")) {
            productTransact = new ProductTransact(getActivity());
            ArrayList<WhereHelper> whereHelpers = new ArrayList<WhereHelper>();
            whereHelpers.add(new WhereHelper("server_id", String.valueOf(getArguments().getInt("id"))));
            product = productTransact.first(whereHelpers);
            _user_session = new UserSession(getActivity());
            _conn = new ConnectionDetector(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_product_detail, container, false);
        if (product != null) {
            ImageView imageView = (ImageView) rootView.findViewById(R.id.im_product);
            Picasso.with(getActivity()).load(_base_url + product.getPhotoExt())
                    .placeholder(R.drawable.im_picture)
                    .error(R.drawable.im_picture)
                    .into(imageView);
            nm_produk = (TextView) rootView.findViewById(R.id.nm_produk);
            nm_produk.setText(product.getTitle().toString());
            nm_kategori = (TextView) rootView.findViewById(R.id.nm_kategori);
            nm_kategori.setText(product.getKategori());
            nm_kode = (TextView) rootView.findViewById(R.id.nm_kode);
            nm_kode.setText(product.getCode());
            nom_harga = (TextView) rootView.findViewById(R.id.nom_harga);
            nom_harga.setText("RP " + product.getHarga() + ",00");
            desc = (TextView) rootView.findViewById(R.id.desc);
            desc.setText(Html.fromHtml(product.getDescription()));
            button = (Button) rootView.findViewById(R.id.cart);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!_conn.isConnectedToInternet()){
                        Toast toast = Toast.makeText(getActivity(),"Tidak ada koneksi internet.", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                    _progress = new ProgressDialog(view.getContext());
                    _progress.setCancelable(true);
                    _progress.setMessage("Submit..");
                    _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    _progress.setProgress(0);
                    _progress.setMax(100);
                    _progress.show();
                    String[] params = new String[]{ "BUY", String.valueOf(_user_session.getUserSessionData().getID()), String.valueOf(product.getSid())};
                    ClientSocket cs = new ClientSocket(getActivity().getApplicationContext());
                    cs.delegate = ProductDetail.this;
                    cs.execute(params);


                }
            });
            if (!_user_session.isUserLoggedIn()) {
                button.setVisibility(View.GONE);
            }
        }
        return rootView;
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

    @Override
    public void processFinish(String output) {
        JSONObject response = new JSONObject();
        try {
            response = new JSONObject(output);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String resp = "";
        try {
            resp = response.getString("RESP");
            if (resp.equals("SCSCRT")) {
                String data;
                data = response.getString("MESSAGE");
                _progress.dismiss();
                Toast toast = Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT);
                toast.show();
            } else if (resp.equals("FLDCRT")) {
                String data;
                data = response.getString("MESSAGE");
                _progress.dismiss();
                Toast toast = Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT);
                toast.show();
            } else {
                _progress.dismiss();
                Toast toast = Toast.makeText(getActivity(), "Terjadi kesalahan.", Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
