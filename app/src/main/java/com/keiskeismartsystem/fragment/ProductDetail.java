package com.keiskeismartsystem.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.keiskeismartsystem.R;
import com.keiskeismartsystem.dbsql.ProductTransact;
import com.keiskeismartsystem.dbsql.WhereHelper;
import com.keiskeismartsystem.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProductDetail.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProductDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductDetail extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Product product;
    ProductTransact productTransact;
    TextView nm_produk,nm_kategori,nom_harga,nm_kode,desc;
    String _base_url = "http://www.smartv2.lapantiga.com/";

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_product_detail, container, false);
        if(product!=null) {
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
            desc.setText(product.getDescription());
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
