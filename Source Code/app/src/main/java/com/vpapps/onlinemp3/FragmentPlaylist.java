package com.vpapps.onlinemp3;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vpapps.adapter.AdapterPlaylist;
import com.vpapps.interfaces.ClickListenerPlayList;
import com.vpapps.item.ItemPlayList;
import com.vpapps.utils.DBHelper;
import com.vpapps.utils.ZProgressHUD;

import java.util.ArrayList;

public class FragmentPlaylist extends Fragment {

    DBHelper dbHelper;
    RecyclerView recyclerView;
    ArrayList<ItemPlayList> arrayList;
    AdapterPlaylist adapterPlaylist;
    ZProgressHUD progressHUD;
    Button button_addplaylist;
    TextView textView_empty;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playlist, container, false);

        dbHelper = new DBHelper(getActivity());

        progressHUD = ZProgressHUD.getInstance(getActivity());
        progressHUD.setMessage(getActivity().getResources().getString(R.string.loading));
        progressHUD.setSpinnerType(ZProgressHUD.FADED_ROUND_SPINNER);

        arrayList = new ArrayList<>();
        textView_empty = rootView.findViewById(R.id.textView_empty_playlist);
        recyclerView = rootView.findViewById(R.id.recyclerView_playlist);
        button_addplaylist = rootView.findViewById(R.id.button_add_playlist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        arrayList.addAll(dbHelper.loadPlayList());
        adapterPlaylist = new AdapterPlaylist(getActivity(), arrayList, new ClickListenerPlayList() {
            @Override
            public void onClick(int position) {
                FragmentManager fm = getFragmentManager();
                FragmentSongByAlbums f1 = new FragmentSongByAlbums();
                FragmentTransaction ft = fm.beginTransaction();

                Bundle bundl = new Bundle();
                bundl.putString("type", getString(R.string.my_playlist));
                bundl.putString("id", arrayList.get(position).getId());
                bundl.putString("name", arrayList.get(position).getName());
                f1.setArguments(bundl);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.hide(getFragmentManager().findFragmentByTag(getResources().getString(R.string.my_playlist)));
                ft.add(R.id.fragment, f1, arrayList.get(position).getName());
                ft.addToBackStack(arrayList.get(position).getName());
                ft.commit();
            }

            @Override
            public void onItemZero() {
                setEmpty();
            }
        }, false);

        recyclerView.setAdapter(adapterPlaylist);
        setEmpty();

        button_addplaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddPlaylistDialog();
            }
        });

        return rootView;
    }

    private void setEmpty() {
        if (arrayList.size() == 0) {
            textView_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textView_empty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.my_playlist));
        super.onResume();
    }

    private void openAddPlaylistDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getString(R.string.add_playlist));
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.layout_dialog_addplaylist, null);

        final EditText editText = (EditText) subView.findViewById(R.id.editText_playlist_name);
        alert.setView(subView);

        alert.setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!editText.getText().toString().trim().isEmpty()) {
                    arrayList.clear();
                    arrayList.addAll(dbHelper.addPlayList(editText.getText().toString()));
                    Toast.makeText(getActivity(), getString(R.string.playlist_added), Toast.LENGTH_SHORT).show();
                    adapterPlaylist.notifyDataSetChanged();
                    setEmpty();
                }
            }
        });

        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();
    }
}
