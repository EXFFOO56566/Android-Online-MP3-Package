package com.vpapps.onlinemp3;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vpapps.adapter.AdapterAlbum;
import com.vpapps.adapter.AdapterArtist;
import com.vpapps.item.ItemAlbums;
import com.vpapps.item.ItemArtist;
import com.vpapps.utils.Constant;
import com.vpapps.utils.JsonUtils;
import com.vpapps.utils.RecyclerItemClickListener;
import com.vpapps.utils.ZProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentAlbums extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ItemAlbums> arrayList;
    AdapterAlbum adapterAlbum;
    ZProgressHUD progressHUD;
    GridLayoutManager gridLayoutManager;
    TextView textView_empty;
    Button button_try;
    LinearLayout ll_empty;
    String errr_msg;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cat, container, false);

        progressHUD = ZProgressHUD.getInstance(getActivity());
        progressHUD.setMessage(getActivity().getResources().getString(R.string.loading));
        progressHUD.setSpinnerType(ZProgressHUD.FADED_ROUND_SPINNER);

        arrayList = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recyclerView_cat);
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        ll_empty = rootView.findViewById(R.id.ll_empty);
        textView_empty = rootView.findViewById(R.id.textView_empty_msg);
        button_try = rootView.findViewById(R.id.button_empty_try);

        loadAlbums();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FragmentManager fm = getFragmentManager();
                FragmentSongByAlbums f1 = new FragmentSongByAlbums();
                FragmentTransaction ft = fm.beginTransaction();

                Bundle bundl = new Bundle();
                bundl.putString("type", getString(R.string.albums));
                bundl.putString("id", arrayList.get(getPosition(adapterAlbum.getID(position))).getId());
                bundl.putString("name", arrayList.get(getPosition(adapterAlbum.getID(position))).getName());
                f1.setArguments(bundl);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.hide(getFragmentManager().findFragmentByTag(getResources().getString(R.string.albums)));
                ft.add(R.id.fragment, f1, arrayList.get(getPosition(adapterAlbum.getID(position))).getName());
                ft.addToBackStack(arrayList.get(getPosition(adapterAlbum.getID(position))).getName());
                ft.commit();
            }
        }));

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAlbums();
            }
        });

        return rootView;
    }

    private void loadAlbums() {
        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new LoadArtist().execute(Constant.URL_ALBUMS);
        } else {
//            Toast.makeText(getActivity(), getResources().getString(R.string.internet_not_conn), Toast.LENGTH_SHORT).show();
            errr_msg = getString(R.string.internet_not_conn);
            setEmpty();
        }
    }

    private class LoadArtist extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            arrayList.clear();
            ll_empty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            progressHUD.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String json = JsonUtils.getJSONString(strings[0]);
                JSONObject mainJson = new JSONObject(json);
                JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);
                JSONObject objJson = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    objJson = jsonArray.getJSONObject(i);

                    String id = objJson.getString(Constant.TAG_AID);
                    String name = objJson.getString(Constant.TAG_ALBUM_NAME);
                    String image = objJson.getString(Constant.TAG_ALBUM_IMAGE);
                    String thumb = objJson.getString(Constant.TAG_ALBUM_THUMB);

                    ItemAlbums objItem = new ItemAlbums(id, name, image, thumb);
                    arrayList.add(objItem);
                }
                return "1";
            } catch (JSONException e) {
                e.printStackTrace();
                return "0";
            } catch (Exception ee) {
                ee.printStackTrace();
                return "0";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            if (getActivity() != null) {
                if (s.equals("1")) {
                    progressHUD.dismissWithSuccess(getResources().getString(R.string.success));
                    adapterAlbum = new AdapterAlbum(getActivity(), arrayList);
                    recyclerView.setAdapter(adapterAlbum);
                    errr_msg = getString(R.string.no_data_found);
                } else {
                    progressHUD.dismissWithFailure(getResources().getString(R.string.error));
//                    Toast.makeText(getActivity(), getResources().getString(R.string.server_no_conn), Toast.LENGTH_SHORT).show();
                    errr_msg = getString(R.string.server_no_conn);
                }
                setEmpty();
                super.onPostExecute(s);
            }
        }
    }

    public void setEmpty() {
        if(arrayList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            recyclerView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    private int getPosition(String id) {
        int count = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            if (id.equals(arrayList.get(i).getId())) {
                count = i;
                break;
            }
        }
        return count;
    }
}
